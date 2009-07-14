package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.PushdownWinGameMessage;
import info.knightrcom.data.HibernateTransactionSupport;
import info.knightrcom.model.game.GamePool;
import info.knightrcom.model.game.pushdownwin.PushdownWinGame;
import info.knightrcom.model.game.pushdownwin.PushdownWinGameSetting;
import info.knightrcom.model.game.pushdownwin.PushdownWinMahjong;
import info.knightrcom.model.global.GameStatus;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.util.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

/**
 * 推倒胡消息控制句柄
 */
public class PushdownWinGameInMessageHandler extends GameInMessageHandler<PushdownWinGameMessage> {

    @Override
    public void GAME_JOIN_MATCHING_QUEUE(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 按优先级别选择游戏玩家进行游戏安排
        // TODO 玩家进入游戏等待队列
        // 判断当前房间内等候的玩家个数是否足够以开始游戏
        Player currentPlayer = ModelUtil.getPlayer(session);
        currentPlayer.setCurrentStatus(GameStatus.MATCHING);
        Room currentRoom = currentPlayer.getCurrentRoom();
        if (currentRoom.getGameStatusNumber(GameStatus.MATCHING) < PushdownWinGame.PLAYER_COGAME_NUMBER) {
            String content = "当前房间等候的玩家数(" + currentRoom.getGameStatusNumber(GameStatus.MATCHING) + 
                ")不足以开始新的游戏，请稍候。";
            echoMessage.setResult(GAME_WAIT);
            echoMessage.setContent(content);
            sessionWrite(session, echoMessage);
            return;
        }
        Map<String, Player> playersInRoom = currentRoom.getChildren();
        synchronized (playersInRoom) {
            // 将同一个房间内的等待队列中的玩家进行排序
            List<Player> playersInQueue = new ArrayList<Player>();
            for (Player eachPlayer : playersInRoom.values()) {
                if (GameStatus.MATCHING.equals(eachPlayer.getCurrentStatus())) {
                    playersInQueue.add(eachPlayer);
                }
            }
            // 按照等候的优先顺序进行排序
            Collections.sort(playersInQueue, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    if (p1.getLastPlayTime() > p2.getLastPlayTime()) {
                        return 1;
                    } else if (p1.getLastPlayTime() < p2.getLastPlayTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            playersInQueue = playersInQueue.subList(0, PushdownWinGame.PLAYER_COGAME_NUMBER);
            // 根据玩家当前的所在的房间进来开始游戏
            GamePool.preparePushdownWinGame(playersInQueue);
            for (Player eachPlayer : playersInQueue) {
                // 向客户端发送游戏id，玩家编号以及游戏所需要的玩家人数
                echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
                echoMessage.setResult(GAME_CREATE);
                echoMessage.setContent(
                        eachPlayer.getGameId() + "~" + 
                        eachPlayer.getCurrentNumber() + "~" + 
                        PushdownWinGame.PLAYER_COGAME_NUMBER);
                sessionWrite(eachPlayer.getIosession(), echoMessage);
            }
        }
        // 根据当前触发游戏开始的玩家所携带的游戏id来取得游戏实例
        PushdownWinGame game = GamePool.getGame(currentPlayer.getGameId(), PushdownWinGame.class);
        // 开始洗牌与发牌，排序功能与出牌规则在客户端完成
        PushdownWinMahjong[][] eachShuffledMahjongs = PushdownWinMahjong.shuffle();
        // 开始发牌 
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < eachShuffledMahjongs.length; i++) {
            for (int j = 0; j < eachShuffledMahjongs[i].length; j++) {
                builder.append(eachShuffledMahjongs[i][j].getValue() + ",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("~");
        }
        builder.deleteCharAt(builder.length() - 1);
        for (Player eachPlayer : game.getPlayers()) {
            echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
            echoMessage.setResult(GAME_STARTED);
            echoMessage.setContent(builder.toString().replaceFirst(",$", ""));
            sessionWrite(eachPlayer.getIosession(), echoMessage);
        }
        // 记录游戏初始时玩家手中的牌信息
        game.appendGameRecord(echoMessage.getContent());
        // 为首次发牌的玩家发送消息
        echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
        echoMessage.setResult(GAME_FIRST_PLAY);
        sessionWrite(game.getPlayerNumberMap().get("1").getIosession(), echoMessage);
    }

    @Override
    public void GAME_SETTING(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
//    	// TODO REMOVE THIS FUNCTION FOR NO QUIREMENTS
//        // 在游戏开始前进行本次设置[独牌|不独|天独]
//        Player currentPlayer = ModelUtil.getPlayer(session);
//        PushdownWinGame game = GamePool.getGame(currentPlayer.getGameId(), PushdownWinGame.class);
//        List<Player> players = game.getPlayers();
//        synchronized (players) {
//            Iterator<Player> itr = players.iterator();
//            while (itr.hasNext()) {
//                Player player = itr.next();
//                if (currentPlayer.equals(player)) {
//                    continue;
//                }
//                echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
//                echoMessage.setResult(GAME_SETTING_UPDATE);
//                echoMessage.setContent(message.getContent());
//                sessionWrite(player.getIosession(), echoMessage);
//            }
//            // 记录当前牌序
//            game.appendGameRecord(message.getContent());
//        }
    }

    @Override
    public void GAME_SETTING_FINISH(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
//    	// TODO REMOVE THIS FUNCTION FOR NO QUIREMENTS
//        // 玩家游戏设置结束
//        Player currentPlayer = ModelUtil.getPlayer(session);
//        PushdownWinGame game = GamePool.getGame(currentPlayer.getGameId(), PushdownWinGame.class);
//        String[] results = message.getContent().split("~");
//        // 游戏最终设置的玩家序号，首次发牌玩家序号
//        String playerNumber = results[0];
//        // 当前游戏设置
//        int settingValue = Integer.parseInt(results[1]); 
//        PushdownWinGameSetting setting = PushdownWinGameSetting.fromOrdinal(settingValue);
//        setting.setPlayerNumber(playerNumber);
//        game.setSetting(setting);
//        log.debug(setting);
    }

    @Override
    public void GAME_START(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }

    @Override
    public void GAME_BRING_OUT(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        PushdownWinGame game = GamePool.getGame(currentPlayer.getGameId(), PushdownWinGame.class);
        List<Player> players = game.getPlayers();
        synchronized (players) {
            Iterator<Player> itr = players.iterator();
            while (itr.hasNext()) {
                Player player = itr.next();
                if (currentPlayer.equals(player)) {
                    continue;
                }
                echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
                echoMessage.setResult(GAME_BRING_OUT);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
            // 记录当前牌序
            game.appendGameRecord(message.getContent());
        }
    }

    @Override
    public void GAME_WIN(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }

    @Override
    @HibernateTransactionSupport
    public void GAME_WIN_AND_END(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 游戏结束，向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        PushdownWinGame game = GamePool.getGame(currentPlayer.getGameId(), PushdownWinGame.class);
        // 消息格式：无内容
        if (StringHelper.isEmpty(message.getContent())) {
            // 流局，扑
//            synchronized (players) {
//                while (itr.hasNext()) {
//                    Player player = itr.next();
//                    echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
//                    echoMessage.setResult(GAME_OVER);
//                    echoMessage.setContent(message.getContent());
//                    sessionWrite(player.getIosession(), echoMessage);
//                }
//            }
        }
        // 消息格式：胜者~牌~败者~是否自摸
        String[] results = message.getContent().split("~");
        List<Player> players = game.getPlayers();
        // 记录当前牌序
        game.appendGameRecord(message.getContent());
        synchronized (players) {
            // 设置名次并计算积分
        	int gameWinSetting = new Integer(results[2]).intValue();
        	if (PushdownWinGameSetting.CLEAR_VICTORY.equals(PushdownWinGameSetting.fromOrdinal(gameWinSetting))) {
                // 自摸
        		game.addWinnerNumber(results[0]);
        	} else {
        		// 点炮
                game.addWinnerNumber(results[0]);
                game.addWinnerNumber(results[2]);
        	}
            // 保存游戏积分
            game.persistScore();
            // 显示游戏积分
            Iterator<Player> itr = players.iterator();
            // 构造积分显示信息
            message.setContent(message.getContent() + "~" + game.getGameDetailScore());
            while (itr.hasNext()) {
                Player player = itr.next();
                echoMessage = F3ServerMessage.createInstance(MessageType.PUSHDOWN_WIN).getEchoMessage();
                echoMessage.setResult(GAME_OVER);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
        }
        // 清除内存中本次游戏的相关信息
        log.debug(game.getGameRecord());
        log.debug(game.getWinnerNumbers());
        GamePool.distroyGame(currentPlayer.getGameId(), PushdownWinGame.class);
    }

    @Override
    public void GAME_PLAYER_LOST_CONNECTION(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 通知其他玩家并为其他玩家分配分数
        Player player = ModelUtil.getPlayer(session);
        if (GamePool.getGame(player.getGameId(), PushdownWinGame.class) != null) {
            // 扣除玩家分数，并为游戏中的其他玩家分配分数
            // TODO
        }
        // 非游戏中掉线的情况，通知其他玩家在线人数发生了变化
        PlatformMessage localMessage = (PlatformMessage)F3ServerMessage.createInstance(MessageType.PLATFORM);
        new PlatformInMessageHandler().PLATFORM_PLAYER_LOST_CONNECTION(session, localMessage, localMessage.getEchoMessage());
    }

    @Override
    public void GAME_CHEAT_FOUND(IoSession session, PushdownWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }
}
