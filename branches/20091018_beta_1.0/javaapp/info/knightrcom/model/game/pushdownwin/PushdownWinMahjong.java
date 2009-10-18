package info.knightrcom.model.game.pushdownwin;

import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.model.plaything.MahjongValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 推到胡麻将
 */
public class PushdownWinMahjong {

	private MahjongValue value;

	public PushdownWinMahjong(MahjongValue value) {
		this.value = value;
	}

	public MahjongValue getValue() {
		return value;
	}

	public void setValue(MahjongValue value) {
		this.value = value;
	}

    /**
     * 正常洗牌
     * 
     * @return
     */
    public static PushdownWinMahjong[][] shuffle() {
        // 准备洗牌用的麻将
        List<PushdownWinMahjong> mahjongs = new ArrayList<PushdownWinMahjong>();
        // 每张牌重复四次
        for (int i = 0; i < 4; i++) {
            for (MahjongValue mahjongValue: MahjongValue.values()) {
            	mahjongs.add(new PushdownWinMahjong(mahjongValue));
            }
        }
        // 开始随机洗牌
        Collections.shuffle(mahjongs);
        // 开始发牌，每人13张牌
        PushdownWinMahjong[][] eachShuffledPokers = new PushdownWinMahjong[PushdownWinGame.PLAYER_COGAME_NUMBER + 1][13];
        for (int i = 0; i < PushdownWinGame.PLAYER_COGAME_NUMBER; i++) {
            for (int j = 0; j < 13; j++) {
            	eachShuffledPokers[i][j] = mahjongs.remove(mahjongs.size() - 1);
            }
        }
        PushdownWinMahjong[] canvasMahjongs = new PushdownWinMahjong[mahjongs.size()];
        mahjongs.toArray(canvasMahjongs);
        eachShuffledPokers[PushdownWinGame.PLAYER_COGAME_NUMBER] = canvasMahjongs;
        return eachShuffledPokers;
    }

    public static void main(String[] args) {
    	shuffle();
    }

    /**
     * 按GM需求洗牌
     * 
     * @return
     */
    public static Red5Poker[][] shuffle(String cheatCards) {
        return null;
    }
}
