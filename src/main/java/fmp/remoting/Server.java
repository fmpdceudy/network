package fmp.remoting;

import java.util.Collection;
import java.util.Optional;

import fmp.common.Addr;

public interface Server<T> extends Endpoint<T> {
    Optional<Addr> getBindAddr();
    boolean isBound();
    Collection<Channel> getChannels();
    Optional<Channel> getChannel(Addr remoteAddress);
}
