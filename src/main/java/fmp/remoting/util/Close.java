package fmp.remoting.util;

public interface Close {
    boolean isClosed();
    void close();
    void open();
}
