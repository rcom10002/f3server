package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.FightLandlordGameMessage;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.HibernateTransactionSupport;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.model.game.GamePool;
import info.knightrcom.model.game.fightlandlord.FightLandlordGame;
import info.knightrcom.model.game.fightlandlord.FightLandlordGameSetting;
import info.knightrcom.model.game.fightlandlord.FightLandlordPoker;
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
 * 斗地主消息控制句柄
 */
public class FightLandlordGameInMessageHandler extends GameInMessageHandler<FightLandlordGameMessage> {
	
	public static final String GAME_SETTING_UPDATE_FINISH = "GAME_SETTING_UPDATE_FINISH";
	
	public static final String GAME_BOMB = "GAME_BOMB";
	
	@Override
	public void GAME_JOIN_MATCHING_QUEUE(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 判断当前玩家是否有足够分数加入游戏
        Player currentPlayer = ModelUtil.getPlayer(session);

        if (!GameStatus.IDLE.equals(currentPlayer.getCurrentStatus())) {
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
        
        if (currentRoom.getGameStatusNumber(GameStatus.MATCHING) < FightLandlordGame.PLAYER_COGAME_NUMBER * groupQuantity) {
            int numPlayers = currentRoom.getGameStatusNumber(GameStatus.MATCHING);
            numPlayers += Math.ceil(numPlayers / (FightLandlordGame.PLAYER_COGAME_NUMBER - 1));
            int matchingRate = (int)Math.round((double)numPlayers / (FightLandlordGame.PLAYER_COGAME_NUMBER * groupQuantity) * 100);
            if (matchingRate == 100) {
                matchingRate = (int)Math.round(((double)numPlayers - new Integer(ModelUtil.getSystemParameter("WAITING_QUEUE_GROUP_QUANTITY"))) / 
                        (FightLandlordGame.PLAYER_COGAME_NUMBER * groupQuantity) * 100);
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
                    currentPlayer = ModelUtil.getPlayer(session);
                    if (currentPlayer != null && GameStatus.MATCHING.equals(currentPlayer.getCurrentStatus())) {
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
    public synchronized void GAME_START(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
        // 取得玩家所在房间内所有的玩家
        Map<String, Player> playersInRoom = ModelUtil.getPlayer(session).getCurrentRoom().getChildren();
        synchronized (playersInRoom) {
            // 取得当前房间内的等待队列中的玩家
        	List<Player> playersInQueue = new ArrayList<Player>();
            Set<String> tempPool4IP = new HashSet<String>();
            // FIXME This line should rewrite when IP excluded parameter is added!
            boolean sameIPexcluded = ModelUtil.getSystemParameter("") != null ? Boolean.getBoolean(ModelUtil.getSystemParameter("").toLowerCase()) : false;
            for (Player eachPlayer : playersInRoom.values()) {
                if (sameIPexcluded && tempPool4IP.contains(eachPlayer.getIosession().getRemoteAddress().toString())) {
                    // 过滤IP相同的玩家
                    continue;
                } else if (sameIPexcluded) {
                    tempPool4IP.add(eachPlayer.getIosession().getRemoteAddress().toString());
                }
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
            int groupQuantity = new Integer(ModelUtil.getSystemParameter("WAITING_QUEUE_GROUP_QUANTITY"));
            if (playersInQueue.size() < FightLandlordGame.PLAYER_COGAME_NUMBER * groupQuantity) {
                groupQuantity = playersInQueue.size() / FightLandlordGame.PLAYER_COGAME_NUMBER;
                if (groupQuantity == 0) {
                    return;
                }
            }
            playersInQueue = playersInQueue.subList(0, FightLandlordGame.PLAYER_COGAME_NUMBER * groupQuantity);

            
            if ("true".equals(ModelUtil.getSystemParameter("WAITING_QUEUE_RANDOM_ENABLE").toLowerCase())) {
                // 将玩家再次随机调整顺序
                Collections.shuffle(playersInQueue);
            }
            List<Player> playersInGroup = new ArrayList<Player>();
            for (int i = 0; i < playersInQueue.size(); i++) {
                playersInGroup.add(playersInQueue.get(i));
                if ((i + 1) % FightLandlordGame.PLAYER_COGAME_NUMBER != 0) {
                    continue;
                }
                // 根据玩家当前的所在的房间进来开始游戏
                String gameId = GamePool.prepareFightLandlordGame(playersInGroup);
                for (Player eachPlayer : playersInGroup) {
                    // 向客户端发送游戏id，玩家编号以及游戏所需要的玩家人数
                    eachPlayer.setCurrentStatus(GameStatus.PLAYING);
                    echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
                    echoMessage.setResult(GAME_CREATE);
                    echoMessage.setContent(
                            eachPlayer.getGameId() + "~" + 
                            eachPlayer.getCurrentNumber() + "~" + 
                            FightLandlordGame.PLAYER_COGAME_NUMBER);
                    sessionWrite(eachPlayer.getIosession(), echoMessage);
                }
                // 根据当前触发游戏开始的玩家所携带的游戏id来取得游戏实例
                FightLandlordGame game = GamePool.getGame(gameId, FightLandlordGame.class);
                List<Player> playersInGame = game.getPlayers();
                // 开始洗牌与发牌，排序功能与出牌规则在客户端完成
                boolean isFirstOut = false;
                FightLandlordPoker[][] eachShuffledPokers = FightLandlordPoker.shuffle();
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
                StringBuilder builderTemp = new StringBuilder();
                String playerCards[] = new String[eachShuffledPokers.length];
                for (int m = 0; m < eachShuffledPokers.length; m++) {
                    StringBuilder builder = new StringBuilder();
                    for (int n = 0; n < eachShuffledPokers[m].length; n++) {
                    	builderTemp.append(eachShuffledPokers[m][n].getValue() + ",");
                    	builder.append(eachShuffledPokers[m][n].getValue() + ",");
                    }
                    playerCards[m] = builder.toString();
                }
                // 由于斗地主在发牌前会扣掉三张底牌，所以可能会将首发牌标志牌(红桃3)扣掉。
                // 确定开始首次发牌标识
                // 判断当前17 * 3 的扑克中是否包括发牌标识
                String currentAllCard = builderTemp.toString();
                boolean boolPK = false;
                // 是否包括红桃3标识牌
                String startPorker = FightLandlordGame.START_POKER.getValue();
                if (currentAllCard.matches("^.*" + startPorker  + ".*$")) {
                	boolPK = true;
                }
                // 是否包括方块3标识牌
                if (!boolPK && currentAllCard.matches("^.*" + FightLandlordGame.START_POKER_DIAMOND.getValue()  + ".*$")) {
                	startPorker = FightLandlordGame.START_POKER_DIAMOND.getValue();
                    boolPK = true;
                }
             	// 是否包括黑桃3标识牌
                if (!boolPK && currentAllCard.matches("^.*" + FightLandlordGame.START_POKER_SPADE.getValue()  + ".*$")) {
                	startPorker = FightLandlordGame.START_POKER_SPADE.getValue();
                	boolPK = true;
                }
                // 不包括前三种花色则直接设定梅花3为首发牌标识
                if (!boolPK) {
                	startPorker = FightLandlordGame.START_POKER_CLUB.getValue();
                }
                for (int x = 0; x < eachShuffledPokers.length; x++) {
                    // 为每位玩家发牌
                    echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
                    echoMessage.setResult(GAME_STARTED);
                    echoMessage.setContent(playerCards[x].replaceFirst(",$", "~") + pokerNumberOfEachPlayer + "~" + startPorker);
                    sessionWrite(playersInGame.get(x).getIosession(), echoMessage);
                    // 记录游戏初始时玩家手中的牌信息
                    game.appendGameRecord(echoMessage.getContent());
                    if (!isFirstOut && playerCards[x].indexOf(startPorker) > -1) {
                        // 如果当前尚未设置过首次发牌的玩家，并且在当前牌序中发现标识牌，则确定为首次发牌的玩家
                    	firstPlayerNumber = playersInGame.get(x).getCurrentNumber();
                        isFirstOut = true;
                    }
                }
                // 广播首次发牌玩家并将各个玩家初始牌发送到各个玩家手中，以便游戏结束亮牌用
                for (Player eachPlayer : playersInGame) {
                    echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
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
        }
    }
	
	@Override
	public void GAME_SETTING(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 在游戏开始前进行本次设置[不叫|1分|2分|3分]
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(), FightLandlordGame.class);
		List<Player> players = game.getPlayers();
		synchronized (players) {
			Iterator<Player> itr = players.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				if (currentPlayer.equals(player)) {
					continue;
				}
				echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_SETTING_UPDATE);
				echoMessage.setContent(message.getContent());
				sessionWrite(player.getIosession(), echoMessage);
			}
			// 记录当前牌序
			game.appendGameRecord(message.getContent());
		}
	}
	
	public void GAME_BOMB(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 在游戏过程中出牌翻倍
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(), FightLandlordGame.class);
		List<Player> players = game.getPlayers();
		String[] results = message.getContent().split("~");
		// 判断是否有炸弹，火箭
		// 地主把牌出完，其余两家一张牌都没出，分数×2 ；
		// 两家中有一家出完牌，而地主仅仅出过一手牌，分数×2 。
		if (results.length == 4 && "double".equals(results[3])) {
			game.addMultiple();
		}
		synchronized (players) {
			for (Player eachPlayer : players) {
				echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_BOMB);
				echoMessage.setContent(message.getContent() + "~" + game.getMultiple() );
				sessionWrite(eachPlayer.getIosession(), echoMessage);
			}
		}
	}
	
	@Override
	public void GAME_SETTING_FINISH(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 玩家游戏设置结束
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(), FightLandlordGame.class);
		String[] results = message.getContent().split("~");
		// 游戏最终设置的玩家序号，首次发牌玩家序号
		String playerNumber = results[0];
		// 当前游戏设置
		int settingValue = Integer.parseInt(results[1]);
		FightLandlordGameSetting setting = FightLandlordGameSetting.fromOrdinal(settingValue);
		setting.setPlayerNumber(playerNumber);
		game.setSetting(setting);
		List<Player> players = game.getPlayers();
        synchronized (players) {
            for (Player eachPlayer : players) {
                echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
                echoMessage.setResult(GAME_SETTING_OVER);
                echoMessage.setContent(playerNumber + "~" + settingValue);
                sessionWrite(eachPlayer.getIosession(), echoMessage);
            }
        }
		log.debug(setting);

		// 为地主发底牌,并为其它两家显示底牌
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < FightLandlordPoker.handlePokers.length; i++) {
			builder.append(FightLandlordPoker.handlePokers[i].getValue() + ",");
		}
		boolean isHandlerPokers = true;
		synchronized (players) {
			for (Player player : players) {
				echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_SETTING_UPDATE_FINISH);
				echoMessage.setContent(builder.toString().replaceFirst(",$","~") + playerNumber);
				sessionWrite(player.getIosession(), echoMessage);
				// 记录底牌
				if (isHandlerPokers) {
					game.appendGameRecord(echoMessage.getContent());
					isHandlerPokers = false;
				}
			}
		}
	}

	@Override
	public void GAME_BRING_OUT(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 向游戏中的其他玩家发送消息
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(), FightLandlordGame.class);
		
		List<Player> players = game.getPlayers();
		synchronized (players) {
			Iterator<Player> itr = players.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				if (currentPlayer.equals(player)) {
					continue;
				}
				echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_BRING_OUT);
				echoMessage.setContent(message.getContent());
				sessionWrite(player.getIosession(), echoMessage);
			}
			// 记录当前牌序
			game.appendGameRecord(message.getContent());
		}
	}

	@Override
	public void GAME_WIN(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 向游戏中的其他玩家发送消息
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(), FightLandlordGame.class);
		// 判断是否为最终获胜
		if (!FightLandlordGameSetting.NO_RUSH.equals(game.getSetting())) {
			// 立即结束当前游戏
			GAME_WIN_AND_END(session, message, echoMessage);
			return;
		} else {
			game.addWinnerNumber(String.valueOf(currentPlayer.getCurrentNumber()));
			List<Player> players = game.getPlayers();
			synchronized (players) {
				Iterator<Player> itr = players.iterator();
				while (itr.hasNext()) {
					Player player = itr.next();
					if (currentPlayer.equals(player)) {
						continue;
					}
					echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
					echoMessage.setResult(GAME_WINNER_PRODUCED);
					echoMessage.setContent(message.getContent());
					sessionWrite(player.getIosession(), echoMessage);
				}
				// 记录当前牌序
				game.appendGameRecord(message.getContent());
			}
		}
	}

	@Override
    @HibernateTransactionSupport
	public void GAME_WIN_AND_END(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 游戏结束，向游戏中的其他玩家发送消息
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		String[] results = message.getContent().split("~");
		List<Player> players = game.getPlayers();
		log.debug(game.getSetting().getDisplayName());
		// 记录当前牌序
		game.appendGameRecord(message.getContent());
		synchronized (players) {
			// 设置名次并计算积分
			if (FightLandlordGameSetting.NO_RUSH.equals(game.getSetting())) {
				// 1分，添加获胜者
				game.addWinnerNumber(results[0]);
			} else if (FightLandlordGameSetting.ONE_RUSH.equals(game
					.getSetting())) {
				// 1分，添加获胜者
				game.addWinnerNumber(results[0]);
			} else if (FightLandlordGameSetting.TWO_RUSH.equals(game
					.getSetting())) {
				// 2分，添加获胜者
				game.addWinnerNumber(results[0]);
			} else if (FightLandlordGameSetting.THREE_RUSH.equals(game
					.getSetting())) {
				// 3分，添加获胜者
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
                echoMessage = F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
                echoMessage.setResult(GAME_OVER);
                echoMessage.setContent(content + "~" + game.getGameDetailScore(player.getCurrentNumber()));
                sessionWrite(player.getIosession(), echoMessage);
            }
		}
		// 清除内存中本次游戏的相关信息
		log.debug(game.getGameRecord());
		log.debug(game.getWinnerNumbers());
		GamePool.distroyGame(currentPlayer.getGameId(), FightLandlordGame.class);
	}

	@Override
	public void GAME_PLAYER_LOST_CONNECTION(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
		// 通知其他玩家并为其他玩家分配分数
		Player player = ModelUtil.getPlayer(session);
		if (GamePool.getGame(player.getGameId(), FightLandlordGame.class) != null) {
			// 扣除玩家分数，并为游戏中的其他玩家分配分数
			// TODO
		} else {
			// 非游戏中掉线的情况，通知其他玩家在线人数发生了变化
			PlatformMessage localMessage = (PlatformMessage) F3ServerMessage.createInstance(MessageType.PLATFORM);
			new PlatformInMessageHandler().PLATFORM_PLAYER_LOST_CONNECTION(session, localMessage, localMessage.getEchoMessage());
		}
	}

	@Override
    public void GAME_CHEAT_FOUND(IoSession session, FightLandlordGameMessage message, EchoMessage echoMessage) throws Exception {
        // TODO Auto-generated method stub

    }
}
