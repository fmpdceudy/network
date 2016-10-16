package fmp.remoting;

import java.util.Optional;

import fmp.common.Addr;
import fmp.remoting.util.Connect;

public interface Client<T> extends Endpoint<T>, Connect {
    Optional<Addr> getLocalAddress();
    Optional<Addr> getRemoteAddress();
    void send(T message) throws RemotingException;
}
