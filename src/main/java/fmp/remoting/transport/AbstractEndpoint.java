package fmp.remoting.transport;

import java.util.Objects;

import fmp.common.util.Holder;
import fmp.remoting.ChannelHandler;
import fmp.remoting.Endpoint;

public class AbstractEndpoint<T> implements Endpoint<T> {

    private final ChannelHandler<T> handler;

    private final Holder<Boolean> closed;

    public AbstractEndpoint(ChannelHandler<T> handler) {
        this.handler = new AbstractHandler<T>(
            Objects.requireNonNull( handler ),
            this );
        closed = new Holder<>();
        closed.set( true );
    }

    @Override
    public final ChannelHandler<T> getChannelHandler() {
        return  handler;
    }

    @Override
    public void open() {
        closed.set( false );
    }

    @Override
    public void close() {
        closed.set( true );
    }

    @Override
    public final boolean isClosed() {
        return closed.get();
    }

}
