package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.QiongWinGameMessage;
import info.knightrcom.data.HibernateTransactionSupport;
import info.knightrcom.model.game.GamePool;
import info.knightrcom.model.game.qiongwin.QiongWinGame;
import info.knightrcom.model.game.qiongwin.QiongWinGameSetting;
import info.knightrcom.model.game.qiongwin.QiongWinMahjong;
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
 * 穷胡消息控制句柄
 */
public class QiongWinGameInMessageHandler extends GameInMessageHandler<QiongWinGameMessage> {

    @Override
    public void GAME_JOIN_MATCHING_QUEUE(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 按优先级别选择游戏玩家进行游戏安排
        // TODO 玩家进入游戏等待队列
        // 判断当前房间内等候的玩家个数是否足够以开始游戏
        Player currentPlayer = ModelUtil.getPlayer(session);
        currentPlayer.setCurrentStatus(GameStatus.MATCHING);
        Room currentRoom = currentPlayer.getCurrentRoom();
        if (currentRoom.getGameStatusNumber(GameStatus.MATCHING) < QiongWinGame.PLAYER_COGAME_NUMBER) {
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
            playersInQueue = playersInQueue.subList(0, QiongWinGame.PLAYER_COGAME_NUMBER);
            // 根据玩家当前的所在的房间进来开始游戏
            GamePool.prepareQiongWinGame(playersInQueue);
            for (Player eachPlayer : playersInQueue) {
                // 向客户端发送游戏id，玩家编号以及游戏所需要的玩家人数
                echoMessage = F3ServerMessage.createInstance(MessageType.QIONG_WIN).getEchoMessage();
                echoMessage.setResult(GAME_CREATE);
                echoMessage.setContent(
                        eachPlayer.getGameId() + "~" + 
                        eachPlayer.getCurrentNumber() + "~" + 
                        QiongWinGame.PLAYER_COGAME_NUMBER);
                sessionWrite(eachPlayer.getIosession(), echoMessage);
            }
        }
        // 根据当前触发游戏开始的玩家所携带的游戏id来取得游戏实例
        QiongWinGame game = GamePool.getGame(currentPlayer.getGameId(), QiongWinGame.class);
        List<Player> playersInGame = game.getPlayers();
        // 开始洗牌与发牌，排序功能与出牌规则在客户端完成
        QiongWinMahjong[][] eachShuffledMahjongs = QiongWinMahjong.shuffle();
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
            echoMessage = F3ServerMessage.createInstance(MessageType.QIONG_WIN).getEchoMessage();
            echoMessage.setResult(GAME_STARTED);
            echoMessage.setContent(builder.toString().replaceFirst(",$", ""));
            sessionWrite(eachPlayer.getIosession(), echoMessage);
        }
        // 记录游戏初始时玩家手中的牌信息
        game.appendGameRecord(echoMessage.getContent());
        // 为首次发牌的玩家发送消息
        echoMessage = F3ServerMessage.createInstance(MessageType.QIONG_WIN).getEchoMessage();
        echoMessage.setResult(GAME_FIRST_PLAY);
        sessionWrite(playersInGame.get(0).getIosession(), echoMessage);
    }

    @Override
    public void GAME_SETTING(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }

    @Override
    public void GAME_SETTING_FINISH(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }

    @Override
    public void GAME_START(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }

    @Override
    public void GAME_BRING_OUT(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        QiongWinGame game = GamePool.getGame(currentPlayer.getGameId(), QiongWinGame.class);
        List<Player> players = game.getPlayers();
        synchronized (players) {
            Iterator<Player> itr = players.iterator();
            while (itr.hasNext()) {
                Player player = itr.next();
                if (currentPlayer.equals(player)) {
                    continue;
                }
                echoMessage = F3ServerMessage.createInstance(MessageType.QIONG_WIN).getEchoMessage();
                echoMessage.setResult(GAME_BRING_OUT);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
            // 记录当前牌序
            game.appendGameRecord(message.getContent());
        }
    }

    @Override
    public void GAME_WIN(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
    }

    @Override
    @HibernateTransactionSupport
    public void GAME_WIN_AND_END(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 游戏结束，向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        QiongWinGame game = GamePool.getGame(currentPlayer.getGameId(), QiongWinGame.class);
        // 消息格式：无内容
        if (StringHelper.isEmpty(message.getContent())) {
            // 流局，扑
        }
        // 消息格式：1.胜者~牌~败者(胡别的玩家牌) 2.胜者~牌(自摸) 3.无消息内容(流局，扑)
        if (StringHelper.isEmpty(message.getContent()) || "null".equalsIgnoreCase(message.getContent())) {
            // 流局
            // FIXME persist the record and destory game instance
            return;
        }
        String[] results = message.getContent().split("~");
        List<Player> players = game.getPlayers();
        // 记录当前牌序
        game.appendGameRecord(message.getContent());
        synchronized (players) {
            // 设置名次并计算积分
        	if (results.length == 2) {
                // 自摸
        		game.addWinnerNumber(results[0]);
                game.setSetting(QiongWinGameSetting.CLEAR_VICTORY);
        	} else {
        		// 点炮
                game.addWinnerNumber(results[0]);
                game.addWinnerNumber(results[2]);
                game.setSetting(QiongWinGameSetting.NARROW_VICTORY);
        	}
            // 保存游戏积分
            game.persistScore();
            // 显示游戏积分
            Iterator<Player> itr = players.iterator();
            // 构造积分显示信息
            message.setContent(message.getContent() + "~" + game.getGameDetailScore());
            while (itr.hasNext()) {
                Player player = itr.next();
                echoMessage = F3ServerMessage.createInstance(MessageType.QIONG_WIN).getEchoMessage();
                echoMessage.setResult(GAME_OVER);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
        }
        // 清除内存中本次游戏的相关信息
        log.debug(game.getGameRecord());
        log.debug(game.getWinnerNumbers());
        GamePool.distroyGame(currentPlayer.getGameId(), QiongWinGame.class);
    }

    @Override
    public void GAME_PLAYER_LOST_CONNECTION(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // 通知其他玩家并为其他玩家分配分数
        Player player = ModelUtil.getPlayer(session);
        if (GamePool.getGame(player.getGameId(), QiongWinGame.class) != null) {
            // 扣除玩家分数，并为游戏中的其他玩家分配分数
            // TODO
        }
        // 非游戏中掉线的情况，通知其他玩家在线人数发生了变化
        PlatformMessage localMessage = (PlatformMessage)F3ServerMessage.createInstance(MessageType.PLATFORM);
        new PlatformInMessageHandler().PLATFORM_PLAYER_LOST_CONNECTION(session, localMessage, localMessage.getEchoMessage());
    }

    @Override
    public void GAME_CHEAT_FOUND(IoSession session, QiongWinGameMessage message, EchoMessage echoMessage) throws Exception {
        // TODO Auto-generated method stub
    }
}
