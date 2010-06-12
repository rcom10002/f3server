package info.knightrcom.command.handler;

import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.util.ModelUtil;

import org.apache.mina.core.session.IoSession;

/**
 * 平台消息控制句柄
 */
public class PlatformInMessageHandler extends F3ServerInMessageHandler {

    public static final String PLATFORM_ENVIRONMENT_INIT = "PLATFORM_ENVIRONMENT_INIT";
    public static final String LOGIN_CLIENT_VERSION_LIMIT = "LOGIN_CLIENT_VERSION_LIMIT";
    public void PLATFORM_REQUEST_ENVIRONMENT(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        echoMessage.setResult(PLATFORM_ENVIRONMENT_INIT);
        echoMessage.setContent(ModelUtil.getModelDesc());
        sessionWrite(session, echoMessage);
    }
    
    public void CLIENT_VERSION_VALIDATE(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception{
    	String results = message.getContent();
    	String userClientVersion = results.replaceAll("^.*\\((.*?)\\).*$", "$1");
        if (!userClientVersion.matches(ModelUtil.getSystemParameter("ALLOWED_GAME_CLIENT_VERSION", "^.*$"))) {
        	// 客户端版本限制
        	echoMessage.setResult(LOGIN_CLIENT_VERSION_LIMIT);
            sessionWrite(session, echoMessage);
            return;
        }
    }

    public void PLATFORM_IDLE_ECHO(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        // this method is used to get echo message from client so that server knows the client is still alive and action for session closing will be canceled.
        session.setAttribute("ALIVE", "yes"); // refer to the implementation of sessionIdle in F3ServerServiceHandler
    }

    // 玩家掉线
    public static final String PLATFORM_PLAYER_LOST_CONNECTION = "PLATFORM_PLAYER_LOST_CONNECTION";
    public void PLATFORM_PLAYER_LOST_CONNECTION(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        // 掉线
    }

    // 刷新房间信息
    public static final String PLATFORM_REFRESH_ROOM_INFO = "PLATFORM_REFRESH_ROOM_INFO";
    public void PLATFORM_REFRESH_ROOM_INFO(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        // 刷新房间信息
    }

    public void SYS_MESSAGE(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        // 刷新房间信息
    }
    
    public void PLAYER_INFO(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        // 刷新房间信息
    }

    // FIXME Not all following declarations are used.
    public static final String SERVER_IDLE_TEST = "SERVER_IDLE_TEST";
    public static final String GAME_INTERRUPTED = "GAME_INTERRUPTED";
    public static final String PLATFORM_IP_CONFLICT = "PLATFORM_IP_CONFLICT";

}
