package info.knightrcom;

import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.HandlerDispatcher;
import info.knightrcom.util.SystemLogger;

import java.util.Collection;

import org.apache.mina.core.session.IoSession;

/**
 * 该类作为Web访问应用服务器的代理，具体的应用操作内容由代理完成
 */
public class F3ServerProxy {

    /**
     * 日志类型
     */
    public static enum LogType {
        CLIENT_ERROR, SYSTEM_ERROR, SYSTEM_LOG
    }

	/**
	 * 获取所有Socket会话
	 * 
	 * @return
	 */
	public static Collection<IoSession> getAllSession() {
		return F3Server.acceptor.getManagedSessions().values();
	}

	/**
	 * 发布消息
	 * 
	 * @param session
	 * @param echoMessage
	 */
	public static void sessionWrite(IoSession session, EchoMessage echoMessage) {
		session.write(EncryptionUtil.Base64Encode(HandlerDispatcher.respondMessage(echoMessage)));
	}

	/**
	 * 启动游戏服务器
	 */
	public static void startServer() {
		F3Server.startServer(null);
	}

	/**
	 * 停止游戏服务器
	 */
	public static void stopServer() {
		F3Server.shutdownServer();
	}

	/**
	 * @return
	 */
	public static boolean isServerRunning() {
		return F3Server.isRunning();
	}

	/**
	 * 获取服务器运行状态
	 * 
	 * @return
	 */
	public static Object getServerStatus() {
		final String[] titles = new String[] {
				"USE_SSL",
				"PORT",
				"SECURITY_CONFIGURATION",
				"MAX_CONNECTION_LIMIT",
				"RUNNING",
				"USER_ONLINE"};
		final Object[] params = new Object[] {
				F3Server.USE_SSL, 
				F3Server.PORT,
				F3Server.SECURITY_CONFIGURATION, 
				F3Server.MAX_CONNECTION_LIMIT,
				F3Server.RUNNING, 
				F3Server.acceptor.getManagedSessionCount() };
		return new Object[] {titles, params};
	}

	/**
	 * 创建日志信息
	 * 
	 * @param caption
	 * @param message
	 * @param info
	 * @param type
	 * @return
	 */
	public static LogInfo createLogInfo(String caption, String message, String info, LogType type) {
	    return SystemLogger.createLog(caption, message, info, type);
	}
}
