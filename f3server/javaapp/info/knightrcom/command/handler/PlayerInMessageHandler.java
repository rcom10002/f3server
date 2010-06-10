package info.knightrcom.command.handler;

import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.PlayerMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.HibernateTransactionSupport;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.model.global.GameStatus;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.util.SystemLogger;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * 玩家消息控制句柄
 */
public class PlayerInMessageHandler extends F3ServerInMessageHandler {

    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String LOGIN_ERROR_USERNAME_OR_PASSWORD = "LOGIN_ERROR_USERNAME_OR_PASSWORD";
    public static final String LOGIN_USER_ALREADY_ONLINE = "LOGIN_USER_ALREADY_ONLINE";
    public static final String LOGIN_MAX_CONNECTION_LIMIT = "LOGIN_MAX_CONNECTION_LIMIT";
    public static final String LOGIN_IP_CONFLICT = "LOGIN_IP_CONFLICT";
    @HibernateTransactionSupport
    public synchronized void LOGIN_SIGN_IN(IoSession session, PlayerMessage message, EchoMessage echoMessage) { // FIXME This synchronized should be considered with more details.
    	Set<IoSession> sessions = ModelUtil.getSessions();
    	if (Boolean.parseBoolean(ModelUtil.getSystemParameter("IP_CONFLICT_ENABLED"))) {
    	    // 同IP登录限制
        	synchronized (sessions) {
        		int count = 0;
    			Iterator<IoSession> itr = sessions.iterator();
    			while (itr.hasNext()) {
    				String currentIP = session.getRemoteAddress().toString().replaceAll("^.*?(\\d+\\.\\d+\\.\\d+\\.\\d+).*$", "$1");
    				String remoteIP = itr.next().getRemoteAddress().toString().replaceAll("^.*?(\\d+\\.\\d+\\.\\d+\\.\\d+).*$", "$1");
    				if (currentIP.equals(remoteIP)) {
    					count++;
    					if (count > 1) {
    				    	echoMessage = PlayerMessage.createInstance(MessageType.PLAYER).getEchoMessage();
    						echoMessage.setResult(LOGIN_IP_CONFLICT);
    						sessionWrite(session, echoMessage);
    						return;
    					}
    				}
    			}
    		}
        }
        String results[] = message.getContent().split("~");
        PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(
                PlayerProfile.class).add(
                        Restrictions.eq("userId", results[0])).add(
                        Restrictions.eq("password", EncryptionUtil.encryptSHA(results[1]))).add(
                        Restrictions.ne("role", "Administrator")).add(
                        Restrictions.or(Restrictions.eq("status", "1"), Restrictions.like("status", "puppet", MatchMode.START))).uniqueResult();
        if (profile == null) {
            // 用户名或密码错误
            echoMessage.setResult(LOGIN_ERROR_USERNAME_OR_PASSWORD);
            sessionWrite(session, echoMessage);
            return;
        }

        synchronized (sessions) {
            Iterator<IoSession> itr = sessions.iterator();
            while (itr.hasNext()) {
                Player player = (Player)itr.next().getAttribute(Player.ATTR_NAME);
                if (player != null && player.getId().equals(profile.getUserId())) {
                    // 用户已经登录
                    echoMessage.setResult(LOGIN_USER_ALREADY_ONLINE);
                    sessionWrite(session, echoMessage);
                    return;
                }
            }
        }
        // 保存玩家信息
        Player player = new Player();
        player.setId(profile.getUserId());
        player.setName(profile.getName());
        player.setDisplayIndex(String.valueOf(new Date().getTime()));
        player.setPuppet(String.valueOf(profile.getStatus()).indexOf("puppet") >= 0 &&
                String.valueOf(profile.getStatus()).indexOf("puppet_disabled") < 0); // 状态可以设置为puppet或puppet_disabled，这个可以启用或禁用puppet标识
        ModelUtil.setPlayer(session, player);
        echoMessage.setContent(profile.getProfileId());
        echoMessage.setResult(LOGIN_SUCCESS);
        sessionWrite(session, echoMessage);
        HibernateSessionFactory.getSession().save(SystemLogger.createLog("LOGIN", null, player.getId(), LogType.SYSTEM_LOG));
    }

    public static final String LOBBY_ENTER_ROOM = "LOBBY_ENTER_ROOM";
    public void LOBBY_ENTER_ROOM(IoSession session, PlayerMessage message, EchoMessage echoMessage) {
        // 房间内广播提示玩家加入了房间
        Player currentPlayer = ModelUtil.getPlayer(session);
        if (currentPlayer.getParent() != null) {
            // 将原有的房间与玩家的关系进行解除
            currentPlayer.getParent().removeChild(currentPlayer.getId());
            currentPlayer.setParent(null);
        }
        // 重新设置房间与玩家的关系
        String roomId = message.getContent();
        Room currentRoom = ModelUtil.getRoom(roomId);
        currentRoom.addChild(currentPlayer.getId(), currentPlayer);
        currentPlayer.setParent(currentRoom);
        currentPlayer.setCurrentStatus(GameStatus.IDLE);
        // 在房间内进行当前玩家进入游戏的消息广播
        // 为房间内每个玩家提供当前房间内所有的玩家信息
        if (true) {
            // FIXME THIS FUNCTION CAN BE ENABLE BY SETTING SYSTEM PARAMETER, DEFAULT IS DISABLE
            return;
        }
        Set<IoSession> sessions = ModelUtil.getSessions();
        synchronized (sessions) {
            Player player = null;
            Iterator<IoSession> itr = sessions.iterator();
            String infoForEntry = "有玩家进入房间[%1$s]，当前房间玩家数共计%2$s人，游戏中有%3$s人，等待队列中有%4$s人。";
            infoForEntry = String.format(infoForEntry, currentRoom.getName(), currentRoom.getChildSize(), 
                    currentRoom.getGameStatusNumber(GameStatus.PLAYING), currentRoom.getGameStatusNumber(GameStatus.MATCHING));
            while (itr.hasNext()) {
                // 向同房间内的玩家发生消息
                session = itr.next();
                player = (Player)session.getAttribute(Player.ATTR_NAME);
                if (player != null && currentRoom.equals(player.getCurrentRoom())) {
                    echoMessage = new PlatformMessage().getEchoMessage();
                    echoMessage.setResult(LOBBY_ENTER_ROOM);
                    echoMessage.setContent(infoForEntry);
                    sessionWrite(session, echoMessage);
                }
            }
        }
    }

    public static final String LOBBY_LEAVE_ROOM = "LOBBY_LEAVE_ROOM";
    public void LOBBY_LEAVE_ROOM(IoSession session, PlayerMessage message, EchoMessage echoMessage) {
        // 房间内广播提示玩家加入了房间
        Player currentPlayer = ModelUtil.getPlayer(session);
        Room currentRoom = currentPlayer.getParent();
        // 将原有的房间与玩家的关系进行解除
        currentPlayer.getParent().removeChild(currentPlayer.getId());
        currentPlayer.setParent(null);
        currentPlayer.setCurrentStatus(GameStatus.WANDER);
        // 在房间内进行当前玩家进入游戏的消息广播
        // 为房间内每个玩家提供当前房间内所有的玩家信息
        if (true) {
            // FIXME THIS FUNCTION CAN BE ENABLE BY SETTING SYSTEM PARAMETER, DEFAULT IS DISABLE
            return;
        }
        Set<IoSession> sessions = ModelUtil.getSessions();
        synchronized (sessions) {
            Player player = null;
            Iterator<IoSession> itr = sessions.iterator();
            String infoForEntry = "有玩家退出房间[%1$s]，当前房间玩家数共计%2$s人，游戏中有%3$s人，等待队列中有%4$s人。";
            infoForEntry = String.format(infoForEntry, currentRoom.getName(), currentRoom.getChildSize(), 
                    currentRoom.getGameStatusNumber(GameStatus.PLAYING), currentRoom.getGameStatusNumber(GameStatus.MATCHING));
            while (itr.hasNext()) {
                player = (Player)itr.next().getAttribute(Player.ATTR_NAME);
                if (player != null) {
                    echoMessage = new PlatformMessage().getEchoMessage();
                    echoMessage.setResult(LOBBY_LEAVE_ROOM);
                    echoMessage.setContent(String.valueOf("玩家[" + currentPlayer.getName() + "]已经退出房间[" + currentRoom.getName() + "]"));
                    sessionWrite(session, echoMessage);
                }
            }
        }
    }

//    public static final String LOBBY_ENTER_GAME = "LOBBY_ENTER_GAME";
//    public void LOBBY_ENTER_GAME(IoSession session, PlayerMessage message, EchoMessage echoMessage) {
//        // TODO 玩家进入游戏
//    }
//
//    public static final String LOBBY_LEAVE_GAME = "LOBBY_LEAVE_GAME";
//    public void LOBBY_LEAVE_GAME(IoSession session, PlayerMessage message, EchoMessage echoMessage) {
//        // TODO 玩家离开游戏
//    }
//
//    public static final String LOBBY_SEND_MESSAGE = "LOBBY_SEND_MESSAGE";
//    public void LOBBY_SEND_MESSAGE(IoSession session, PlayerMessage message, EchoMessage echoMessage) {
//        // TODO 大厅内聊天
//    }
}
