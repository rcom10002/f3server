package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.model.game.red5.Red5Game;
import info.knightrcom.model.game.red5.Red5GameSetting;
import info.knightrcom.model.global.Player;
import junit.framework.TestCase;

public class PersistScoreTestCase extends TestCase {

    protected void setUp() throws Exception {
        HibernateSessionFactory.init();
        HibernateSessionFactory.getSession().beginTransaction();
        super.setUp();
    }

    /**
     * gameRecord = 2~0~3;2~0~4;2~0~1;2~0~2;2~1V10,2V10,3V10,3V10,4V10~3;2~1V10,2V10,3V10,3V10,4V10~4~pass;2~1V10,2V10,3V10,3V10,4V10~1~pass;2~1V10,2V10,3V10,3V10,4V10~2~pass;2~1VK,2VK,3VK,4VK,4VK~3;2~1VK,2VK,3VK,4VK,4VK~4~pass;2~1VK,2VK,3VK,4VK,4VK~1~pass;2~1VK,2VK,3VK,4VK,4VK~2~pass;2~1VA~3;2~1VA~4~pass;4~0VX~1;1~0VY~2;1~0VY~3~pass;1~0VY~4~pass;1~0VY~1~pass;1~2VJ,3VJ,3VJ~2;1~2VJ,3VJ,3VJ~3~pass;3~3V5,3V5,4V5~4;3~3V5,3V5,4V5~1~pass;3~3V5,3V5,4V5~2~pass;3~3V5,3V5,4V5~3~pass;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~4;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~1~pass;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~2~pass;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~3~pass;3~2VA,2VA,4VA~4;4~1V2,1V2,4V2~1;4~1V2,1V2,4V2~2~pass;4~1V2,1V2,4V2~3~pass;4~1V2,1V2,4V2~4~pass;4~1VJ,1VQ,2VK,3VA~1;4~1VJ,1VQ,2VK,3VA~2~pass;4~1VJ,1VQ,2VK,3VA~3~pass;4~1VJ,1VQ,2VK,3VA~4~pass;4~2VJ,4VJ~1;1~2VQ,3VQ~2;2~2V2,4V2~3;2~2V2,4V2~4~pass;4~1V5,1V5~1;4~1V5,1V5~2~pass;4~1V5,1V5~3~pass;4~1V5,1V5~4~pass;4~2VQ,3VQ,4VQ~1;1~1VA,3VA,4VA~2;1~1VA,3VA,4VA~3~pass;1~1VA,3VA,4VA~1~pass;1~4V10~2;2~0VY~3;2~0VY~1~pass;2~0VY~2~pass;2~2V5~3;3~2V5~1~pass;1~0VX~3;1~0VX~1~pass;1~2V5,4V5~3;1~2V5,4V5~1~pass;1~3V2~3;1~3V2~1~pass;1~1VK~3;
     * 
     * winnerNumbers = 4~2~1~3
     */
    public void testNoRushScore() throws Exception {
        Red5Game game = new Red5Game();
        game.setSetting(Red5GameSetting.NO_RUSH);
        game.getSetting().setPlayerNumber("1");
        game.setGameMark(5);
        game.setMinGameStartMark(100);
        game.addWinnerNumber("4~2~1~3");
        game.appendGameRecord("2~0~3;2~0~4;2~0~1;2~0~2;2~1V10,2V10,3V10,3V10,4V10~3;2~1V10,2V10,3V10,3V10,4V10~4~pass;2~1V10,2V10,3V10,3V10,4V10~1~pass;2~1V10,2V10,3V10,3V10,4V10~2~pass;2~1VK,2VK,3VK,4VK,4VK~3;2~1VK,2VK,3VK,4VK,4VK~4~pass;2~1VK,2VK,3VK,4VK,4VK~1~pass;2~1VK,2VK,3VK,4VK,4VK~2~pass;2~1VA~3;2~1VA~4~pass;4~0VX~1;1~0VY~2;1~0VY~3~pass;1~0VY~4~pass;1~0VY~1~pass;1~2VJ,3VJ,3VJ~2;1~2VJ,3VJ,3VJ~3~pass;3~3V5,3V5,4V5~4;3~3V5,3V5,4V5~1~pass;3~3V5,3V5,4V5~2~pass;3~3V5,3V5,4V5~3~pass;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~4;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~1~pass;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~2~pass;3~1V10,2V10,1VJ,4VJ,1VQ,4VQ~3~pass;3~2VA,2VA,4VA~4;4~1V2,1V2,4V2~1;4~1V2,1V2,4V2~2~pass;4~1V2,1V2,4V2~3~pass;4~1V2,1V2,4V2~4~pass;4~1VJ,1VQ,2VK,3VA~1;4~1VJ,1VQ,2VK,3VA~2~pass;4~1VJ,1VQ,2VK,3VA~3~pass;4~1VJ,1VQ,2VK,3VA~4~pass;4~2VJ,4VJ~1;1~2VQ,3VQ~2;2~2V2,4V2~3;2~2V2,4V2~4~pass;4~1V5,1V5~1;4~1V5,1V5~2~pass;4~1V5,1V5~3~pass;4~1V5,1V5~4~pass;4~2VQ,3VQ,4VQ~1;1~1VA,3VA,4VA~2;1~1VA,3VA,4VA~3~pass;1~1VA,3VA,4VA~1~pass;1~4V10~2;2~0VY~3;2~0VY~1~pass;2~0VY~2~pass;2~2V5~3;3~2V5~1~pass;1~0VX~3;1~0VX~1~pass;1~2V5,4V5~3;1~2V5,4V5~1~pass;1~3V2~3;1~3V2~1~pass;1~1VK~3");
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
        
        game.persistScore();
        Thread.sleep(3000);
    }

    /**
     * gameRecord = 2~1~3;2~1~4;2~1~1;2~1~2;2~1V10,4V10~3;3~1VQ,2VQ~4;4~2VK,3VK~1;1~1VA,3VA~2;2~2V5,3V5~3;2~2V5,3V5~4~pass;2~2V5,3V5~1~pass;2~2V5,3V5~2~pass;2~4VJ~3;3~4VA~4;4~4V5~1;4~4V5~2~pass;2~0VX~3;2~0VX~4~pass;2~0VX~1~pass;2~0VX~2~pass;2~2VQ,3VQ,4VQ~3;2~2VQ,3VQ,4VQ~4~pass;4~1VA,2VA,2VA~1;1~3V2,4V2,4V2~2;1~3V2,4V2,4V2~3~pass;1~3V2,4V2,4V2~4~pass;1~3V2,4V2,4V2~1~pass;1~3V10,1VJ,3VQ,4VK,3VA~2;1~3V10,1VJ,3VQ,4VK,3VA~3~pass;1~3V10,1VJ,3VQ,4VK,3VA~4~pass;1~3V10,1VJ,3VQ,4VK,3VA~1~pass;1~3VJ,4VQ,4VK,4VA~2;1~3VJ,4VQ,4VK,4VA~3~pass;1~3VJ,4VQ,4VK,4VA~4~pass;1~3VJ,4VQ,4VK,4VA~1~pass;1~4V5~2;
     * 
     * winnerNumbers = 4~2~1~3
     */
    public void testRushScore() throws Exception {
        Red5Game game = new Red5Game();
        game.setSetting(Red5GameSetting.RUSH);
        game.getSetting().setPlayerNumber("2");
        game.setGameMark(5);
        game.setMinGameStartMark(100);
        game.addWinnerNumber("1");
        game.appendGameRecord("2~1~3;2~1~4;2~1~1;2~1~2;2~1V10,4V10~3;3~1VQ,2VQ~4;4~2VK,3VK~1;1~1VA,3VA~2;2~2V5,3V5~3;2~2V5,3V5~4~pass;2~2V5,3V5~1~pass;2~2V5,3V5~2~pass;2~4VJ~3;3~4VA~4;4~4V5~1;4~4V5~2~pass;2~0VX~3;2~0VX~4~pass;2~0VX~1~pass;2~0VX~2~pass;2~2VQ,3VQ,4VQ~3;2~2VQ,3VQ,4VQ~4~pass;4~1VA,2VA,2VA~1;1~3V2,4V2,4V2~2;1~3V2,4V2,4V2~3~pass;1~3V2,4V2,4V2~4~pass;1~3V2,4V2,4V2~1~pass;1~3V10,1VJ,3VQ,4VK,3VA~2;1~3V10,1VJ,3VQ,4VK,3VA~3~pass;1~3V10,1VJ,3VQ,4VK,3VA~4~pass;1~3V10,1VJ,3VQ,4VK,3VA~1~pass;1~3VJ,4VQ,4VK,4VA~2;1~3VJ,4VQ,4VK,4VA~3~pass;1~3VJ,4VQ,4VK,4VA~4~pass;1~3VJ,4VQ,4VK,4VA~1~pass;1~4V5~2;");
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

        game.persistScore();

        Thread.sleep(3000);
    }

    protected void tearDown() throws Exception {
        HibernateSessionFactory.getSession().getTransaction().commit();
        HibernateSessionFactory.closeSession();
        super.tearDown();
    }
}
