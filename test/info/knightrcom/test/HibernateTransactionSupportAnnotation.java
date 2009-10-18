package info.knightrcom.test;

import info.knightrcom.command.handler.game.Red5GameInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.game.Red5GameMessage;
import info.knightrcom.data.HibernateTransactionSupport;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.mina.core.session.IoSession;

public class HibernateTransactionSupportAnnotation extends TestCase {

    public void testAnnotation() throws SecurityException, NoSuchMethodException {
        Method annotatedMethod = Red5GameInMessageHandler.class.getMethod("test", IoSession.class, Red5GameMessage.class, EchoMessage.class);
        assertTrue(annotatedMethod.isAnnotationPresent(HibernateTransactionSupport.class));
    }
}
