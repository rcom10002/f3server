package info.knightrcom;

import info.knightrcom.F3ServerProxy.LogType;
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
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.game.GamePool;
import info.knightrcom.model.global.Platform;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.HandlerDispatcher;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.util.StringHelper;
import info.knightrcom.util.SystemLogger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.DemuxingIoHandler;
import org.apache.mina.handler.demux.MessageHandler;

/**
 *
 */
public class F3ServerServiceHandler extends DemuxingIoHandler {

    private static final Log log = LogFactory.getLog(F3ServerServiceHandler.class);

    // TODO What's the differences between sessions and managed-sessions of mina ?
    // In the example of mina a third sessions is used for session management. 
    private final Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    private ExecutorService idleFutureExecutor = Executors.newFixedThreadPool(new Integer(ModelUtil.getSystemParameter("MAX_THREADS_IN_IDLE_FUTURE_EXECUTOR", 100)));

    /**
     * @param platform
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public F3ServerServiceHandler(Platform platform) throws InstantiationException, IllegalAccessException {
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
        // 默认90分钟后自动超时
        int idleTime = new Integer(ModelUtil.getSystemParameter("IDLE_TIME", 90));
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
        // 添加Session
        sessions.add(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idlestatus) throws Exception {
        // 到达空闲时间时，主动询问客户端
        EchoMessage echoMessage = F3ServerMessage.createInstance(MessageType.PLATFORM).getEchoMessage();
        echoMessage.setResult(PlatformInMessageHandler.SERVER_IDLE_TEST);
        session.setAttribute("ALIVE", null);
        sessionWrite(session, echoMessage);
        final IoSession threadSession = session;
        idleFutureExecutor.execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(15 * 1000);
                } catch (Exception e) {
                }
                if (!"yes".equals(threadSession.getAttribute("ALIVE"))) {
                    threadSession.close(true);
                }
            }
        });
        // session.setAttributeIfAbsent(obj, obj1);
        // 关闭Session
        // iosession.close(true);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // 移除用户登录信息
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
            synchronized (game) {
                for (Player eachPlayer : game.getPlayers()) {
                    // 通知游戏中的其他玩家游戏已经中断，如果想重新加入游戏，必须进入游戏队列中
                    if (!session.equals(eachPlayer.getIosession())) {
                        eachPlayer.setCurrentStatus(info.knightrcom.model.global.GameStatus.IDLE);
                        EchoMessage echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage(); // FIXME THIS SHOULD BE CHANGE TO PLATFORM EVENT TYPE
                        echoMessage.setResult(PlatformInMessageHandler.GAME_INTERRUPTED);
                        sessionWrite(eachPlayer.getIosession(), echoMessage);
                    }
                }
                // 计算掉线积分
                game.persistDisconnectScore(player);
            }
            GamePool.distroyGame(game.getId(), game.getClass());
        }
        HibernateSessionFactory.getSession().save(SystemLogger.createLog("SESSION CLOSED", 
                session.getRemoteAddress().toString(), 
                player.getId(), 
                LogType.SYSTEM_LOG));
        HibernateSessionFactory.getSession().flush();
        HibernateSessionFactory.getSession().close();
        super.sessionClosed(session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        // Flex安全策略
        if (String.valueOf(message).trim().equalsIgnoreCase("<policy-file-request/>")) {
            session.write(F3Server.SECURITY_CONFIGURATION);
            // FIXME THE FOLLOWING LINE MAY BE REMOVED SO THAT ONLY ONE CONNECTION WILL FETCH INFO FROM SOCKET SERVER.
            // session.close(true);
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
        // serverMessage.setSessions(sessions);
        // 派发消息
        super.messageReceived(session, serverMessage);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        try {
            log.warn("Global exception is found.");
            if (cause instanceof IOException) {
                // FIXME 原始IOException可能已经被RuntimeException包装，判断条件可能不合适
                log.warn("Try to close socket session and persist current game scores!");
                sessionClosed(session);
                session.close(true);
            }
            // 日志记录
            LogInfo logInfo = SystemLogger.createLog(LogType.SYSTEM_ERROR.toString(), cause.getMessage(), StringHelper.convertExceptionStack2String(cause), LogType.SYSTEM_ERROR);
            HibernateSessionFactory.getSession().save(logInfo);
            HibernateSessionFactory.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
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
