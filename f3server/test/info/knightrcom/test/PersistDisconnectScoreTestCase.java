package info.knightrcom.test;

import junit.framework.TestCase;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.game.red5.Red5Game;
import info.knightrcom.model.game.red5.Red5GameSetting;
import info.knightrcom.model.global.Player;

/**
 * 掉线积分计算测试
 */
public class PersistDisconnectScoreTestCase extends TestCase {

    protected void setUp() throws Exception {
        HibernateSessionFactory.init();
        HibernateSessionFactory.getSession().beginTransaction();
        super.setUp();
    }

    /**
     * 红五掉线积分计算测试
     * 
     * 玩家2掉线，扣除基本分×当前游戏等级：5 × 4
     * 
     * 另扣除基本分：5，作为系统分
     */
    public void testRed5GamePersistDisconnectScore() throws Exception {
        Red5Game game = new Red5Game();
        game.setSetting(Red5GameSetting.RUSH);
        game.getSetting().setPlayerNumber("1");
        game.setGameMark(10);
        game.setLowLevelMark(3);
        addGamePlayer(game);
        // 测试游戏模型的创建时间与最终更新时间差
        Thread.sleep(1000);
        game.persistDisconnectScore(game.getPlayerNumberMap().get("2"));
    }

    /**
     * @param game
     */
    private void addGamePlayer(Game<?> game) {
        game.getPlayers().add(new Player() {
            {
                this.id = "user1";
                this.setCurrentNumber(1);
            }
        });
        game.getPlayers().add(new Player() {
            {
                this.id = "user2";
                this.setCurrentNumber(2);
            }
        });
        game.getPlayers().add(new Player() {
            {
                this.id = "user3";
                this.setCurrentNumber(3);
            }
        });
        game.getPlayers().add(new Player() {
            {
                this.id = "user4";
                this.setCurrentNumber(4);
            }
        });
        game.getPlayerNumberMap().put("1", new Player() {
            {
                this.id = "user1";
            }
        });
        game.getPlayerNumberMap().put("2", new Player() {
            {
                this.id = "user2";
            }
        });
        game.getPlayerNumberMap().put("3", new Player() {
            {
                this.id = "user3";
            }
        });
        game.getPlayerNumberMap().put("4", new Player() {
            {
                this.id = "user4";
            }
        });
    }

    protected void tearDown() throws Exception {
        HibernateSessionFactory.getSession().getTransaction().commit();
        HibernateSessionFactory.closeSession();
        super.tearDown();
    }
}

