package fmp.remoting.transport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import fmp.Base;
import fmp.common.Addr;
import fmp.common.util.Holder;
import fmp.remoting.Channel;
import fmp.remoting.ChannelHandler;
import fmp.remoting.RemotingException;

public class AbstractClientTest extends Base {

    @Mock
    private Channel channel;

    @Mock
    private RemotingException remoteException;

    @InjectIntoByType
    private Lock connectLock = new ReentrantLock();

    @InjectInto( property="closed" )
    private Holder<Boolean> closed = new Holder<>();

    @InjectInto( property="channel" )
    private Holder<Channel> holder = new Holder<>();

    @Mock
    @InjectIntoByType
    private ChannelHandler<Object> handler;

    @Mock
    @InjectIntoByType
    protected Addr addr;

    @Mock(answer=Answers.CALLS_REAL_METHODS)
    @TestedObject
    private AbstractClient<Object> client;

    @Test
    public void isConnected() {
        closed.set( false );
        when( channel.isConnected() )
            .thenReturn( true )
            .thenReturn( false )
            .thenReturn( true );

        Assert.assertFalse( client.isConnected() );
        holder.set( channel );
        Assert.assertTrue( client.isConnected() );
        Assert.assertFalse( client.isConnected() );
        Assert.assertTrue( client.isConnected() );

        closed.set( true );
        Assert.assertFalse( client.isConnected() );
    }

    private AbstractClient<?> thr( Throwable re ) {
        if( re == null )
            return doNothing().when( client );
        else
            return doThrow( re ).when( client );
    }

    @Test
    public void open() throws RemotingException {
        int open = 0;
        int close = 0;
        int disconnect = 0;
        RuntimeException run = new RuntimeException();
        for (Boolean closebefore : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Throwable doOpen : Arrays.asList( null, remoteException, run) )
        for (Throwable doClose : Arrays.asList( null, remoteException, run) )
        for (Throwable doConnect : Arrays.asList( null, remoteException, run) )
        for (Throwable doDisConnect : Arrays.asList( null, remoteException, run) ) {
            thr( doOpen ).doOpen();
            thr( doClose ).doClose();
            thr( doConnect ).doConnect();
            thr( doDisConnect ).doDisConnect();

            closed.set( closebefore );
            client.open();

            open++;
            if( doOpen != null ) {
                close ++;
                if( !closebefore )
                    disconnect++;
            }
            check( doOpen != null, open, close, 0, disconnect );

        }
    }

    @Test
    public void close() throws RemotingException {
        int close = 0;
        int disconnect = 0;
        RuntimeException run = new RuntimeException();
        for (Boolean closebefore : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Throwable doOpen : Arrays.asList( null, remoteException, run) )
        for (Throwable doClose : Arrays.asList( null, remoteException, run) )
        for (Throwable doConnect : Arrays.asList( null, remoteException, run) )
        for (Throwable doDisConnect : Arrays.asList( null, remoteException, run) ) {
            thr( doOpen ).doOpen();
            thr( doClose ).doClose();
            thr( doConnect ).doConnect();
            thr( doDisConnect ).doDisConnect();

            closed.set( closebefore );
            client.close();

            close++;
            if( !closebefore )
                disconnect++;
            check( true, 0, close, 0, disconnect );

        }
    }

    @Test
    public void connect() throws RemotingException {
        int close = 0;
        int connect = 0;
        int disconnect = 0;
        RuntimeException run = new RuntimeException();
        holder.set( channel );
        for (Boolean closebefore : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Boolean connected : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Throwable doOpen : Arrays.asList( null, remoteException, run) )
        for (Throwable doClose : Arrays.asList( null, remoteException, run) )
        for (Throwable doConnect : Arrays.asList( null, remoteException, run) )
        for (Throwable doDisConnect : Arrays.asList( null, remoteException, run) ) {
            thr( doOpen ).doOpen();
            thr( doClose ).doClose();
            thr( doConnect ).doConnect();
            thr( doDisConnect ).doDisConnect();
            when( channel.isConnected() ).thenReturn( connected );

            closed.set( closebefore );
            client.connect();

            if( !connected && !closebefore ) {
                connect ++;
                if( doConnect != null ) {
                    close ++;
                    if( !closebefore )
                        disconnect++;
                }
            }
            check( !connected && doConnect != null || closebefore, 0, close, connect, disconnect );
        }
    }

    @Test
    public void disconnect() throws RemotingException {
        int disconnect = 0;
        RuntimeException run = new RuntimeException();
        for (Boolean closebefore : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Throwable doOpen : Arrays.asList( null, remoteException, run) )
        for (Throwable doClose : Arrays.asList( null, remoteException, run) )
        for (Throwable doConnect : Arrays.asList( null, remoteException, run) )
        for (Throwable doDisConnect : Arrays.asList( null, remoteException, run) ) {
            thr( doOpen ).doOpen();
            thr( doClose ).doClose();
            thr( doConnect ).doConnect();
            thr( doDisConnect ).doDisConnect();

            closed.set( closebefore );
            client.disconnect();

            if( !closebefore )
                disconnect ++;
            check( closebefore, 0, 0, 0, disconnect );
        }
    }

    private void check( boolean closed, int doopen, int doClose, int doConnect, int doDisConnect ) throws RemotingException {
        Assert.assertEquals( closed, client.isClosed() );
        verify( client, times( doopen ) ).doOpen();
        verify( client, times( doClose ) ).doClose();
        verify( client, times( doConnect ) ).doConnect();
        verify( client, times( doDisConnect ) ).doDisConnect();
        verify( channel, times(0)).write(any(String.class));
    }

    @Test
    public void send() throws RemotingException {
        closed.set( false );
        holder.set( channel );
        when( channel.isConnected() ).thenReturn( false ).thenReturn( true );
        client.send( "aaaa" );
        verify( channel, times( 1) ).write( "aaaa" );
        client.send( "aaaa" );
        verify( channel, times( 2) ).write( "aaaa" );
    }

    @Test
    public void getAddr() {
        when( channel.getRemoteAddress() ).thenReturn( Optional.of( addr ) );
        when( channel.getLocalAddress() ).thenReturn( Optional.of( addr ) );
        Assert.assertEquals( false, client.getRemoteAddress().isPresent() );
        Assert.assertEquals( false, client.getLocalAddress().isPresent() );
        holder.set( channel );
        Assert.assertEquals( addr, client.getRemoteAddress().get() );
        Assert.assertEquals( addr, client.getLocalAddress().get() );
        when( channel.getRemoteAddress() ).thenReturn( Optional.empty() );
        when( channel.getLocalAddress() ).thenReturn( Optional.empty() );
        Assert.assertEquals( false, client.getRemoteAddress().isPresent() );
        Assert.assertEquals( false, client.getLocalAddress().isPresent() );
    }
}
