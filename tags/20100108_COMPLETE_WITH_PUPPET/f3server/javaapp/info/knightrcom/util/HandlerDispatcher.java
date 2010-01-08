package info.knightrcom.util;

import info.knightrcom.command.handler.F3ServerInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.HibernateTransactionSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandlerDispatcher {

    private static final Log log = LogFactory.getLog(HandlerDispatcher.class);

    /**
     * 根据消息的命令签名，将消息派发给句柄的具体方法
     * 
     * @param <T>
     * @param handler
     * @param signature
     * @param message
     * @throws Exception
     */
    public static <T extends F3ServerMessage> void dispatchMessage(
            F3ServerInMessageHandler handler, String signature, T message) throws Exception {
        Method method = handler.getMethod(handler.getClass().getName().toString().concat(".").concat(signature));
        String methodFullName = method.getDeclaringClass().getName().toString().concat(".").concat(method.getName());
        log.debug("进入方法:\n" + methodFullName);
        log.debug("消息内容: " + message.getContent());
        if (method.isAnnotationPresent(HibernateTransactionSupport.class)) {
            log.debug("已经启动Hibernate事务支持功能");
            try {
                HibernateSessionFactory.getSession().beginTransaction();
                method.invoke(handler, message.getCurrentSession(), message, message.getEchoMessage());
                HibernateSessionFactory.getSession().getTransaction().commit();
            } catch (Exception ex) {
                HibernateSessionFactory.getSession().getTransaction().rollback();
                throw new RuntimeException(ex);
            } finally {
                HibernateSessionFactory.closeSession();
            }
        } else {
            log.debug("没有启动Hibernate事务支持功能");
            method.invoke(handler, message.getCurrentSession(), message, message.getEchoMessage());
        }
        log.debug("退出方法:\n" + methodFullName);
    }

    /**
     * 构造回馈消息
     * 
     * @param message
     * @return
     */
    public static String respondMessage(EchoMessage message) {
        return String.format("%1$s~%2$s~%3$s~%4$s", 
                message.getType(),
                message.getNumber(), 
                message.getResult(), 
                message.getContent());
    }

    /**
     * 初始化句柄方法
     * 
     * @param handler
     */
    public static void initMethodPoolForHandler(F3ServerInMessageHandler handler) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isVolatile(method.getModifiers())) {
                continue;
            }
            log.debug(handler.getClass().getName().toString().concat(".").concat(method.getName()));
            handler.addMethod(handler.getClass().getName().toString().concat(".").concat(method.getName()), method);
        }
    }

}
