package fmp.remoting.transport.netty;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import fmp.common.Addr;
import fmp.common.util.NamedThreadFactory;
import fmp.remoting.ChannelHandler;
import fmp.remoting.RemotingException;
import fmp.remoting.transport.AbstractClient;

public class NettyClient<T> extends AbstractClient<T> {

    private static final ChannelFactory channelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(new NamedThreadFactory("NettyClientBoss", true)), 
                                                                                           Executors.newCachedThreadPool(new NamedThreadFactory("NettyClientWorker", true)), 
                                                                                           50);
    private ClientBootstrap bootstrap;

    public NettyClient( Addr addr, final ChannelHandler<T> handler) throws RemotingException{
        super(addr, handler);
    }

    @Override
    protected void doOpen() throws RemotingException {
        bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("tcpNoDelay", true);
        final NettyHandler<T> nettyHandler = new NettyHandler<>(this.getChannelHandler());
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast( "encoder", new ObjectEncoder() );
                pipeline.addLast( "decoder", new ObjectDecoder() );
                pipeline.addLast("handler", nettyHandler);
                return pipeline;
            }
        });
    }

    protected void doConnect() throws RemotingException {
        ChannelFuture future = bootstrap.connect(addr.getAdd());
        try{
            boolean ret = future.awaitUninterruptibly(50, TimeUnit.MILLISECONDS);
            if (ret && future.isSuccess()) {
                Channel newChannel = future.getChannel();
                newChannel.setInterestOps(Channel.OP_READ_WRITE);
                if (NettyClient.this.isClosed()) {
                    try {
                        newChannel.close();
                    } finally {
                        NettyClient.this.channel.set( null );
                        NettyChannel.remove(newChannel);
                    }
                } else {
                    NettyClient.this.channel.set( NettyChannel.getChannel( newChannel ).orElse( null ) );
                }
            } else if (future.getCause() != null) {
                throw new RemotingException( future.getCause().getMessage(), future.getCause() );
            } else {
                throw new RemotingException( "connect fail", new Exception("connect fail" ));
            }
        }finally{
            if (! isConnected()) {
                future.cancel();
            }
        }
    }

    @Override
    protected void doDisConnect() throws RemotingException {
        channel.ifPresent( t -> t.disconnect() );
    }

    @Override
    protected void doClose() throws RemotingException {
        channel.ifPresent( t -> t.close() );
    }

}
