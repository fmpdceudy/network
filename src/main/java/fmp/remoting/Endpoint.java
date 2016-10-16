package fmp.remoting;

import fmp.remoting.util.Close;

public interface Endpoint<T> extends Close {
    ChannelHandler<T> getChannelHandler();
}
