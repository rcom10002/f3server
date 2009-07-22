package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.game.pushdownwin.PushdownWinGame;
import info.knightrcom.model.game.pushdownwin.PushdownWinGameSetting;
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
     * 红五不独
     * 
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
        addGamePlayer(game);
        
        game.persistScore();
        Thread.sleep(3000);
    }

    /**
     * 红五独牌
     * 
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
        addGamePlayer(game);

        game.persistScore();

        Thread.sleep(3000);
    }

    /**
     * 推倒胡点炮
     * 
     * 2~1 获胜玩家~失败玩家
     *
     * W1,NORTH,T9,W9,T8,NORTH,T3,B9,WHITE,T1,W7,EAST,B7~WHITE,GREEN,W3,T2,RED,B2,RED,T3,T5,B1,W2,B8,B9~B6,W2,B5,B5,W7,T7,T6,T3,T9,W9,B2,W6,B5~T8,T7,RED,T3,WEST,W3,B6,B2,SOUTH,B3,EAST,B4,B4~W8,T5,B1,T4,T8,B1,WHITE,W3,SOUTH,WEST,W5,W2,B2,T1,B8,W9,NORTH,RED,W7,W1,B8,T6,B6,B5,B9,T4,W7,T5,T6,WEST,NORTH,T6,W1,T8,T2,SOUTH,B4,W4,B3,B8,T7,W9,W4,T9,T1,B9,EAST,T7,WEST,W8,B7,SOUTH,T4,B3,GREEN,W6,B3,W8,W6,B7,W8,W6,B6,W5,B1,T2,W3,B4,W5,GREEN,T2,W2,EAST,T9,B7,W4,WHITE,W4,GREEN,T4,T1,T5,W5,W1;1~W8;1~EAST~2;2~T5;2~GREEN~3;3~B1;3~T3~4;4~T4;4~RED~1;1~T8;1~WHITE~2;2~B1;2~WHITE~3;3~WHITE;3~WHITE~4;4~W3;4~EAST~1;1~SOUTH;1~SOUTH~2;2~WEST;2~WEST~3;3~W5;3~W2~4;4~W2;4~SOUTH~1;1~B2;1~W1~2;2~T1;2~B2~3;3~B8;3~W9~4;4~W9;4~WEST~1;1~NORTH;1~B2~2;2~RED;2~B9~3;3~W7;3~B1~4;4~W1;4~W9~1;1~B8;1~T1~2;2~T6;2~B8~3;3~B6;3~B5~4;4~B5;4~W3~1;1~B9;1~B9~2;2~T4;2~T5~3;3~W7;3~B2~4;4~T5;4~T7~1;1~T6;1~T3~2;2~WEST;2~WEST~3;3~NORTH;3~NORTH~4;4~T6;4~T3~1;1~W1;1~W1~2;2~W1~1;
     */
    public void testPushdownWin_NarrowVictory() {
    	// 48.2     39.4 13940996048
        PushdownWinGame game = new PushdownWinGame();
        game.setSetting(PushdownWinGameSetting.NARROW_VICTORY);
        game.setGameMark(10);
        game.setMinGameStartMark(100);
        game.addWinnerNumber("2~1");
        game.appendGameRecord("W1,NORTH,T9,W9,T8,NORTH,T3,B9,WHITE,T1,W7,EAST,B7~WHITE,GREEN,W3,T2,RED,B2,RED,T3,T5,B1,W2,B8,B9~B6,W2,B5,B5,W7,T7,T6,T3,T9,W9,B2,W6,B5~T8,T7,RED,T3,WEST,W3,B6,B2,SOUTH,B3,EAST,B4,B4~W8,T5,B1,T4,T8,B1,WHITE,W3,SOUTH,WEST,W5,W2,B2,T1,B8,W9,NORTH,RED,W7,W1,B8,T6,B6,B5,B9,T4,W7,T5,T6,WEST,NORTH,T6,W1,T8,T2,SOUTH,B4,W4,B3,B8,T7,W9,W4,T9,T1,B9,EAST,T7,WEST,W8,B7,SOUTH,T4,B3,GREEN,W6,B3,W8,W6,B7,W8,W6,B6,W5,B1,T2,W3,B4,W5,GREEN,T2,W2,EAST,T9,B7,W4,WHITE,W4,GREEN,T4,T1,T5,W5,W1;1~W8;1~EAST~2;2~T5;2~GREEN~3;3~B1;3~T3~4;4~T4;4~RED~1;1~T8;1~WHITE~2;2~B1;2~WHITE~3;3~WHITE;3~WHITE~4;4~W3;4~EAST~1;1~SOUTH;1~SOUTH~2;2~WEST;2~WEST~3;3~W5;3~W2~4;4~W2;4~SOUTH~1;1~B2;1~W1~2;2~T1;2~B2~3;3~B8;3~W9~4;4~W9;4~WEST~1;1~NORTH;1~B2~2;2~RED;2~B9~3;3~W7;3~B1~4;4~W1;4~W9~1;1~B8;1~T1~2;2~T6;2~B8~3;3~B6;3~B5~4;4~B5;4~W3~1;1~B9;1~B9~2;2~T4;2~T5~3;3~W7;3~B2~4;4~T5;4~T7~1;1~T6;1~T3~2;2~WEST;2~WEST~3;3~NORTH;3~NORTH~4;4~T6;4~T3~1;1~W1;1~W1~2;2~W1~1;");
        addGamePlayer(game);
        game.persistScore();
    }

    /**
     * 推倒胡自摸
     * 
     * 2~1 获胜玩家~失败玩家
     *
     * W1,NORTH,T9,W9,T8,NORTH,T3,B9,WHITE,T1,W7,EAST,B7~WHITE,GREEN,W3,T2,RED,B2,RED,T3,T5,B1,W2,B8,B9~B6,W2,B5,B5,W7,T7,T6,T3,T9,W9,B2,W6,B5~T8,T7,RED,T3,WEST,W3,B6,B2,SOUTH,B3,EAST,B4,B4~W8,T5,B1,T4,T8,B1,WHITE,W3,SOUTH,WEST,W5,W2,B2,T1,B8,W9,NORTH,RED,W7,W1,B8,T6,B6,B5,B9,T4,W7,T5,T6,WEST,NORTH,T6,W1,T8,T2,SOUTH,B4,W4,B3,B8,T7,W9,W4,T9,T1,B9,EAST,T7,WEST,W8,B7,SOUTH,T4,B3,GREEN,W6,B3,W8,W6,B7,W8,W6,B6,W5,B1,T2,W3,B4,W5,GREEN,T2,W2,EAST,T9,B7,W4,WHITE,W4,GREEN,T4,T1,T5,W5,W1;1~W8;1~EAST~2;2~T5;2~GREEN~3;3~B1;3~T3~4;4~T4;4~RED~1;1~T8;1~WHITE~2;2~B1;2~WHITE~3;3~WHITE;3~WHITE~4;4~W3;4~EAST~1;1~SOUTH;1~SOUTH~2;2~WEST;2~WEST~3;3~W5;3~W2~4;4~W2;4~SOUTH~1;1~B2;1~W1~2;2~T1;2~B2~3;3~B8;3~W9~4;4~W9;4~WEST~1;1~NORTH;1~B2~2;2~RED;2~B9~3;3~W7;3~B1~4;4~W1;4~W9~1;1~B8;1~T1~2;2~T6;2~B8~3;3~B6;3~B5~4;4~B5;4~W3~1;1~B9;1~B9~2;2~T4;2~T5~3;3~W7;3~B2~4;4~T5;4~T7~1;1~T6;1~T3~2;2~WEST;2~WEST~3;3~NORTH;3~NORTH~4;4~T6;4~T3~1;1~W1;1~W1~2;2~W1~1;
     */
    public void testPushdownWin_ClearVictory() {
    	// 48.2     39.4 13940996048
        PushdownWinGame game = new PushdownWinGame();
        game.setSetting(PushdownWinGameSetting.CLEAR_VICTORY);
        game.setGameMark(10);
        game.setMinGameStartMark(100);
        game.addWinnerNumber("2");
        game.appendGameRecord("W1,NORTH,T9,W9,T8,NORTH,T3,B9,WHITE,T1,W7,EAST,B7~WHITE,GREEN,W3,T2,RED,B2,RED,T3,T5,B1,W2,B8,B9~B6,W2,B5,B5,W7,T7,T6,T3,T9,W9,B2,W6,B5~T8,T7,RED,T3,WEST,W3,B6,B2,SOUTH,B3,EAST,B4,B4~W8,T5,B1,T4,T8,B1,WHITE,W3,SOUTH,WEST,W5,W2,B2,T1,B8,W9,NORTH,RED,W7,W1,B8,T6,B6,B5,B9,T4,W7,T5,T6,WEST,NORTH,T6,W1,T8,T2,SOUTH,B4,W4,B3,B8,T7,W9,W4,T9,T1,B9,EAST,T7,WEST,W8,B7,SOUTH,T4,B3,GREEN,W6,B3,W8,W6,B7,W8,W6,B6,W5,B1,T2,W3,B4,W5,GREEN,T2,W2,EAST,T9,B7,W4,WHITE,W4,GREEN,T4,T1,T5,W5,W1;1~W8;1~EAST~2;2~T5;2~GREEN~3;3~B1;3~T3~4;4~T4;4~RED~1;1~T8;1~WHITE~2;2~B1;2~WHITE~3;3~WHITE;3~WHITE~4;4~W3;4~EAST~1;1~SOUTH;1~SOUTH~2;2~WEST;2~WEST~3;3~W5;3~W2~4;4~W2;4~SOUTH~1;1~B2;1~W1~2;2~T1;2~B2~3;3~B8;3~W9~4;4~W9;4~WEST~1;1~NORTH;1~B2~2;2~RED;2~B9~3;3~W7;3~B1~4;4~W1;4~W9~1;1~B8;1~T1~2;2~T6;2~B8~3;3~B6;3~B5~4;4~B5;4~W3~1;1~B9;1~B9~2;2~T4;2~T5~3;3~W7;3~B2~4;4~T5;4~T7~1;1~T6;1~T3~2;2~WEST;2~WEST~3;3~NORTH;3~NORTH~4;4~T6;4~T3~1;1~W1;1~W1~2;2~W1~1;");
        addGamePlayer(game);
        game.persistScore();
    }

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
