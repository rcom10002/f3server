package info.knightrcom;

import java.util.Collection;

import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.HandlerDispatcher;

import org.apache.mina.core.session.IoSession;

/**
 * 该类作为Web访问应用服务器的代理，具体的应用操作内容由代理完成
 */
public class F3ServerProxy {

	/**
	 * @return
	 */
	public static Collection<IoSession> getAllSession() {
		return F3Server.acceptor.getManagedSessions().values();
	}

	/**
	 * @param session
	 * @param echoMessage
	 */
	public static void sessionWrite(IoSession session, EchoMessage echoMessage) {
		session.write(EncryptionUtil.Base64Encode(HandlerDispatcher.respondMessage(echoMessage)));
	}

	/**
	 * 
	 */
	public static void startServer() {
		F3Server.startServer(null);
	}

	/**
	 * 
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
}
