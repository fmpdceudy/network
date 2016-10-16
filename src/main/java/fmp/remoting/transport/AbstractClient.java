package fmp.remoting.transport;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fmp.common.Addr;
import fmp.remoting.Channel;
import fmp.remoting.ChannelHandler;
import fmp.remoting.Client;
import fmp.remoting.RemotingException;

public abstract class AbstractClient<T> extends AbstractEndpoint<T> implements Client<T> {

    private final Lock connectLock = new ReentrantLock();

    protected final Addr addr;

    public AbstractClient( Addr addr, ChannelHandler<T> handler) {
        super(handler);
        this.addr = addr;
        open();
    }

    @Override
    public final void open() {
        try {
            doOpen();
            super.open();
        } catch (Exception e) {
            close();
        }
    }

    @Override
    public final void close() {
        disconnect();
        super.close();
        try {
            doClose();
        } catch (Exception e) {
        }
    }

    @Override
    public final boolean isConnected() {
        return !isClosed() && getChannel().map( Channel::isConnected ).orElse( Boolean.FALSE );
    }

    @Override
    public final void connect() {
        try {
            connectLock.lock();
            if (isConnected() || isClosed() ) {
                return;
            }
            doConnect();
        } catch (Exception e) {
            close();
        } finally {
            connectLock.unlock();
        }
    }

    @Override
    public final void disconnect() {
        try {
            connectLock.lock();
            if( isClosed() )
                return;
            doDisConnect();
        } catch (Exception e) {
        } finally {
            connectLock.unlock();
        }
    }

    @Override
    public final Optional<Addr> getLocalAddress() {
        return getChannel().map( Channel::getLocalAddress ).filter( Optional::isPresent ).map( Optional::get );
    }

    @Override
    public final Optional<Addr> getRemoteAddress() {
        return getChannel().map( Channel::getRemoteAddress ).filter( Optional::isPresent ).map( Optional::get );
    }

    @Override
    public final void send(T message) throws RemotingException {
        if (!isConnected()) {
            return;
        }
        doSend( message );
    }

    protected abstract void doOpen() throws RemotingException;
    protected abstract void doClose() throws RemotingException;
    protected abstract void doConnect() throws RemotingException;
    protected abstract void doDisConnect() throws RemotingException;
    protected abstract void doSend( T message) throws RemotingException;
    protected abstract Optional<Channel> getChannel();

}
