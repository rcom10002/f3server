package info.knightrcom.model.game.pushdownwin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import info.knightrcom.model.game.red5.Red5Game;
import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.model.plaything.MahjongValue;
import info.knightrcom.model.plaything.PokerColor;
import info.knightrcom.model.plaything.PokerValue;

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
        List<MahjongValue> mahjongs = new ArrayList<MahjongValue>();
        // 每张牌重复四次
        for (int i = 0; i < 4; i++) {
            for (MahjongValue mahjongValue: MahjongValue.values()) {
            	mahjongs.add(mahjongValue);
            }
        }
        // 开始随机洗牌
        Collections.shuffle(mahjongs);
        System.out.println();
        // 开始发牌
        PushdownWinMahjong[][] eachShuffledPokers = new PushdownWinMahjong[PushdownWinGame.PLAYER_COGAME_NUMBER][34];
        int currentSide = 0;
        for (int i = 0; i < mahjongs.size(); i+=Red5Game.PLAYER_COGAME_NUMBER) {
            for (int j = 0; j < eachShuffledPokers.length; j++) {
                if (i + j == mahjongs.size()) {
                    break;
                }
                eachShuffledPokers[j][currentSide] = mahjongs.get(i + j);
            }
            currentSide++;
        }
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
