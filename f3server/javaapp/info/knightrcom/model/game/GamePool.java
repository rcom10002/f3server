package info.knightrcom.model.game;

import info.knightrcom.model.game.fightlandlord.FightLandlordGame;
import info.knightrcom.model.game.pushdownwin.PushdownWinGame;
import info.knightrcom.model.game.qiongwin.QiongWinGame;
import info.knightrcom.model.game.red5.Red5Game;
import info.knightrcom.model.global.GameStatus;
import info.knightrcom.model.global.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 游戏池，对正在进行游戏的进行管理
 */
public class GamePool {

    private static final Map<String, Object> games = Collections.synchronizedSortedMap(new TreeMap<String, Object>());

    /**
     * FIXME
     * 
     * @param players
     */
    public static synchronized void prepareGame(List<Player> players) {
    	
    }

    /**
     * 处理游戏中的通用设置，将特殊设置放于私有方法postInitProcess中
     * 
     * @param players
     */
    public static String prepareRed5Game(List<Player> players) {
        // 创建游戏信息
        Red5Game game = new Red5Game();
        String gameId = game.getId();
        games.put(gameId, game);
        int i = 0;
        for (Player player : players) {
            player.setGameId(gameId);
            player.setCurrentStatus(GameStatus.PLAYING);
            player.setCurrentNumber(++i);
            game.involvePlayer(player);
            game.getPlayerNumberMap().put(String.valueOf(i), player);
        }
        // 从房间中取得当前房间设置的游戏每局得分数以及游戏开始所需最小分数
        int gameMark = players.get(0).getParent().getGameMark();
        game.setGameMark(gameMark);
        int lowLevelMark = players.get(0).getParent().getGameLowLevelMark();
        int midLevelMark = players.get(0).getParent().getGameMidLevelMark();
        int highLevelMark = players.get(0).getParent().getGameHighLevelMark();
        game.setLowLevelMark(lowLevelMark);
        game.setMidLevelMark(midLevelMark);
        game.setHighLevelMark(highLevelMark);
        int minGameStartMark = players.get(0).getParent().getMinGameMarks();
        game.setMinGameStartMark(minGameStartMark);
        postInitProcess(game);
        return gameId;
    }

    /**
     * @param players
     */
    public static void prepareFightLandlordGame(List<Player> players) {
        FightLandlordGame game = new FightLandlordGame();
        String gameId = game.getId();
        games.put(gameId, game);
        int i = 0;
        for (Player player : players) {
            player.setGameId(gameId);
            player.setCurrentStatus(GameStatus.PLAYING);
            player.setCurrentNumber(++i);
            game.involvePlayer(player);
            game.getPlayerNumberMap().put(String.valueOf(i), player);
        }
        int gameMark = players.get(0).getParent().getGameMark();
        int lowLevelMark = players.get(0).getParent().getGameLowLevelMark();
        int midLevelMark = players.get(0).getParent().getGameMidLevelMark();
        int highLevelMark = players.get(0).getParent().getGameHighLevelMark();
        game.setGameMark(gameMark);
        game.setLowLevelMark(lowLevelMark);
        game.setMidLevelMark(midLevelMark);
        game.setHighLevelMark(highLevelMark);
        int minGameMarks = players.get(0).getParent().getMinGameMarks();
        game.setMinGameStartMark(minGameMarks);
        postInitProcess(game);
    }

    /**
     * 处理游戏中的通用设置，将特殊设置放于私有方法postInitProcess中
     * 
     * @param players
     */
    public static void preparePushdownWinGame(List<Player> players) {
        // 创建游戏信息
        PushdownWinGame game = new PushdownWinGame();
        String gameId = game.getId();
        games.put(gameId, game);
        int i = 0;
        for (Player player : players) {
            player.setGameId(gameId);
            player.setCurrentStatus(GameStatus.PLAYING);
            player.setCurrentNumber(++i);
            game.involvePlayer(player);
            game.getPlayerNumberMap().put(String.valueOf(i), player);
        }
        // 从房间中取得当前房间设置的游戏每局得分数以及游戏开始所需最小分数
        int gameMark = players.get(0).getParent().getGameMark();
        game.setGameMark(gameMark);
        int minGameStartMark = players.get(0).getParent().getMinGameMarks();
        game.setMinGameStartMark(minGameStartMark);
        postInitProcess(game);
    }

    /**
     * 处理游戏中的通用设置，将特殊设置放于私有方法postInitProcess中
     * 
     * @param players
     */
    public static void prepareQiongWinGame(List<Player> players) {
        // 创建游戏信息
        QiongWinGame game = new QiongWinGame();
        String gameId = game.getId();
        games.put(gameId, game);
        int i = 0;
        for (Player player : players) {
            player.setGameId(gameId);
            player.setCurrentStatus(GameStatus.PLAYING);
            player.setCurrentNumber(++i);
            game.involvePlayer(player);
            game.getPlayerNumberMap().put(String.valueOf(i), player);
        }
        // 从房间中取得当前房间设置的游戏每局得分数以及游戏开始所需最小分数
        int gameMark = players.get(0).getParent().getGameMark();
        game.setGameMark(gameMark);
        int minGameStartMark = players.get(0).getParent().getMinGameMarks();
        game.setMinGameStartMark(minGameStartMark);
        postInitProcess(game);
    }
    
    /**
     * 
     */
    private static <T extends Game<?>> void postInitProcess(T game) {
        // FIXME 该方法可以移植到AbstractGame内部，作为抽象方法供子类实现
        if (game instanceof Red5Game) {
            
        } else if (game instanceof FightLandlordGame) {
            
        } else if (game instanceof PushdownWinGame) {
            
        } else if (game instanceof QiongWinGame) {
            
        } else {
            //throw new runtim
        }
    }

    /**
     * @param <T>
     * @param gameId
     * @param gameType
     * @return
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T extends Game> T getGame(String gameId, Class<T> gameType) {
        return gameType.cast(games.get(gameId));
    }

    /**
     * @param gameId
     */
    public static synchronized <T extends Game<?>> void distroyGame(String gameId, Class<T> gameType) {
        T game = gameType.cast(games.remove(gameId));
        if (game != null) {
            game.getPlayers().clear();
        }
    }
}
