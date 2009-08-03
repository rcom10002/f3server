package info.knightrcom.command.handler.game;

import info.knightrcom.command.handler.PlatformInMessageHandler;
import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.command.message.PlatformMessage;
import info.knightrcom.command.message.F3ServerMessage.MessageType;
import info.knightrcom.command.message.game.FightLandlordGameMessage;
import info.knightrcom.data.HibernateTransactionSupport;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.mina.core.session.IoSession;

/**
 * 斗地主消息控制句柄
 */
public class FightLandlordGameInMessageHandler extends
		GameInMessageHandler<FightLandlordGameMessage> {
	
	public static final String GAME_SETTING_UPDATE_FINISH = "GAME_SETTING_UPDATE_FINISH";
	
	public static final String GAME_BOMB = "GAME_BOMB";

	@Override
	public void GAME_JOIN_MATCHING_QUEUE(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
		// 按优先级别选择游戏玩家进行游戏安排
		// TODO 玩家进入游戏等待队列
		// 判断当前房间内等候的玩家个数是否足够以开始游戏
		Player currentPlayer = ModelUtil.getPlayer(session);
		currentPlayer.setCurrentStatus(GameStatus.MATCHING);
		Room currentRoom = currentPlayer.getCurrentRoom();
		if (currentRoom.getGameStatusNumber(GameStatus.MATCHING) < FightLandlordGame.PLAYER_COGAME_NUMBER) {
			String content = "当前房间等候的玩家数("
					+ currentRoom.getGameStatusNumber(GameStatus.MATCHING)
					+ ")不足以开始新的游戏，请稍候。";
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
			playersInQueue = playersInQueue.subList(0,
					FightLandlordGame.PLAYER_COGAME_NUMBER);
			// 根据玩家当前的所在的房间进来开始游戏
			GamePool.prepareFightLandlordGame(playersInQueue);
			for (Player eachPlayer : playersInQueue) {
				// 向客户端发送游戏id，玩家编号以及游戏所需要的玩家人数
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_CREATE);
				echoMessage.setContent(eachPlayer.getGameId() + "~"
						+ eachPlayer.getCurrentNumber() + "~"
						+ FightLandlordGame.PLAYER_COGAME_NUMBER);
				sessionWrite(eachPlayer.getIosession(), echoMessage);
			}
		}
		// 根据当前触发游戏开始的玩家所携带的游戏id来取得游戏实例
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		List<Player> playersInGame = game.getPlayers();
		// 开始洗牌与发牌，排序功能与出牌规则在客户端完成
		boolean isFirstOut = false;
		FightLandlordPoker[][] eachShuffledPokers = FightLandlordPoker
				.shuffle();
		// 取得合作玩家牌数
		String pokerNumberOfEachPlayer = "";
		for (int i = 0; i < eachShuffledPokers.length; i++) {
			int lastIndex = eachShuffledPokers[i].length - 1;
			pokerNumberOfEachPlayer += i + "=";
			if (eachShuffledPokers[i][lastIndex] == null) {
				pokerNumberOfEachPlayer += (eachShuffledPokers[i].length - 1)
						+ ",";
			} else {
				pokerNumberOfEachPlayer += eachShuffledPokers[i].length + ",";
			}
		}
		pokerNumberOfEachPlayer = pokerNumberOfEachPlayer
				.replaceFirst(",$", "");
		// 开始发牌
		for (int i = 0; i < eachShuffledPokers.length; i++) {
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < eachShuffledPokers[i].length; j++) {
				builder.append(eachShuffledPokers[i][j].getValue() + ",");
			}
			echoMessage = F3ServerMessage.createInstance(
					MessageType.FIGHT_LANDLORD).getEchoMessage();
			echoMessage.setResult(GAME_STARTED);
			echoMessage.setContent(builder.toString().replaceFirst(",$", "~")
					+ pokerNumberOfEachPlayer);
			sessionWrite(playersInGame.get(i).getIosession(), echoMessage);
			 // 记录游戏初始时玩家手中的牌信息
            game.appendGameRecord(echoMessage.getContent());
			if (!isFirstOut
					&& builder
							.indexOf(FightLandlordGame.START_POKER.getValue()) > -1) {
				// 如果当前尚未设置过首次发牌的玩家，并且在当前牌序中发现红桃3，则为首次发牌的玩家发送消息
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_FIRST_PLAY);
				sessionWrite(playersInGame.get(i).getIosession(), echoMessage);
				isFirstOut = true;
			}
			// FIXME 重新进行逻辑描述，我看不明白，注释太长的话需要用多行表现，那样不需要滚动查看
			// 由于斗地主在发牌前会扣掉三张底牌，所以可能会将首发牌标志牌扣掉，所以为防止这种情况发生，采用随机设置一个玩家为游戏初始设置玩家
			if (i == eachShuffledPokers.length - 1 && !isFirstOut) {
				int rndIndex = new Random().nextInt(eachShuffledPokers.length);
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_FIRST_PLAY);
				sessionWrite(playersInGame.get(rndIndex).getIosession(), echoMessage);
				isFirstOut = true;
				game.appendGameRecord(echoMessage.getContent());
			}
		}

		// if (new Random(10).nextInt() > 4) {
		// echoMessage.setResult(GAME_WAIT);
		// echoMessage.setContent("GAME_JOIN_MATCHING_QUEUE请稍候！");
		// sessionWrite(session, echoMessage);
		// log.debug("GAME_JOIN_MATCHING_QUEUE稍候");
		// return;
		// } else {
		// echoMessage =
		// F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
		// echoMessage.setResult(GAME_CREATE);
		// sessionWrite(session, echoMessage);
		// log.debug("GAME_JOIN_MATCHING_QUEUE创建");
		// echoMessage =
		// F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
		// echoMessage.setResult(GAME_STARTED);
		// sessionWrite(session, echoMessage);
		// log.debug("GAME_JOIN_MATCHING_QUEUE开始");
		// }
	}

	@Override
	public void GAME_SETTING(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
		// 在游戏开始前进行本次设置[不叫|1分|2分|3分]
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		List<Player> players = game.getPlayers();
		synchronized (players) {
			Iterator<Player> itr = players.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				if (currentPlayer.equals(player)) {
					continue;
				}
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_SETTING_UPDATE);
				echoMessage.setContent(message.getContent());
				sessionWrite(player.getIosession(), echoMessage);
			}
			// 记录当前牌序
			game.appendGameRecord(message.getContent());
		}

		// echoMessage =
		// F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
		// echoMessage.setResult(GAME_SETTING_UPDATE);
		// echoMessage.setContent(message.getContent());
		// sessionWrite(session, echoMessage);
	}
	
	public void GAME_BOMB(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
		// 在游戏过程中出牌翻倍
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		List<Player> players = game.getPlayers();
		String[] results = message.getContent().split("~");
		// 判断是否有炸弹，火箭
		// 地主把牌出完，其余两家一张牌都没出，分数×2 ；
		// 两家中有一家出完牌，而地主仅仅出过一手牌，分数×2 。
		if (results.length == 4 && "double".equals(results[3])) {
			game.addMultiple();
		}
		synchronized (players) {
			Iterator<Player> itr = players.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				if (currentPlayer.equals(player)) {
					continue;
				}
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_BOMB);
				echoMessage.setContent(message.getContent());
				sessionWrite(player.getIosession(), echoMessage);
			}
			// 记录当前牌序
			game.appendGameRecord(message.getContent());
		}

	}

	@Override
	public void GAME_SETTING_FINISH(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
		// 玩家游戏设置结束
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		String[] results = message.getContent().split("~");
		// 游戏最终设置的玩家序号，首次发牌玩家序号
		String playerNumber = results[0];
		// 当前游戏设置
		int settingValue = Integer.parseInt(results[1]);
		FightLandlordGameSetting setting = FightLandlordGameSetting
				.fromOrdinal(settingValue);
		setting.setPlayerNumber(playerNumber);
		game.setSetting(setting);
		log.debug(setting);

		// 为地主发底牌,并为其它两家显示底牌
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < FightLandlordPoker.handlePokers.length; i++) {
			builder.append(FightLandlordPoker.handlePokers[i].getValue() + ",");
		}
		List<Player> players = game.getPlayers();
		synchronized (players) {
			Iterator<Player> itr = players.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_SETTING_UPDATE_FINISH);
				echoMessage.setContent(builder.toString().replaceFirst(",$",
						"~")
						+ playerNumber);
				sessionWrite(player.getIosession(), echoMessage);
			}
			// 记录当前牌序
			game.appendGameRecord(message.getContent());
		}
	}

	@Override
	public void GAME_START(IoSession session, FightLandlordGameMessage message,
			EchoMessage echoMessage) throws Exception {
	}

	@Override
	public void GAME_BRING_OUT(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
		// 向游戏中的其他玩家发送消息
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		
		List<Player> players = game.getPlayers();
		synchronized (players) {
			Iterator<Player> itr = players.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				if (currentPlayer.equals(player)) {
					continue;
				}
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_BRING_OUT);
				echoMessage.setContent(message.getContent());
				sessionWrite(player.getIosession(), echoMessage);
			}
			// 记录当前牌序
			game.appendGameRecord(message.getContent());
		}

		// echoMessage =
		// F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
		// echoMessage.setResult(GAME_BRING_OUT);
		// echoMessage.setContent(message.getContent());
		// sessionWrite(session, echoMessage);
	}

	@Override
	public void GAME_WIN(IoSession session, FightLandlordGameMessage message,
			EchoMessage echoMessage) throws Exception {
		// 向游戏中的其他玩家发送消息
		Player currentPlayer = ModelUtil.getPlayer(session);
		FightLandlordGame game = GamePool.getGame(currentPlayer.getGameId(),
				FightLandlordGame.class);
		// 判断是否为最终获胜
		if (!FightLandlordGameSetting.NO_RUSH.equals(game.getSetting())) {
			// 立即结束当前游戏
			GAME_WIN_AND_END(session, message, echoMessage);
			return;
		} else {
			game.addWinnerNumber(String.valueOf(currentPlayer
					.getCurrentNumber()));
			List<Player> players = game.getPlayers();
			synchronized (players) {
				Iterator<Player> itr = players.iterator();
				while (itr.hasNext()) {
					Player player = itr.next();
					if (currentPlayer.equals(player)) {
						continue;
					}
					echoMessage = F3ServerMessage.createInstance(
							MessageType.FIGHT_LANDLORD).getEchoMessage();
					echoMessage.setResult(GAME_WINNER_PRODUCED);
					echoMessage.setContent(message.getContent());
					sessionWrite(player.getIosession(), echoMessage);
				}
				// 记录当前牌序
				game.appendGameRecord(message.getContent());
			}
		}

		// echoMessage =
		// F3ServerMessage.createInstance(MessageType.FIGHT_LANDLORD).getEchoMessage();
		// echoMessage.setResult(GAME_WINNER_PRODUCED);
		// echoMessage.setContent(message.getContent());
		// sessionWrite(session, echoMessage);
	}

	@Override
    @HibernateTransactionSupport
	public void GAME_WIN_AND_END(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
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
            message.setContent(message.getContent() + "~" + game.getGameDetailScore());
			// 设置玩家得分
			while (itr.hasNext()) {
				Player player = itr.next();
				echoMessage = F3ServerMessage.createInstance(
						MessageType.FIGHT_LANDLORD).getEchoMessage();
				echoMessage.setResult(GAME_OVER);
				// 显示游戏积分
				echoMessage.setContent(message.getContent());
				sessionWrite(player.getIosession(), echoMessage);
			}
		}
		// 清除内存中本次游戏的相关信息
		log.debug(game.getGameRecord());
		log.debug(game.getWinnerNumbers());
		GamePool
				.distroyGame(currentPlayer.getGameId(), FightLandlordGame.class);
	}

	@Override
	public void GAME_PLAYER_LOST_CONNECTION(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
		// 通知其他玩家并为其他玩家分配分数
		Player player = ModelUtil.getPlayer(session);
		if (GamePool.getGame(player.getGameId(), FightLandlordGame.class) != null) {
			// 扣除玩家分数，并为游戏中的其他玩家分配分数
			// TODO
		} else {
			// 非游戏中掉线的情况，通知其他玩家在线人数发生了变化
			PlatformMessage localMessage = (PlatformMessage) F3ServerMessage
					.createInstance(MessageType.PLATFORM);
			new PlatformInMessageHandler().PLATFORM_PLAYER_LOST_CONNECTION(
					session, localMessage, localMessage.getEchoMessage());
		}
	}

	@Override
	public void GAME_CHEAT_FOUND(IoSession session,
			FightLandlordGameMessage message, EchoMessage echoMessage)
			throws Exception {
	}
}
