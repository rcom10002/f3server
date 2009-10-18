package info.knightrcom.test;

import info.knightrcom.model.game.fightlandlord.FightLandlordGame;
import info.knightrcom.model.game.fightlandlord.FightLandlordGameSetting;

/**
 * 斗地主积分计算测试
 */
public class PersistFightLandlordGameScoreTestCase extends PersistScoreTestCase {

    /**
     * 斗地主
     * 
     * gameRecord = 4V6,4V10,1V9,3V5,4V8,3V9,3V7,1V3,4V5,3VK,3V6,2V7,2VJ,1V7,0VY,2V3,2V10~0=17,1=17,2=17;4V3,1V5,2V8,3V10,2V4,1VQ,1VA,3VA,2VK,4V9,3V2,4V7,4VA,4V2,2VQ,3V4,4VK~0=17,1=17,2=17;1V4,1V2,2V9,3V3,3V8,1V10,1VJ,4VQ,2V2,4VJ,2VA,2V6,0VX,1V8,1V6,3VJ,2V5~0=17,1=17,2=17;1~3~2;4V4,1VK,3VQ~1;1~4V4,1V7,2V7,3V7~2;2~4V3,1VA,3VA,4VA~3;2~4V3,1VA,3VA,4VA~1~pass;2~4V3,1VA,3VA,4VA~2~pass;2~2V4,3V4~3;3~1V6,2V6~1;1~1V9,3V9~2;1~1V9,3V9~3~pass;1~1V9,3V9~1~pass;1~1V3,2V3~2;2~1VQ,2VQ~3;3~1V2,2V2~1;3~1V2,2V2~2~pass;3~1V2,2V2~3~pass;3~3V3,1VJ,3VJ,4VJ~1;3~3V3,1VJ,3VJ,4VJ~2~pass;3~3V3,1VJ,3VJ,4VJ~3~pass;3~1V4~1;3~1V4~2~pass;2~4V7~3;2~4V7~1~pass;1~4V8~2;1~4V8~3~pass;1~4V8~1~pass;1~3V5,4V5~2;1~3V5,4V5~3~pass;1~3V5,4V5~1~pass;1~3V6,4V6~2;1~3V6,4V6~3~pass;1~3V6,4V6~1~pass;1~2V10,4V10~2;1~2V10,4V10~3~pass;1~2V10,4V10~1~pass;1~2VJ~2;1~2VJ~3~pass;3~0VX~1;1~0VY~2;1~0VY~3~pass;1~0VY~1~pass;1~1VK,3VK~2;1~1VK,3VK~3~pass;1~1VK,3VK~1~pass;1~3VQ~2~double;1~3VQ~2;
     * 
     * winnerNumbers = 1
     */
    public void testFightLandlordScoreSuccess() throws Exception {
        FightLandlordGame game = new FightLandlordGame();
        game.setSetting(FightLandlordGameSetting.THREE_RUSH);
        game.setHighLevelMark(50);
        game.getSetting().setPlayerNumber("1");
        game.setGameMark(5);
        game.setMinGameStartMark(100);
        game.addWinnerNumber("1");
        game.appendGameRecord("4V6,4V10,1V9,3V5,4V8,3V9,3V7,1V3,4V5,3VK,3V6,2V7,2VJ,1V7,0VY,2V3,2V10~0=17,1=17,2=17;4V3,1V5,2V8,3V10,2V4,1VQ,1VA,3VA,2VK,4V9,3V2,4V7,4VA,4V2,2VQ,3V4,4VK~0=17,1=17,2=17;1V4,1V2,2V9,3V3,3V8,1V10,1VJ,4VQ,2V2,4VJ,2VA,2V6,0VX,1V8,1V6,3VJ,2V5~0=17,1=17,2=17;1~3~2;4V4,1VK,3VQ~1;1~4V4,1V7,2V7,3V7~2;2~4V3,1VA,3VA,4VA~3;2~4V3,1VA,3VA,4VA~1~pass;2~4V3,1VA,3VA,4VA~2~pass;2~2V4,3V4~3;3~1V6,2V6~1;1~1V9,3V9~2;1~1V9,3V9~3~pass;1~1V9,3V9~1~pass;1~1V3,2V3~2;2~1VQ,2VQ~3;3~1V2,2V2~1;3~1V2,2V2~2~pass;3~1V2,2V2~3~pass;3~3V3,1VJ,3VJ,4VJ~1;3~3V3,1VJ,3VJ,4VJ~2~pass;3~3V3,1VJ,3VJ,4VJ~3~pass;3~1V4~1;3~1V4~2~pass;2~4V7~3;2~4V7~1~pass;1~4V8~2;1~4V8~3~pass;1~4V8~1~pass;1~3V5,4V5~2;1~3V5,4V5~3~pass;1~3V5,4V5~1~pass;1~3V6,4V6~2;1~3V6,4V6~3~pass;1~3V6,4V6~1~pass;1~2V10,4V10~2;1~2V10,4V10~3~pass;1~2V10,4V10~1~pass;1~2VJ~2;1~2VJ~3~pass;3~0VX~1;1~0VY~2;1~0VY~3~pass;1~0VY~1~pass;1~1VK,3VK~2;1~1VK,3VK~3~pass;1~1VK,3VK~1~pass;1~3VQ~2~double;1~3VQ~2;");
        addGamePlayer(game, 3);
        game.persistScore();
        Thread.sleep(3000);
    }

    /**
     * 斗地主
     * 
     * gameRecord = 4V6,4V10,1V9,3V5,4V8,3V9,3V7,1V3,4V5,3VK,3V6,2V7,2VJ,1V7,0VY,2V3,2V10~0=17,1=17,2=17;4V3,1V5,2V8,3V10,2V4,1VQ,1VA,3VA,2VK,4V9,3V2,4V7,4VA,4V2,2VQ,3V4,4VK~0=17,1=17,2=17;1V4,1V2,2V9,3V3,3V8,1V10,1VJ,4VQ,2V2,4VJ,2VA,2V6,0VX,1V8,1V6,3VJ,2V5~0=17,1=17,2=17;1~3~2;4V4,1VK,3VQ~1;1~4V4,1V7,2V7,3V7~2;2~4V3,1VA,3VA,4VA~3;2~4V3,1VA,3VA,4VA~1~pass;2~4V3,1VA,3VA,4VA~2~pass;2~2V4,3V4~3;3~1V6,2V6~1;1~1V9,3V9~2;1~1V9,3V9~3~pass;1~1V9,3V9~1~pass;1~1V3,2V3~2;2~1VQ,2VQ~3;3~1V2,2V2~1;3~1V2,2V2~2~pass;3~1V2,2V2~3~pass;3~3V3,1VJ,3VJ,4VJ~1;3~3V3,1VJ,3VJ,4VJ~2~pass;3~3V3,1VJ,3VJ,4VJ~3~pass;3~1V4~1;3~1V4~2~pass;2~4V7~3;2~4V7~1~pass;1~4V8~2;1~4V8~3~pass;1~4V8~1~pass;1~3V5,4V5~2;1~3V5,4V5~3~pass;1~3V5,4V5~1~pass;1~3V6,4V6~2;1~3V6,4V6~3~pass;1~3V6,4V6~1~pass;1~2V10,4V10~2;1~2V10,4V10~3~pass;1~2V10,4V10~1~pass;1~2VJ~2;1~2VJ~3~pass;3~0VX~1;1~0VY~2;1~0VY~3~pass;1~0VY~1~pass;1~1VK,3VK~2;1~1VK,3VK~3~pass;1~1VK,3VK~1~pass;1~3VQ~2~double;1~3VQ~2;
     * 
     * winnerNumbers = 1
     */
    public void testFightLandlordScoreFail() throws Exception {
        FightLandlordGame game = new FightLandlordGame();
        game.setSetting(FightLandlordGameSetting.TWO_RUSH);
        game.setMidLevelMark(30);
        game.getSetting().setPlayerNumber("1");
        game.setGameMark(5);
        game.setMinGameStartMark(100);
        game.addWinnerNumber("2");
        game.appendGameRecord("4V6,4V10,1V9,3V5,4V8,3V9,3V7,1V3,4V5,3VK,3V6,2V7,2VJ,1V7,0VY,2V3,2V10~0=17,1=17,2=17;4V3,1V5,2V8,3V10,2V4,1VQ,1VA,3VA,2VK,4V9,3V2,4V7,4VA,4V2,2VQ,3V4,4VK~0=17,1=17,2=17;1V4,1V2,2V9,3V3,3V8,1V10,1VJ,4VQ,2V2,4VJ,2VA,2V6,0VX,1V8,1V6,3VJ,2V5~0=17,1=17,2=17;1~3~2;4V4,1VK,3VQ~1;1~4V4,1V7,2V7,3V7~2;2~4V3,1VA,3VA,4VA~3;2~4V3,1VA,3VA,4VA~1~pass;2~4V3,1VA,3VA,4VA~2~pass;2~2V4,3V4~3;3~1V6,2V6~1;1~1V9,3V9~2;1~1V9,3V9~3~pass;1~1V9,3V9~1~pass;1~1V3,2V3~2;2~1VQ,2VQ~3;3~1V2,2V2~1;3~1V2,2V2~2~pass;3~1V2,2V2~3~pass;3~3V3,1VJ,3VJ,4VJ~1;3~3V3,1VJ,3VJ,4VJ~2~pass;3~3V3,1VJ,3VJ,4VJ~3~pass;3~1V4~1;3~1V4~2~pass;2~4V7~3;2~4V7~1~pass;1~4V8~2;1~4V8~3~pass;1~4V8~1~pass;1~3V5,4V5~2;1~3V5,4V5~3~pass;1~3V5,4V5~1~pass;1~3V6,4V6~2;1~3V6,4V6~3~pass;1~3V6,4V6~1~pass;1~2V10,4V10~2;1~2V10,4V10~3~pass;1~2V10,4V10~1~pass;1~2VJ~2;1~2VJ~3~pass;3~0VX~1;1~0VY~2;1~0VY~3~pass;1~0VY~1~pass;1~1VK,3VK~2;1~1VK,3VK~3~pass;1~1VK,3VK~1~pass;1~3VQ~2~double;1~3VQ~2;");
        addGamePlayer(game, 3);
        game.persistScore();
        Thread.sleep(3000);
    }

}
