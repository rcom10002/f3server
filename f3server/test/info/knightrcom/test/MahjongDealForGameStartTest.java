package info.knightrcom.test;

import info.knightrcom.command.handler.game.PushdownWinGameInMessageHandler;
import info.knightrcom.model.game.pushdownwin.PushdownWinMahjong;
import junit.framework.TestCase;

public class MahjongDealForGameStartTest extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testShuffle() throws Exception {
		PushdownWinMahjong[][] mahjongs = PushdownWinMahjong.shuffle();
		printValues(mahjongs);
	}

	/**
	 * @throws Exception
	 */
	public void testMahjongsAtGameStart() throws Exception {
		PushdownWinMahjong[][] mahjongs = PushdownWinMahjong.shuffle();
		printValues(mahjongs);
		new PushdownWinGameInMessageHandler();
        PushdownWinMahjong[][] eachShuffledMahjongs = PushdownWinMahjong.shuffle();
        // 开始发牌
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < eachShuffledMahjongs.length; i++) {
            for (int j = 0; j < eachShuffledMahjongs[i].length; j++) {
                builder.append(eachShuffledMahjongs[i][j].getValue() + ",");
            }
        }
        System.out.println("SESSION CONTENT: " + builder.toString().replaceFirst(",$", ""));
	}

	private void printValues(PushdownWinMahjong[][] mahjongs) {
		for (PushdownWinMahjong[] currentMahjongs : mahjongs) {
			for (PushdownWinMahjong mahjong : currentMahjongs) {
				System.out.print(mahjong.getValue() + "\t\t");
			}
			System.out.println();
		}
	}
}
