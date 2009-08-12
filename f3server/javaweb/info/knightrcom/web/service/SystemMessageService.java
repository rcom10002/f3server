package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.session.IoSession;

/**
 *
 */
public class SystemMessageService extends F3SWebServiceAdaptor<Object> {

	/**
	 * 
	 * 向游戏端的玩家发送系统消息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String SEND_SYSTEM_MESSAGE(HttpServletRequest request, HttpServletResponse response) {
		String notification = request.getParameter("MESSAGE_CONTENT");
		int success = 0;
		int failed = 0;
		for (IoSession session : F3ServerProxy.getAllSession()) {
			try {
				EchoMessage echoMessage = F3ServerMessage.createInstance(MessageType.PLATFORM).getEchoMessage();
				echoMessage.setContent(notification);
				echoMessage.setResult("PLATFORM_" + request.getParameter("MESSAGE_TYPE") + "_MESSAGE_BROADCASTED");
				F3ServerProxy.sessionWrite(session, echoMessage);
				success++;
			} catch (Exception e) {
				failed++;
			}
		}
		F3SWebServiceResult result = failed > 0 ? F3SWebServiceResult.WARNING : F3SWebServiceResult.SUCCESS;
		if (F3ServerProxy.getAllSession().size() != 0 && failed == F3ServerProxy.getAllSession().size()) {
			result = F3SWebServiceResult.FAIL;
		}
		return toXML(createEntityInfo(new Object(), result));
	}
}
