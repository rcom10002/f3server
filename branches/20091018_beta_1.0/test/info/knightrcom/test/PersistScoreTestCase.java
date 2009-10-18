package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import junit.framework.TestCase;

/**
 * 积分计算测试
 */
public class PersistScoreTestCase extends TestCase {

    protected void setUp() throws Exception {
        HibernateSessionFactory.getSession().beginTransaction();
        super.setUp();
    }

    /**
     * @param game
     * @param playersNumber
     */
    protected void addGamePlayer(Game<?> game, int playersNumber) {
    	for (int i = 0; i < playersNumber; i++) {
    		final int currentIndex = i + 1;
            game.getPlayers().add(new Player() {
                {
                    this.id = "user" + currentIndex;
                    this.setCurrentNumber(currentIndex);
                }
            });
            game.getPlayerNumberMap().put(String.valueOf(currentIndex), new Player() {
                {
                    this.id = "user" + currentIndex;
                }
            });
		}
    }

    protected void tearDown() throws Exception {
        HibernateSessionFactory.closeSession();
        super.tearDown();
    }
}
