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
    public void PLATFORM_REQUEST_ENVIRONMENT(IoSession session, PlatformMessage message, EchoMessage echoMessage) throws Exception {
        echoMessage.setResult(PLATFORM_ENVIRONMENT_INIT);
        echoMessage.setContent(ModelUtil.getModelDesc());
        sessionWrite(session, echoMessage);
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

    public static final String GAME_INTERRUPTED = "GAME_INTERRUPTED";
// private boolean isChatUser(String name) {
//        return players.contains(name);
//    }
//
//    private int getNumberOfUsers() {
//        return players.size();
//    }
//
//    private void kick(String name) {
//        synchronized (sessions) {
//            for (IoSession session : sessions) {
//                if (name.equals(session.getAttribute("user"))) {
//                    session.close(true);
//                    break;
//                }
//            }
//        }
//    }

}
