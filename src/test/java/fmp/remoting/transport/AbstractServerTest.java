package fmp.remoting.transport;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import fmp.Base;
import fmp.common.util.Holder;
import fmp.remoting.RemotingException;

public class AbstractServerTest extends Base {

    @Mock
    private RemotingException remoteException;

    @InjectIntoByType
    private Holder<Boolean> closed = new Holder<Boolean>();

    @Mock(answer=Answers.CALLS_REAL_METHODS)
    @TestedObject
    private AbstractServer<Object> server;

    private AbstractServer<?> thr( Throwable re ) {
        if( re == null )
            return doNothing().when( server );
        else
            return doThrow( re ).when( server );
    }

    private void check( boolean closed, int doopen, int doClose ) throws RemotingException {
        Assert.assertEquals( closed, server.isClosed() );
        verify( server, times( doopen ) ).doOpen();
        verify( server, times( doClose ) ).doClose();
    }

    @Test
    public void open() throws RemotingException {
        int open = 0;
        int close = 0;
        RuntimeException run = new RuntimeException();
        for (Boolean closebefore : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Throwable doOpen : Arrays.asList( null, remoteException, run) )
        for (Throwable doClose : Arrays.asList( null, remoteException, run) ) {
            thr( doOpen ).doOpen();
            thr( doClose ).doClose();

            closed.set( closebefore );
            server.open();

            open++;
            if( doOpen != null ) {
                close ++;
            }
            check( doOpen != null, open, close );
        }
    }

    @Test
    public void close() throws RemotingException {
        int close = 0;
        RuntimeException run = new RuntimeException();
        for (Boolean closebefore : Arrays.asList( Boolean.TRUE, Boolean.FALSE) )
        for (Throwable doOpen : Arrays.asList( null, remoteException, run) )
        for (Throwable doClose : Arrays.asList( null, remoteException, run) ) {
            thr( doOpen ).doOpen();
            thr( doClose ).doClose();

            closed.set( closebefore );
            server.close();

            close ++;

            check( true, 0, close );
        }
    }
}
