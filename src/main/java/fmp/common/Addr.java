package fmp.common;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;

public class Addr {
    // The hostname of the Socket Address
    private final String hostname;
    // The IP address of the Socket Address
    private final InetAddress addr;
    // The port number of the Socket Address
    private final int port;

    private final SocketAddress socket;

    public Addr(InetAddress addr, int port) {
        hostname = null;
        this.addr = addr;
        this.port = port;
        this.socket = null;
    }

    public Addr(String host, Integer port ) {
        this.hostname = host;
        this.port = port;
        addr = null;
        this.socket = null;
    }

    public Addr( SocketAddress addr ) {
        this.hostname = null;
        this.port = 0;
        this.addr = null;
        this.socket = addr;
    }

    public Addr( URL addr ) {
        hostname = addr.getHost();
        port = addr.getPort();
        this.addr = null;
        this.socket = null;
    }

    public SocketAddress getAdd() {
        if( hostname != null )
            return new InetSocketAddress( hostname, port );
        if( addr == null )
            return new InetSocketAddress( addr, port );
        return socket;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return addr;
    }

    public String getHostName() {
        if (hostname != null)
            return hostname;
        if (addr != null)
            return addr.getHostName();
        return null;
    }

    public String getHostString() {
        if (hostname != null)
            return hostname;
        if (addr != null) {
            return addr.getHostAddress();
        }
        return null;
    }

    public boolean isUnresolved() {
        return addr == null;
    }

    @Override
    public String toString() {
        if (isUnresolved()) {
            return hostname + ":" + port;
        } else {
            return addr.toString() + ":" + port;
        }
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Addr))
            return false;
        Addr that = (Addr)obj;
        boolean sameIP;
        if (addr != null)
            sameIP = addr.equals(that.addr);
        else if (hostname != null)
            sameIP = (that.addr == null) &&
                hostname.equalsIgnoreCase(that.hostname);
        else
            sameIP = (that.addr == null) && (that.hostname == null);
        return sameIP && (port == that.port);
    }

    @Override
    public final int hashCode() {
        if (addr != null)
            return addr.hashCode() + port;
        if (hostname != null)
            return hostname.toLowerCase().hashCode() + port;
        return port;
    }
}
