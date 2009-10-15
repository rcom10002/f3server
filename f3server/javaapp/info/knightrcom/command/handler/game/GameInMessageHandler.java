package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.F3ServerInMessageHandler;
import info.knightrcom.command.message.EchoMessage;

import org.apache.mina.core.session.IoSession;

/**
 * 游戏消息控制句柄
 */
public abstract class GameInMessageHandler<T> extends F3ServerInMessageHandler {

    public static final String GAME_SETTING_UPDATE = "GAME_SETTING_UPDATE";
    /** 设置游戏 */
    public abstract void GAME_SETTING(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_SETTING_OVER = "GAME_SETTING_OVER";
    /** 游戏设置结束 */
    public abstract void GAME_SETTING_FINISH(IoSession session, T message, EchoMessage echoMessage) throws Exception;
    
    public static final String GAME_CREATE = "GAME_CREATE";
    public static final String GAME_WAIT = "GAME_WAIT";
    /** 取得当前游戏设置信息 */
    public abstract void GAME_JOIN_MATCHING_QUEUE(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_STARTED = "GAME_STARTED";
    public static final String GAME_FIRST_PLAY = "GAME_FIRST_PLAY";
    /** @deprecated 游戏进入准备状态，随时可以开始游戏 */
    public abstract void GAME_START(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_WINNER_PRODUCED = "GAME_WINNER_PRODUCED";
    /** 游戏胜利，玩家有获胜情况时，通知系统，但游戏不一定结束 */
    public abstract void GAME_WIN(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_OVER = "GAME_OVER";
    /** 游戏结束，计算所有玩家分数 */
    public abstract void GAME_WIN_AND_END(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_PLAYER_LOST_CONNECTION = "GAME_PLAYER_LOST_CONNECTION";
    /** 玩家掉线 */
    public abstract void GAME_PLAYER_LOST_CONNECTION(IoSession session, T message, EchoMessage echoMessage) throws Exception;

//    /** 游戏开始 */
//    public static final String GAME_START = "GAME_START";
//    public static final String GAME_WAITING = "GAME_WAITING";
//    public abstract void GAME_START(IoSession session, T message, EchoMessage echoMessage) throws Exception;
//
//    /** 游戏结束 */
//    public static final String GAME_OVER = "GAME_OVER";
//    public abstract void GAME_OVER(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_BRING_OUT = "GAME_BRING_OUT";
    /** 传递游戏消息 */
    public abstract void GAME_BRING_OUT(IoSession session, T message, EchoMessage echoMessage) throws Exception;

    public static final String GAME_CHEAT_FOUND = "GAME_CHEAT_FOUND";
    /** 发现作弊现象 */
    public abstract void GAME_CHEAT_FOUND(IoSession session, T message, EchoMessage echoMessage) throws Exception;

//    /** 游戏组内广播 */
//    public static final String GAME_TEAM_BROADCAST = "GAME_TEAM_BROADCAST";
//    public abstract void GAME_TEAM_BROAD_CAST(IoSession session, T message, EchoMessage echoMessage) throws Exception;

}
