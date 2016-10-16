package fmp.remoting;

public interface ChannelHandler<T> {
    void connected(Channel channel);
    void disconnected(Channel channel);
    void sent(Channel channel, T message);
    void received(Channel channel, T message);
    void caught(Channel channel, Throwable exception);
    void getException() throws RemotingException;
}
