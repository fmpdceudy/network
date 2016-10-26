package fmp.remoting;

import fmp.common.Addr;

public class RemotingException extends Exception {

    private static final long serialVersionUID = -3160452149606778709L;

    private Addr localAddress;

    private Addr remoteAddress;

    public RemotingException(Channel channel, String msg){
        this(channel == null ? null : channel.getLocalAddress().get(), channel == null ? null : channel.getRemoteAddress().get(),
             msg);
    }

    public RemotingException(Addr localAddress, Addr remoteAddress, String message){
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Channel channel, Throwable cause){
        this(channel == null ? null : channel.getLocalAddress().get(), channel == null ? null : channel.getRemoteAddress().get(),
             cause);
    }

    public RemotingException(Addr localAddress, Addr remoteAddress, Throwable cause){
        super(cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Channel channel, String message, Throwable cause){
        this(channel == null ? null : channel.getLocalAddress().get(), channel == null ? null : channel.getRemoteAddress().get(),
             message, cause);
    }

    public RemotingException(Addr localAddress, Addr remoteAddress, String message,
                             Throwable cause){
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(String message,
                             Throwable cause){
        super(message, cause);
    }

    public Addr getLocalAddress() {
        return localAddress;
    }

    public Addr getRemoteAddress() {
        return remoteAddress;
    }
}
