package fmp.remoting.transport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.unitils.reflectionassert.ReflectionAssert;

import fmp.Base;
import fmp.remoting.ChannelHandler;

public class AbstractEndpointTest extends Base {

    @Mock
    private ChannelHandler<Object> handler;

    private AbstractEndpoint<Object> endpoint;

    @Before
    public void setUp() {
        endpoint = new AbstractEndpoint<Object>( handler );
    }

    @Test
    public void getChannelHandler() {
        ReflectionAssert.assertReflectionEquals( new AbstractHandler<>( handler, endpoint ), endpoint.getChannelHandler() );
    }

    @Test
    public void close() {
        Assert.assertTrue( endpoint.isClosed() );
        endpoint.open();
        Assert.assertFalse( endpoint.isClosed() );
        endpoint.close();
        Assert.assertTrue( endpoint.isClosed() );
    }

    @Test
    public void initnull() {
        endpoint = null;
        try {
            endpoint = new AbstractEndpoint<Object>( null );
        } catch (Exception e) {
        }
        Assert.assertNull( endpoint );
    }
}
