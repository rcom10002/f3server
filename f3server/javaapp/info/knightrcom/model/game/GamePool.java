package info.knightrcom.model.game;

import info.knightrcom.model.game.fightlandlord.FightLandlordGame;
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
     * 处理游戏中的通用设置，将特殊设置放于私有方法postInitProcess中
     * 
     * @param players
     */
    public static synchronized void prepareGame(List<Player> players) {
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
        int minGameStartMark = players.get(0).getParent().getMinGameMarks();
        game.setMinGameStartMark(minGameStartMark);
        postInitProcess(game);
    }

    /**
     * @param players
     */
    public static synchronized void prepareFightLandlordGame(List<Player> players) {
        FightLandlordGame game = new FightLandlordGame();
        String gameId = game.getId();
        games.put(gameId, game);
        int i = 0;
        for (Player player : players) {
            player.setGameId(gameId);
            player.setCurrentStatus(GameStatus.PLAYING);
            player.setCurrentNumber(++i);
            game.involvePlayer(player);
        }
        int gameMark = players.get(0).getParent().getGameMark();
        game.setGameMark(gameMark);
        int minGameMarks = players.get(0).getParent().getMinGameMarks();
        game.setMinGameStartMark(minGameMarks);
        postInitProcess(game);
    }
    
    /**
     * 
     */
    private static <T extends Game<?>> void postInitProcess(T game) {
        // FIXME 该方法可以移植到AbstractGame内部，作为抽象方法供子类实现
        if (game instanceof Red5Game) {
            
        } else if (game instanceof FightLandlordGame) {
            
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
        game.getPlayers().clear();
        games.remove(gameId);
    }
}
