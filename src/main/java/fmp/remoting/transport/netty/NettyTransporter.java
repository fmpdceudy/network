package fmp.remoting.transport.netty;

import fmp.common.Addr;
import fmp.remoting.ChannelHandler;
import fmp.remoting.Client;
import fmp.remoting.RemotingException;
import fmp.remoting.Server;
import fmp.remoting.Transporter;

public class NettyTransporter<T> implements Transporter<T> {

    public static final String NAME = "netty";

    public Server<T> bind(Addr addr, ChannelHandler<T> listener) throws RemotingException {
        return new NettyServer<>(addr, listener);
    }

    public Client<T> connect(Addr addr, ChannelHandler<T> listener) throws RemotingException {
        return new NettyClient<>(addr, listener);
    }
}
