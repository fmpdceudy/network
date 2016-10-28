package fmp.remoting.transport;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import fmp.Base;
import fmp.common.Addr;

public class AbstractChannelTest extends Base {
    @Mock
    T t1, t2,t3;

    @Mock
    K k1, k2, k3;

    @BeforeClass
    public static void setUp() {
        AbstractChannel.update( T.class, U::new, T::canremove );
    }

    @After
    public void teardown() {
        when( t1.canremove() ).thenReturn( true );
        when( t2.canremove() ).thenReturn( true );
        when( t3.canremove() ).thenReturn( true );
        when( k1.canremove() ).thenReturn( true );
        when( k2.canremove() ).thenReturn( true );
        when( k3.canremove() ).thenReturn( true );
        AbstractChannel.autoremove( K.class );
        Assert.assertEquals( 0, AbstractChannel.getChannels( K.class ).size() );
    }

    @Test
    public void testnull() {
        Assert.assertFalse( AbstractChannel.getChannel( null ).isPresent() );
        Assert.assertEquals( 0, AbstractChannel.getChannels( null ).size() );
        Assert.assertFalse( AbstractChannel.getChannel( null, null ).isPresent() );
        AbstractChannel.remove( null );
        AbstractChannel.autoremove( null );
    }

    @Test
    public void other() {
        Assert.assertFalse( AbstractChannel.getChannel( new M() ).isPresent() );
        Assert.assertEquals( 0, AbstractChannel.getChannels( M.class ).size() );
    }

    @Test
    public void remove() {
        U u1 = AbstractChannel.<T,K,U>getChannel( k1 ).get();
        U u2 = AbstractChannel.<T,K,U>getChannel( k1 ).get();
        Assert.assertEquals( u1, u2 );
        Assert.assertEquals( 1, AbstractChannel.getChannels( K.class ).size() );
        Assert.assertEquals( 1, AbstractChannel.getChannels( T.class ).size() );

        when( k1.canremove() ).thenReturn( false );
        AbstractChannel.remove( k1 );
        Assert.assertEquals( 1, AbstractChannel.getChannels( K.class ).size() );

        when( k1.canremove() ).thenReturn( true );
        AbstractChannel.remove( k1 );
        Assert.assertEquals( 0, AbstractChannel.getChannels( K.class ).size() );
    }

    @Test
    public void List() {
        U u1 = AbstractChannel.<T,K,U>getChannel( k1 ).get();
        U u2 = AbstractChannel.<T,K,U>getChannel( k2 ).get();
        U u3 = AbstractChannel.<T,K,U>getChannel( k3 ).get();
        U u4 = AbstractChannel.<T,T,U>getChannel( t1 ).get();
        U u5 = AbstractChannel.<T,T,U>getChannel( t2 ).get();
        U u6 = AbstractChannel.<T,T,U>getChannel( t3 ).get();
        List<U> u = Arrays.asList( u1, u2, u3, u4, u5, u6 );
        Collection<U> g1 = AbstractChannel.getChannels( K.class );
        Collection<U> g2 = AbstractChannel.getChannels( T.class );
        ReflectionAssert.assertReflectionEquals( g1, g2 );
        ReflectionAssert.assertReflectionEquals( g1, u, ReflectionComparatorMode.LENIENT_ORDER );
        Assert.assertEquals( 0, AbstractChannel.getChannels( M.class ).size() );
    }

    @Test
    public void addr() {
        U u1 = AbstractChannel.<T,K,U>getChannel( k1 ).get();
        U u2 = AbstractChannel.<T,K,U>getChannel( k2 ).get();
        Addr addr1 = new Addr( "local", 1 );
        Addr addr2 = new Addr( "local", 2 );
        Addr addr3 = new Addr( "local", 3 );
        when( k1.getAddr() ).thenReturn( addr1 );
        when( k2.getAddr() ).thenReturn( addr2 );
        when( k3.getAddr() ).thenReturn( addr3 );
        when( t1.getAddr() ).thenReturn( addr3 );
        when( t2.getAddr() ).thenReturn( addr3 );
        when( t3.getAddr() ).thenReturn( addr3 );
        U g1 = AbstractChannel.<T,K,U>getChannel( K.class, addr1 ).get();
        U g2 = AbstractChannel.<T,K,U>getChannel( K.class, addr2 ).get();
        Assert.assertEquals( u1, g1 );
        Assert.assertEquals( u2, g2 );

        Assert.assertFalse( AbstractChannel.getChannel( K.class, null ).isPresent() );
        Assert.assertFalse( AbstractChannel.getChannel( M.class, null ).isPresent() );
        Assert.assertFalse( AbstractChannel.getChannel( M.class, addr1 ).isPresent() );
    }
}

class M { }
interface T {
    boolean canremove();
    Addr getAddr();
}
interface K extends T {}
class U extends AbstractChannel<T> {
    public U( T t ) { super( t ); }
    public Optional<fmp.common.Addr> getLocalAddress() { return null; }
    public Optional<fmp.common.Addr> getRemoteAddress() { return null; }
    public void close(){}
    public boolean isClosed() { return false; }
    public void open(){}
    public void connect(){}
    public void disconnect(){}
    public boolean isConnected() { return true; }
    protected Addr getAddr() { return channel.getAddr(); }
    public void write(Object mess) {}
}
