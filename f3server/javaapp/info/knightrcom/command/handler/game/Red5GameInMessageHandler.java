package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.Red5GameMessage;
import info.knightrcom.data.HibernateTransactionSupport;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.model.game.GamePool;
import info.knightrcom.model.game.red5.Red5Game;
import info.knightrcom.model.game.red5.Red5GameSetting;
import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.model.global.GameStatus;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.util.ModelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

/**
 * 红五消息控制句柄
 */
public class Red5GameInMessageHandler extends GameInMessageHandler<Red5GameMessage> {

    @Override
    public void GAME_JOIN_MATCHING_QUEUE(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 判断当前玩家是否有足够分数加入游戏
        Player currentPlayer = ModelUtil.getPlayer(session);
        Room currentRoom = currentPlayer.getCurrentRoom();
        PlayerProfile currentPlayerProfile = new PlayerProfileDAO().findByUserId(currentPlayer.getId()).get(0);
        if (currentPlayerProfile.getCurrentScore() < currentRoom.getMinGameMarks()) {
            currentPlayer.setCurrentStatus(GameStatus.IDLE);
            String content = "当前房间所需最低游戏分数为" + currentPlayer.getCurrentRoom().getMinGameMarks() + "分，您的分数不足，请充值！";
            echoMessage.setResult(GAME_WAIT);
            echoMessage.setContent(content);
            sessionWrite(session, echoMessage);
            return;
        }

        // 将当前玩家加入游戏等待队列中
        ModelUtil.getPlayer(session).setCurrentStatus(GameStatus.MATCHING);

        // 判断当前房间内等候的玩家个数是否足够以开始游戏
        int groupQuantity = new Integer(ModelUtil.getSystemParameters("WAITING_QUEUE_GROUP_QUANTITY"));
        if (currentRoom.getGameStatusNumber(GameStatus.MATCHING) < Red5Game.PLAYER_COGAME_NUMBER * groupQuantity) {
            String content = "当前房间等候的玩家数(" + currentRoom.getGameStatusNumber(GameStatus.MATCHING) + 
                ")不足以开始新的游戏，请稍候。";
            echoMessage.setResult(GAME_WAIT);
            echoMessage.setContent(content);
            sessionWrite(session, echoMessage);
            return;
        }

        // 开始游戏
        GAME_START(session, message, echoMessage);
    }

    @Override
    public synchronized void GAME_START(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 取得玩家所在房间内所有的玩家
        Map<String, Player> playersInRoom = ModelUtil.getPlayer(session).getCurrentRoom().getChildren();
        synchronized (playersInRoom) {
            // 取得当前房间内的等待队列中的玩家
            List<Player> playersInQueue = new ArrayList<Player>();
            for (Player eachPlayer : playersInRoom.values()) {
                if (GameStatus.MATCHING.equals(eachPlayer.getCurrentStatus())) {
                    playersInQueue.add(eachPlayer);
                }
            }
            // 按照等候的优先顺序进行排序，使先进入等待队列的玩家排在前面
            Collections.sort(playersInQueue, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    if (p1.getLastPlayTime() < p2.getLastPlayTime()) {
                        return 1;
                    } else if (p1.getLastPlayTime() > p2.getLastPlayTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            // 按照系统设置的最大游戏开始人数进行人数截取
            int groupQuantity = new Integer(ModelUtil.getSystemParameters("WAITING_QUEUE_GROUP_QUANTITY"));
            playersInQueue = playersInQueue.subList(0, Red5Game.PLAYER_COGAME_NUMBER * groupQuantity);
            if ("true".equals(ModelUtil.getSystemParameters("WAITING_QUEUE_RANDOM_ENABLE").toLowerCase())) {
                // 将玩家再次随机调整顺序
                Collections.shuffle(playersInQueue);
            }
            List<Player> playersInGroup = new ArrayList<Player>();
            for (int i = 0; i < playersInQueue.size(); i++) {
                playersInGroup.add(playersInQueue.get(i));
                if ((i + 1) % Red5Game.PLAYER_COGAME_NUMBER != 0) {
                    continue;
                }
                // 根据玩家当前的所在的房间进来开始游戏
                String gameId = GamePool.prepareRed5Game(playersInGroup);
                for (Player eachPlayer : playersInGroup) {
                    // 向客户端发送游戏id，玩家编号以及游戏所需要的玩家人数
                    eachPlayer.setCurrentStatus(GameStatus.PLAYING);
                    echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                    echoMessage.setResult(GAME_CREATE);
                    echoMessage.setContent(
                            eachPlayer.getGameId() + "~" + 
                            eachPlayer.getCurrentNumber() + "~" + 
                            Red5Game.PLAYER_COGAME_NUMBER);
                    sessionWrite(eachPlayer.getIosession(), echoMessage);
                }
                // 根据当前触发游戏开始的玩家所携带的游戏id来取得游戏实例
                Red5Game game = GamePool.getGame(gameId, Red5Game.class);
                List<Player> playersInGame = game.getPlayers();
                // 开始洗牌与发牌，排序功能与出牌规则在客户端完成
                boolean isFirstOut = false;
                Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle();
                // 取得合作玩家手中所持有的牌数
                String pokerNumberOfEachPlayer = "";
                for (int index = 0; index < eachShuffledPokers.length; index++) {
                    int lastIndex = eachShuffledPokers[index].length - 1;
                    pokerNumberOfEachPlayer += index + "=";
                    if (eachShuffledPokers[index][lastIndex] == null) {
                        pokerNumberOfEachPlayer += (eachShuffledPokers[index].length - 1) + ","; 
                    } else {
                        pokerNumberOfEachPlayer += eachShuffledPokers[index].length + ",";
                    }
                }
                pokerNumberOfEachPlayer = pokerNumberOfEachPlayer.replaceFirst(",$", "");
                // 准备发牌开始游戏 
                for (int m = 0; m < eachShuffledPokers.length; m++) {
                    StringBuilder builder = new StringBuilder();
                    for (int n = 0; n < eachShuffledPokers[m].length; n++) {
                        builder.append(eachShuffledPokers[m][n].getValue() + ",");
                    }
                    // 为每位玩家发牌
                    echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                    echoMessage.setResult(GAME_STARTED);
                    echoMessage.setContent(builder.toString().replaceFirst(",$", "~") + pokerNumberOfEachPlayer);
                    sessionWrite(playersInGame.get(m).getIosession(), echoMessage);
                    // 记录游戏初始时玩家手中的牌信息
                    game.appendGameRecord(echoMessage.getContent());
                    if (!isFirstOut && builder.indexOf(Red5Game.START_POKER.getValue()) > -1) {
                        // 如果当前尚未设置过首次发牌的玩家，并且在当前牌序中发现红桃十，则为首次发牌的玩家发送消息
                        echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                        echoMessage.setResult(GAME_FIRST_PLAY);
                        sessionWrite(playersInGame.get(m).getIosession(), echoMessage);
                        isFirstOut = true;
                    }
                }
                playersInGroup.clear();
            }
        }
    }

    @Override
    public void GAME_SETTING(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 在游戏开始前进行本次设置[独牌|不独|天独]
        Player currentPlayer = ModelUtil.getPlayer(session);
        Red5Game game = GamePool.getGame(currentPlayer.getGameId(), Red5Game.class);
        List<Player> players = game.getPlayers();
        synchronized (players) {
            Iterator<Player> itr = players.iterator();
            while (itr.hasNext()) {
                Player player = itr.next();
                if (currentPlayer.equals(player)) {
                    continue;
                }
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_SETTING_UPDATE);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
            // 记录当前牌序
            game.appendGameRecord(message.getContent());
        }
    }

    @Override
    public void GAME_SETTING_FINISH(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 玩家游戏设置结束
        Player currentPlayer = ModelUtil.getPlayer(session);
        Red5Game game = GamePool.getGame(currentPlayer.getGameId(), Red5Game.class);
        String[] results = message.getContent().split("~");
        // 游戏最终设置的玩家序号，首次发牌玩家序号
        String playerNumber = results[0];
        // 当前游戏设置
        int settingValue = Integer.parseInt(results[1]); 
        Red5GameSetting setting = Red5GameSetting.fromOrdinal(settingValue);
        setting.setPlayerNumber(playerNumber);
        game.setSetting(setting);
        log.debug(setting);
    }

    @Override
    public void GAME_BRING_OUT(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        Red5Game game = GamePool.getGame(currentPlayer.getGameId(), Red5Game.class);
        List<Player> players = game.getPlayers();
        synchronized (players) {
            Iterator<Player> itr = players.iterator();
            while (itr.hasNext()) {
                Player player = itr.next();
                if (currentPlayer.equals(player)) {
                    continue;
                }
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_BRING_OUT);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
            // 记录当前牌序
            game.appendGameRecord(message.getContent());
        }
    }

    @Override
    public void GAME_WIN(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        Red5Game game = GamePool.getGame(currentPlayer.getGameId(), Red5Game.class);
        // 判断是否为最终获胜
        if (!Red5GameSetting.NO_RUSH.equals(game.getSetting())) {
            // 游戏为独牌或天独的时，立即结束当前游戏
            GAME_WIN_AND_END(session, message, echoMessage);
            return;
        }
        game.addWinnerNumber(String.valueOf(currentPlayer.getCurrentNumber()));
        List<Player> players = game.getPlayers();
        synchronized (players) {
            Iterator<Player> itr = players.iterator();
            while (itr.hasNext()) {
                Player player = itr.next();
                if (currentPlayer.equals(player)) {
                    continue;
                }
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_WINNER_PRODUCED);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
            // 记录当前牌序
            game.appendGameRecord(message.getContent());
        }
    }

    @Override
    @HibernateTransactionSupport
    public void GAME_WIN_AND_END(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 游戏结束，向游戏中的其他玩家发送消息
        Player currentPlayer = ModelUtil.getPlayer(session);
        Red5Game game = GamePool.getGame(currentPlayer.getGameId(), Red5Game.class);
        String[] results = message.getContent().split("~");
        List<Player> players = game.getPlayers();
        log.debug(game.getSetting().getDisplayName());
        // 记录当前牌序
        game.appendGameRecord(message.getContent());
        synchronized (players) {
            // 设置名次并计算积分
            if (Red5GameSetting.NO_RUSH.equals(game.getSetting())) {
                // 不独，添加第三名、第四名玩家
                game.addWinnerNumber(results[0]);
                game.addWinnerNumber(results[2]);
            } else if (Red5GameSetting.RUSH.equals(game.getSetting())) {
                // 独牌，添加获胜者
                game.addWinnerNumber(results[0]);
            } else if (Red5GameSetting.DEADLY_RUSH.equals(game.getSetting())) {
                // 天独，添加获胜者
                game.addWinnerNumber(results[0]);
            } else if (Red5GameSetting.EXTINCT_RUSH.equals(game.getSetting())) {
                // 天外天，添加获胜者
                game.addWinnerNumber(results[0]);
            }
            // 保存游戏积分
            game.persistScore();
            // 显示游戏积分
            Iterator<Player> itr = players.iterator();
            // 构造积分显示信息
            message.setContent(message.getContent() + "~" + game.getGameDetailScore());
            while (itr.hasNext()) {
                Player player = itr.next();
                player.setCurrentStatus(GameStatus.IDLE);
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_OVER);
                echoMessage.setContent(message.getContent());
                sessionWrite(player.getIosession(), echoMessage);
            }
        }
        // 清除内存中本次游戏的相关信息
        log.debug(game.getGameRecord());
        log.debug(game.getWinnerNumbers());
        GamePool.distroyGame(currentPlayer.getGameId(), Red5Game.class);
    }

    @Override
    public void GAME_PLAYER_LOST_CONNECTION(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 通知其他玩家并为其他玩家分配分数
        Player player = ModelUtil.getPlayer(session);
        if (GamePool.getGame(player.getGameId(), Red5Game.class) != null) {
            // 扣除玩家分数，并为游戏中的其他玩家分配分数
            // TODO
        }
        // 非游戏中掉线的情况，通知其他玩家在线人数发生了变化
        PlatformMessage localMessage = (PlatformMessage)F3ServerMessage.createInstance(MessageType.PLATFORM);
        new PlatformInMessageHandler().PLATFORM_PLAYER_LOST_CONNECTION(session, localMessage, localMessage.getEchoMessage());
    }

    @Override
    public void GAME_CHEAT_FOUND(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // TODO Auto-generated method stub

    }
}
