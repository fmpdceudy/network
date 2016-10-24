package fmp.remoting.transport;

import java.util.Objects;

import fmp.remoting.Channel;
import fmp.remoting.ChannelHandler;
import fmp.remoting.RemotingException;
import fmp.remoting.util.Close;

class AbstractHandler<T> implements ChannelHandler<T> {

    private final ChannelHandler<T> handler;
    private final Close endpoint;

    protected AbstractHandler( ChannelHandler<T> handler, Close endpoint) {
        this.handler = Objects.requireNonNull( handler );
        this.endpoint = Objects.requireNonNull( endpoint );
    }

    @Override
    public final void connected(Channel ch) {
        if (endpoint.isClosed())
            return;
        handler.connected(ch);
    }

    @Override
    public final void disconnected(Channel ch) {
        handler.disconnected(ch);
    }

    @Override
    public final void sent(Channel ch, T msg) {
        if (endpoint.isClosed())
            return;
        handler.sent(ch, msg);
    }

    @Override
    public final void received(Channel ch, T msg) {
        if (endpoint.isClosed())
            return;
        handler.received(ch, msg);
    }

    @Override
    public final void caught(Channel ch, Throwable ex) {
        handler.caught(ch, ex);
    }

    @Override
    public final void getException() throws RemotingException {
        handler.getException();
    }
}
