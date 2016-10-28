package fmp.remoting;

import java.util.Optional;

import fmp.common.Addr;
import fmp.remoting.util.Close;
import fmp.remoting.util.Connect;

public interface Channel extends Close, Connect  {
    Optional<Addr> getLocalAddress();
    Optional<Addr> getRemoteAddress();
    void write( Object message );
}
