package fmp.remoting.transport;

import fmp.common.Addr;
import fmp.remoting.ChannelHandler;
import fmp.remoting.RemotingException;
import fmp.remoting.Server;

public abstract class AbstractServer<T> extends AbstractEndpoint<T> implements Server<T> {

    protected final Addr addr;

    public AbstractServer( Addr addr, ChannelHandler<T> handler) {
        super(handler);
        this.addr = addr;
        open();
    }

    @Override
    public final void open() {
        try {
            doOpen();
            super.open();
        } catch (Exception t) {
            close();
        }
    }

    @Override
    public final void close() {
        super.close();
        try {
            doClose();
        } catch (Exception e) {
        }
    }

    protected abstract void doOpen() throws RemotingException;
    protected abstract void doClose() throws RemotingException;

}
