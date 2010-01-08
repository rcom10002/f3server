package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.Red5GameMessage;
import info.knightrcom.data.HibernateSessionFactory;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

/**
 * 红五消息控制句柄
 */
public class Red5GameInMessageHandler extends GameInMessageHandler<Red5GameMessage> {

    @Override
    public void GAME_JOIN_MATCHING_QUEUE(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        // 判断当前玩家是否有足够分数加入游戏
        Player currentPlayer = ModelUtil.getPlayer(session);

        if (GameStatus.PLAYING.equals(ModelUtil.getPlayer(session).getCurrentStatus())) {
            // 游戏状态判断
            return;
        }

        Room currentRoom = currentPlayer.getCurrentRoom();
        HibernateSessionFactory.getSession().clear();
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
        int groupQuantity = new Integer(ModelUtil.getSystemParameter("WAITING_QUEUE_GROUP_QUANTITY"));
        if (currentRoom.getGameStatusNumber(GameStatus.MATCHING) < Red5Game.PLAYER_COGAME_NUMBER * groupQuantity) {
            int numPlayers = currentRoom.getGameStatusNumber(GameStatus.MATCHING);
            numPlayers += Math.ceil(numPlayers / (Red5Game.PLAYER_COGAME_NUMBER - 1));
            int matchingRate = (int)Math.round((double)numPlayers / (Red5Game.PLAYER_COGAME_NUMBER * groupQuantity) * 100);
            if (matchingRate == 100) {
                matchingRate = (int)Math.round(((double)numPlayers - new Integer(ModelUtil.getSystemParameter("WAITING_QUEUE_GROUP_QUANTITY"))) / 
                        (Red5Game.PLAYER_COGAME_NUMBER * groupQuantity) * 100);
            }
            String content = "当前房间等候的玩家数不足以开始新的游戏，系统配对比率为【" + matchingRate + "%】，请稍候。";
            echoMessage.setResult(GAME_WAIT);
            echoMessage.setContent(content);
            Set<IoSession> sessions = ModelUtil.getSessions();
            synchronized (sessions) {
                Iterator<IoSession> itr = sessions.iterator();
                while (itr.hasNext()) {
                    // 向同房间内的玩家发生消息
                    session = itr.next();
                    if (GameStatus.MATCHING.equals(ModelUtil.getPlayer(session).getCurrentStatus())) {
                        sessionWrite(session, echoMessage);
                    }
                }
            }
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
            Set<String> tempPool4IP = new HashSet<String>();
            boolean deskmateCrossIPEnable = Boolean.getBoolean(ModelUtil.getSystemParameter("DESKMATE_WITH_DIFFERENT_IP", false));
            for (Player eachPlayer : playersInRoom.values()) {
                if (deskmateCrossIPEnable && !eachPlayer.isPuppet() && tempPool4IP.contains(eachPlayer.getIosession().getRemoteAddress().toString())) {
                    // 过滤IP相同的玩家，PUPPET除外
                    continue;
                } else if (deskmateCrossIPEnable && !eachPlayer.isPuppet()) {
                    tempPool4IP.add(eachPlayer.getIosession().getRemoteAddress().toString());
                }
                if (GameStatus.MATCHING.equals(eachPlayer.getCurrentStatus())) {
                    playersInQueue.add(eachPlayer);
                }
            }
            // 按照等候的优先顺序进行排序，使先进入等待队列的玩家排在前面
            Collections.sort(playersInQueue, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    // 将PUPPET放置队列末端
                    if (p1.isPuppet() && !p2.isPuppet()) {
                        return -1;
                    } else if (!p1.isPuppet() && p2.isPuppet()) {
                        return 1;
                    }
                    // 按最后的游戏时间排序
                    if (p1.getLastPlayTime() < p2.getLastPlayTime()) {
                        return 1;
                    } else if (p1.getLastPlayTime() > p2.getLastPlayTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            // 根据房间中等候游戏开始的玩家人数来计算组数
            int groupQuantity = new Integer(ModelUtil.getSystemParameter("WAITING_QUEUE_GROUP_QUANTITY", 1));
            if (playersInQueue.size() < Red5Game.PLAYER_COGAME_NUMBER * groupQuantity) {
                groupQuantity = playersInQueue.size() / Red5Game.PLAYER_COGAME_NUMBER;
                if (groupQuantity == 0) {
                    return;
                }
            }
            // 调整PUPPET位置，进行插队操作
            for (int i = 0; i < groupQuantity; i++) {
                Collections.swap(playersInQueue, groupQuantity * i, playersInQueue.size() - 1 - i);
            }
            // 按照系统设置的最大游戏开始人数进行人数截取
            playersInQueue = playersInQueue.subList(0, Red5Game.PLAYER_COGAME_NUMBER * groupQuantity);

            if (Boolean.valueOf(ModelUtil.getSystemParameter("WAITING_QUEUE_RANDOM_ENABLE").toLowerCase())) {
                // 将玩家再次随机调整顺序
                Collections.shuffle(playersInQueue);
            }
            // 重新调整PUPPET位置，每组分配一个
            Collections.sort(playersInQueue, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    // 将PUPPET放置队列末端
                    if (p1.isPuppet() && !p2.isPuppet()) {
                        return -1;
                    } else if (!p1.isPuppet() && p2.isPuppet()) {
                        return 1;
                    }
                    return 0;
                }
            });
            // 调整PUPPET位置，每组分配一个
            for (int i = 0; i < groupQuantity; i++) {
                Collections.swap(playersInQueue, groupQuantity * i, playersInQueue.size() - 1 - i);
            }

            // 开始游戏
            List<Player> playersInGroup = new ArrayList<Player>();
            for (int i = 0; i < playersInQueue.size(); i++) {
                playersInGroup.add(playersInQueue.get(i));
                if ((i + 1) % Red5Game.PLAYER_COGAME_NUMBER != 0) {
                    continue;
                }
                if (Boolean.valueOf(ModelUtil.getSystemParameter("PUPPETS_HAPPY_PROHIBIT", true))) {
                    // 禁止PUPPET自行娱乐
                    boolean allPuppets = true;
                    for (Player eachPlayer : playersInGroup) {
                        allPuppets = allPuppets && eachPlayer.isPuppet();
                    }
                    if (allPuppets) {
                        playersInGroup.clear();
                        continue;
                    }
                }
                if (new Boolean(ModelUtil.getSystemParameter("ALLOW_ONLY_ONE_PUPPET_ENGAGE", false)) && 
                        (playersInGroup.get(Red5Game.PLAYER_COGAME_NUMBER - 2).isPuppet() ||
                        playersInGroup.get(Red5Game.PLAYER_COGAME_NUMBER - 3).isPuppet())) {
                    continue;
                }
                // 根据玩家当前的所在的房间进来开始游戏
                String gameId = GamePool.prepareRed5Game(playersInGroup);
                for (Player eachPlayer : playersInGroup) {
                    // 向客户端发送游戏id，玩家编号以及游戏所需要的玩家人数
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
                Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle(playersInGroup.get(playersInGroup.size() - 1).isPuppet());
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
                String firstPlayerNumber = null;
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
                        // 如果当前尚未设置过首次发牌的玩家，并且在当前牌序中发现红桃十，则确定为首次发牌的玩家
                        firstPlayerNumber = playersInGame.get(m).getCurrentNumber();
                        isFirstOut = true;
                    }
                }
                // 广播首次发牌玩家并将各个玩家初始牌发送到各个玩家手中，以便游戏结束亮牌用
                for (Player eachPlayer : playersInGame) {
                    echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                    echoMessage.setContent(firstPlayerNumber + "~" + game.getGameRecord());
                    echoMessage.setResult(GAME_FIRST_PLAY);
                    sessionWrite(eachPlayer.getIosession(), echoMessage);
                }
                for (Player eachPlayer : playersInGroup) {
                    // 更改玩家状态
                    eachPlayer.setCurrentStatus(GameStatus.PLAYING);
                }
                playersInGroup.clear();
            }
            if (Boolean.valueOf(ModelUtil.getSystemParameter("RED5_DEADLY7_EXTINCT8", false))) {
                // 为七独八天设置积分
                GAME_DEADLY7_EXTINCT8(session, message, echoMessage);
            }
        }
    }

    public static final String GAME_DEADLY7_EXTINCT8 = "GAME_DEADLY7_EXTINCT8";
    /**
     * 七独八天
     * 
     * @param session
     * @param message
     * @param echoMessage
     * @throws Exception
     */
    public void GAME_DEADLY7_EXTINCT8(IoSession session, Red5GameMessage message, EchoMessage echoMessage) throws Exception {
        Player currentPlayer = ModelUtil.getPlayer(session);
        Red5Game game = GamePool.getGame(currentPlayer.getGameId(), Red5Game.class);
        List<Player> players = game.getPlayers();
        synchronized (players) {
            Iterator<Player> itr = players.iterator();
            // 保存七独八天的积分
            String result = game.persistGameDeadly7Extinct8();
            while (itr.hasNext()) {
                Player player = itr.next();
                if (currentPlayer.equals(player)) {
                    continue;
                }
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_DEADLY7_EXTINCT8);
                echoMessage.setContent(result);
                sessionWrite(player.getIosession(), echoMessage);
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
        List<Player> players = game.getPlayers();
        synchronized (players) {
            for (Player eachPlayer : players) {
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_SETTING_OVER);
                echoMessage.setContent(playerNumber + "~" + settingValue);
                sessionWrite(eachPlayer.getIosession(), echoMessage);
            }
        }
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
            String content = message.getContent();
            while (itr.hasNext()) {
                Player player = itr.next();
                player.setCurrentStatus(GameStatus.IDLE);
                echoMessage = F3ServerMessage.createInstance(MessageType.RED5GAME).getEchoMessage();
                echoMessage.setResult(GAME_OVER);
                echoMessage.setContent(content + "~" + game.getGameDetailScore(player.getCurrentNumber()));
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
