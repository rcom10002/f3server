package info.knightrcom;

import info.knightrcom.command.handler.F3ServerInMessageHandler;
import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.handler.PlayerInMessageHandler;
import info.knightrcom.command.handler.game.FightLandlordGameInMessageHandler;
import info.knightrcom.command.handler.game.PushdownWinGameInMessageHandler;
import info.knightrcom.command.handler.game.QiongWinGameInMessageHandler;
import info.knightrcom.command.handler.game.Red5GameInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.PlayerMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.FightLandlordGameMessage;
import info.knightrcom.command.message.game.PushdownWinGameMessage;
import info.knightrcom.command.message.game.QiongWinGameMessage;
import info.knightrcom.command.message.game.Red5GameMessage;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.game.GamePool;
import info.knightrcom.model.global.Platform;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.HandlerDispatcher;
import info.knightrcom.util.ModelUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.DemuxingIoHandler;
import org.apache.mina.handler.demux.MessageHandler;

/**
 *
 */
public class ServiceHandler extends DemuxingIoHandler {

    private static final Log log = LogFactory.getLog(ServiceHandler.class);

    // TODO What's the differences between sessions and managed-sessions ?
    private final Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());

    /**
     * @param platform
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServiceHandler(Platform platform) throws InstantiationException, IllegalAccessException {
        ModelUtil.setSessions(sessions);
        this.addReceivedMessageHandler(PlatformMessage.class, new PlatformInMessageHandler());
        this.addReceivedMessageHandler(PlayerMessage.class, new PlayerInMessageHandler());
        this.addReceivedMessageHandler(Red5GameMessage.class, new Red5GameInMessageHandler());
        this.addReceivedMessageHandler(FightLandlordGameMessage.class, new FightLandlordGameInMessageHandler());
        this.addReceivedMessageHandler(PushdownWinGameMessage.class, new PushdownWinGameInMessageHandler());
        this.addReceivedMessageHandler(QiongWinGameMessage.class, new QiongWinGameInMessageHandler());
        Iterator<MessageHandler<?>> itr = this.getReceivedMessageHandlerMap().values().iterator();
        while (itr.hasNext()) {
            F3ServerInMessageHandler handler = (F3ServerInMessageHandler)itr.next();
            HandlerDispatcher.initMethodPoolForHandler(handler);
        }
    }

    @Override
    public void sessionCreated(IoSession session) {
        // 24小时后自动超时
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60 * 60 * 24);
        // 添加Session
        sessions.add(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void sessionIdle(IoSession iosession, IdleStatus idlestatus) throws Exception {
        super.sessionIdle(iosession, idlestatus);
        // 关闭Session
        iosession.close(true);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // TODO 移除用户登录信息
        sessions.remove(session);
        Player player = ModelUtil.getPlayer(session);
        if (player == null) {
            return;
        }
        Room room = player.getParent();
        if (room != null) {
            room.removeChild(player.getId());
            player.setParent(null);
        }
        // 清除与游戏相关的内容
        Game<?> game = GamePool.getGame(player.getGameId(), Game.class);
        if (game != null) {
            List<Player> players = game.getPlayers();
            synchronized (players) {
                for (Player eachPlayer : players) {
                    // 通知游戏中的其他玩家游戏已经中断，如果想重新加入游戏，必须进入游戏队列中
                    if (!session.equals(eachPlayer.getIosession())) {
                        // TODO 掉线积分处理
                        // if (处理过) 
                        // if (尚未处理)
                        
                        eachPlayer.setCurrentStatus(info.knightrcom.model.global.GameStatus.IDLE);
                        EchoMessage echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                        echoMessage.setResult(PlatformInMessageHandler.GAME_INTERRUPTED);
                        sessionWrite(eachPlayer.getIosession(), echoMessage);
                    }
                }
            }
            GamePool.distroyGame(game.getId(), game.getClass());
            
        }
        // broadcast("The user " + player + " has left the chat session.");
        super.sessionClosed(session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        // Flex安全策略
        if (String.valueOf(message).trim().equalsIgnoreCase("<policy-file-request/>")) {
            session.write(F3Server.SECURITY_CONFIGURATION);
            session.close(true);
            return;
        }
        // 解析消息，消息构成格式为：消息类型、消息编号、消息签名、消息内容
        message = EncryptionUtil.Base64Decode(message.toString());
        String[] results = message.toString().split("~", 4);
        int msgType = Integer.parseInt(results[0]);
        long number = Long.parseLong(results[1]);
        String signature = results[2];
        String content = results[3];
        if (content != null) {
            if (content.length() == 0 || "NULL".equalsIgnoreCase(content)) {
                content = null;
            }
        }
        // 构造消息
        F3ServerMessage serverMessage = F3ServerMessage.createInstance(msgType);
        serverMessage.setType(msgType);
        serverMessage.setNumber(number);
        serverMessage.setSignature(signature);
        serverMessage.setContent(content);
        serverMessage.setCurrentSession(session);
        serverMessage.setSessions(sessions);
        // 派发消息
        super.messageReceived(session, serverMessage);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // TODO Auto-generated method stub
        // FIXME super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        try {
            log.warn("Unexpected exception.", cause);
            // TODO 通知客户端服务器有错误发生，断开连接
            // session.close(false) ???
            // 需要移除内存中相关的在线用户信息
            session.close(true);
            // super.exceptionCaught(session, cause);
        } catch (Exception e) {
            e.printStackTrace();
            sessionClosed(session);
        }
    }

    /**
     * @param session
     * @param echoMessage
     */
    private void sessionWrite(IoSession session, EchoMessage echoMessage) {
        session.write(EncryptionUtil.Base64Encode(HandlerDispatcher.respondMessage(echoMessage)));
    }
}
