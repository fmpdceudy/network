package fmp.remoting.transport.netty;

import java.util.Objects;
import java.util.function.Consumer;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import fmp.remoting.ChannelHandler;
import fmp.remoting.transport.AbstractChannel;

@Sharable
public class NettyHandler<T> extends SimpleChannelHandler {

    private final ChannelHandler<T> handler;

    public NettyHandler(ChannelHandler<T> handler){
        this.handler = Objects.requireNonNull( handler );
    }

    private void check( ChannelHandlerContext ctx, Consumer<AbstractChannel<org.jboss.netty.channel.Channel>> con ) throws Exception {
        AbstractChannel<org.jboss.netty.channel.Channel> channel = NettyChannel.getChannel(ctx.getChannel()).orElseThrow( IllegalStateException::new );
        try {
            con.accept( channel );
            handler.getException();
        } finally {
            NettyChannel.remove(ctx.getChannel());
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        check( ctx, c -> handler.connected( c ) );
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        check( ctx, c-> handler.disconnected( c ) );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        check( ctx, c-> handler.received( c, (T)e.getMessage() ) );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
        check( ctx, c-> handler.sent( c, (T)e.getMessage() ) );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        check( ctx, c-> handler.caught( c, e.getCause() ) );
    }

}
