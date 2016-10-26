package fmp.remoting.transport.netty;

import java.util.Optional;

import fmp.common.Addr;
import fmp.remoting.RemotingException;
import fmp.remoting.transport.AbstractChannel;

final class NettyChannel extends AbstractChannel<org.jboss.netty.channel.Channel> {

    static {
        update( org.jboss.netty.channel.Channel.class, NettyChannel::new,
                ( ch ) -> !ch.isConnected()
                );
    }

    private NettyChannel(org.jboss.netty.channel.Channel channel){
        super( channel );
    }

    @Override
    public Optional<Addr> getLocalAddress() {
        return Optional.ofNullable( channel ).map( org.jboss.netty.channel.Channel::getLocalAddress ).map( Addr::new );
    }

    @Override
    public Optional<Addr> getRemoteAddress() {
        return Optional.ofNullable( channel ).map( org.jboss.netty.channel.Channel::getRemoteAddress ).map( Addr::new );
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    public boolean isClosed() {
        return !channel.isOpen();
    }

    @Override
    public void close() {
        try {
            channel.close().await();
        } catch (Exception e) {
        }
        try {
            remove(channel);
        } catch (Exception e) {
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NettyChannel other = (NettyChannel) obj;
        if (channel == null) {
            if (other.channel != null) return false;
        } else if (!channel.equals(other.channel)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "NettyChannel [channel=" + channel + "]";
    }

    @Override
    public void disconnect() {
        try {
            channel.disconnect().await();
        } catch (Exception e) {
        }
    }

    public void send(Object message) throws RemotingException {
        try {
            if( isConnected() )
                channel.write( message ).await();
        } catch (Exception e) {
            throw new RemotingException( this, e );
        }
    }

    @Override
    public void connect() {
    }

    @Override
    public void open() {
    }

    @Override
    public Addr getAddr() {
        return new Addr( channel.getRemoteAddress() );
    }

}
