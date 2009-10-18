package info.knightrcom.test;

import info.knightrcom.model.game.red5.Red5Poker;
import junit.framework.TestCase;

public class Red5DealForGameStartTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testMahjongsAtGameStart() throws Exception {
        Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle();
        // 取得合作玩家手中所持有的牌数
        String pokerNumberOfEachPlayer = "";
        for (int i = 0; i < eachShuffledPokers.length; i++) {
            int lastIndex = eachShuffledPokers[i].length - 1;
            pokerNumberOfEachPlayer += i + "=";
            if (eachShuffledPokers[i][lastIndex] == null) {
                pokerNumberOfEachPlayer += (eachShuffledPokers[i].length - 1) + ","; 
            } else {
                pokerNumberOfEachPlayer += eachShuffledPokers[i].length + ",";
            }
        }
        pokerNumberOfEachPlayer = pokerNumberOfEachPlayer.replaceFirst(",$", "");
        // 开始发牌 
        for (int i = 0; i < eachShuffledPokers.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < eachShuffledPokers[i].length; j++) {
                builder.append(eachShuffledPokers[i][j].getValue() + ",");
            }
            System.out.println("echoMessage.setContent(" + builder.toString().replaceFirst(",$", "~") + pokerNumberOfEachPlayer + ")");
        }
    }

}
