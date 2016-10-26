package fmp.remoting.transport.netty;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import fmp.common.Addr;
import fmp.common.util.NamedThreadFactory;
import fmp.remoting.ChannelHandler;
import fmp.remoting.RemotingException;
import fmp.remoting.Server;
import fmp.remoting.transport.AbstractServer;

public class NettyServer<T> extends AbstractServer<T> implements Server<T> {

    private ServerBootstrap                 bootstrap;

    private Channel channel;

    public NettyServer(Addr addr, ChannelHandler<T> handler) throws RemotingException{
        super(addr,handler);
    }

    @Override
    protected void doOpen() throws RemotingException {
        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerBoss", true));
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerWorker", true));
        ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker, 5);
        bootstrap = new ServerBootstrap(channelFactory);

        final NettyHandler<T> nettyHandler = new NettyHandler<>(this.getChannelHandler());
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast( "encoder", new ObjectEncoder() );
                pipeline.addLast( "decoder", new ObjectDecoder() );
                pipeline.addLast( "handler", nettyHandler);
                return pipeline;
            }
        });
        channel = bootstrap.bind( getBindAddr().get().getAdd());
    }

    public Optional<Addr> getBindAddr() {
        return Optional.ofNullable( addr );
    }

    @Override
    protected void doClose() throws RemotingException {
        try {
            if (channel != null) {
                channel.unbind();
                channel.close();
            }
        } catch (Throwable e) {
        }
        try {
            getChannels().forEach( fmp.remoting.Channel::close );
        } catch (Throwable e) {
        }
        try {
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
            }
        } catch (Throwable e) {
        }
    }

    public boolean isBound() {
        return channel.isBound();
    }

    @Override
    public Optional<fmp.remoting.Channel> getChannel(Addr remoteAddress) {
        return NettyChannel.getChannel( Channel.class, remoteAddress ).map( t -> t );
    }

    @Override
    public Collection<fmp.remoting.Channel> getChannels() {
        return NettyChannel.getChannels( Channel.class ).stream().map( t -> t ).collect( Collectors.toList() );
    }
}
