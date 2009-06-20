package info.knightrcom.command.handler;

import static info.knightrcom.util.HandlerDispatcher.dispatchMessage;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.HandlerDispatcher;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;

/**
 * F3Server消息控制句柄
 */
public abstract class F3ServerInMessageHandler implements MessageHandler<F3ServerMessage> {

    protected static final Log log = LogFactory.getLog(F3ServerInMessageHandler.class);

    public static final String NO_ECHO_RESULT = "NO_ECHO_RESULT";

    private Map<String, Method> methodPool = new HashMap<String, Method>();

    /**
     * @return the methodPool
     */
    public Method getMethod(String signature) {
        return methodPool.get(signature);
    }

    /**
     * @param methodPool
     *            the methodPool to set
     */
    public void addMethod(String signature, Method method) {
        if (methodPool.containsKey(signature)) {
            throw new RuntimeException(String.format("方法签名[%s]已经存在于方法池中！", signature));
        }
        methodPool.put(signature, method);
    }

    /**
     * @param session
     * @param message
     * @throws Exception
     */
    public void handleMessage(IoSession session, F3ServerMessage message) throws Exception {
        dispatchMessage(this, message.getSignature(), message);
    }

    /**
     * @param session
     * @param echoMessage
     */
    protected void sessionWrite(IoSession session, EchoMessage echoMessage) {
        if (!NO_ECHO_RESULT.equals(echoMessage.getResult())) {
            session.write(EncryptionUtil.Base64Encode(HandlerDispatcher.respondMessage(echoMessage)));
        } else {
            log.warn("警告：服务器处理结束，但未向客户端发送任何回馈结果！");
        }
    }

}
