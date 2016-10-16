package fmp.remoting;

import fmp.common.Addr;

public interface Transporter<T> {
    Server<T> bind(Addr addr, ChannelHandler<T> handler) throws RemotingException;
    Client<T> connect(Addr addr, ChannelHandler<T> handler) throws RemotingException;
}
