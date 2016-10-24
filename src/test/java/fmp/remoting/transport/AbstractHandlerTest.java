package fmp.remoting.transport;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import fmp.Base;
import fmp.remoting.Channel;
import fmp.remoting.ChannelHandler;
import fmp.remoting.util.Close;

public class AbstractHandlerTest extends Base {

    @Mock
    private Channel channel;

    @Mock
    @InjectIntoByType
    private Close endpoint;

    @Mock
    @InjectIntoByType
    private ChannelHandler<Object> ch;

    @Mock(answer=Answers.CALLS_REAL_METHODS)
    @TestedObject
    private AbstractHandler<Object> handler;


    @Test
    public void connected() {
        int num = 0;
        int close = 0;
        for (boolean isClosed : Arrays.asList( true, false ) ) {
            when( endpoint.isClosed() ).thenReturn( isClosed );
            handler.connected( channel );
            close++;
            if( !isClosed )
                num++;
            verify( ch, times( num ) ).connected( channel );
            verify( endpoint, times( close ) ).isClosed();
            verifyNoMoreInteractions( ch );
            verifyNoMoreInteractions( endpoint );
        }
    }

    @Test
    public void disconnected() {
        int num = 0;
        for (boolean isClosed : Arrays.asList( true, false ) ) {
            when( endpoint.isClosed() ).thenReturn( isClosed );
            handler.disconnected( channel );
            num++;
            verify( ch, times( num ) ).disconnected( channel );
            verifyNoMoreInteractions( ch );
            verifyZeroInteractions( endpoint );
        }
    }

    @Test
    public void send() {
        int num = 0;
        int close = 0;
        Object msg = new Object();
        for (boolean isClosed : Arrays.asList( true, false ) ) {
            when( endpoint.isClosed() ).thenReturn( isClosed );
            handler.sent( channel, msg );
            close++;
            if( !isClosed )
                num++;
            verify( ch, times( num ) ).sent( channel, msg );
            verify( endpoint, times( close ) ).isClosed();
            verifyNoMoreInteractions( ch );
            verifyNoMoreInteractions( endpoint );
        }
    }

    @Test
    public void received() {
        int num = 0;
        int close = 0;
        Object msg = new Object();
        for (boolean isClosed : Arrays.asList( true, false ) ) {
            when( endpoint.isClosed() ).thenReturn( isClosed );
            handler.received( channel, msg );
            close++;
            if( !isClosed )
                num++;
            verify( ch, times( num ) ).received( channel, msg );
            verify( endpoint, times( close ) ).isClosed();
            verifyNoMoreInteractions( ch );
            verifyNoMoreInteractions( endpoint );
        }
    }

    @Test
    public void caught() {
        int num = 0;
        Throwable ex = new Throwable();
        for (boolean isClosed : Arrays.asList( true, false ) ) {
            when( endpoint.isClosed() ).thenReturn( isClosed );
            handler.caught( channel, ex );
            num++;
            verify( ch, times( num ) ).caught( channel, ex );
            verifyNoMoreInteractions( ch );
            verifyZeroInteractions( endpoint );
        }
    }

    @Test
    public void getException() throws Exception {
        int num = 0;
        for (boolean isClosed : Arrays.asList( true, false ) ) {
            when( endpoint.isClosed() ).thenReturn( isClosed );
            handler.getException();
            num++;
            verify( ch, times( num ) ).getException();
            verifyNoMoreInteractions( ch );
            verifyZeroInteractions( endpoint );
        }
    }
}
