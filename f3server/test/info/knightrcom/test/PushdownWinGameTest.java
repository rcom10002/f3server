package info.knightrcom.test;

import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.model.game.pushdownwin.PushdownWinGame;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class PushdownWinGameTest extends TestCase{
	private PushdownWinGame pushdownWinGame = null;
	private Method privateStringMethod = null;
	
	protected void setUp() throws Exception {
		pushdownWinGame = new PushdownWinGame();
		privateStringMethod = PushdownWinGame.class.getDeclaredMethod("getPoints", GameRecord.class);
		privateStringMethod.setAccessible(true);
        super.setUp();
    }
	
	public void testGetPoints十三幺() throws Exception{
		String record = "T4,B5,W9,W2,W4,B3,T2,B8,SOUTH,B9,W5,W2,T5~W9,WHITE,SOUTH,W7,EAST,W5,T6,W8,T2,B2,GREEN,T9,B5~W1,T1,NORTH,T3,W9,RED,W6,W3,W7,EAST,T4,B8,SOUTH~GREEN,B2,B1,T1,T8,W8,T3,RED,B4,W4,T1,T7,B7~W2,B9,EAST,B2,B5,T5,W6,B7,B6,T3,T4,B1,T3,T7,T4,W8,WEST,B8,GREEN,WEST,T9,B9,T9,B3,B4,B3,GREEN,T2,B3,T2,NORTH,SOUTH,T8,B1,W7,W2,T6,W1,T6,RED,B7,W3,NORTH,W1,W4,B1,T1,T7,T5,EAST,WHITE,T8,W1,WEST,B6,W4,W6,W3,B6,W5,B4,B2,T8,B4,W3,W8,B5,B9,B7,T5,T9,W9,W6,NORTH,WHITE,W7,T7,B8,WHITE,RED,B6,T6,WEST,W5;1~W2;1~SOUTH~2;2~B9;2~WHITE~3;3~EAST;3~RED~4;4~B2;4~GREEN~1;1~B5;1~W9~2;2~T5;2~GREEN~3;3~W6;3~NORTH~4;4~B7;4~RED~1;1~B6;1~T2~2;2~T3;2~T9~3;3~T4;3~SOUTH~4;4~B1;4~W8~1;1~T3;1~B9~2;2~T7;2~SOUTH~3;3~T4;3~B8~4;4~W8;4~W8~1;1~WEST;1~WEST~2;2~B8;2~EAST~3;3~EAST,EAST,EAST~4~2~EAST~2;3~W9~4;4~GREEN;4~GREEN~1;1~WEST;1~WEST~2;2~T9;2~W5~3;3~W5,W6,W7~4~2~W5~3;3~W6~4;4~B9;4~W4~1;1~T9;1~T9~2;2~B3;2~B5~3;1~B5,B5,B5~2~2~B5~2;1~B3~2;2~B4;2~T9~3;3~B3;3~B3~4;4~GREEN;4~GREEN~1;1~T2;1~T2~2;2~B3;2~B3~3;3~T2;3~W3~4;4~NORTH;4~NORTH~1;1~SOUTH;1~SOUTH~2;2~T8;2~T8~3;3~B1;3~B1~4;4~B1,B1,B1~1~3~B1~2;4~B4~1;1~W7;1~W7~2;2~W2;2~W2~3;1~W2,W2,W2,null~2~2~W2~1;1~T6;1~T6~2;2~W1;2~W1~3;3~W1~2#EAST~SOUTH~WEST~NORTH~RED~GREEN~WHITE~W1~W9~B1~B9~T1~T9,T9;";
		GameRecord gameRecord = new GameRecord();
		gameRecord.setRecord(record);
		Object returnObj = (Object)privateStringMethod.invoke(pushdownWinGame, gameRecord);
		int result = Integer.valueOf(returnObj.toString());
		assertEquals(result, 100);	
	}

	public void testGetPoints碰碰和() throws Exception{
		String record = "T4,B5,W9,W2,W4,B3,T2,B8,SOUTH,B9,W5,W2,T5~W9,WHITE,SOUTH,W7,EAST,W5,T6,W8,T2,B2,GREEN,T9,B5~W1,T1,NORTH,T3,W9,RED,W6,W3,W7,EAST,T4,B8,SOUTH~GREEN,B2,B1,T1,T8,W8,T3,RED,B4,W4,T1,T7,B7~W2,B9,EAST,B2,B5,T5,W6,B7,B6,T3,T4,B1,T3,T7,T4,W8,WEST,B8,GREEN,WEST,T9,B9,T9,B3,B4,B3,GREEN,T2,B3,T2,NORTH,SOUTH,T8,B1,W7,W2,T6,W1,T6,RED,B7,W3,NORTH,W1,W4,B1,T1,T7,T5,EAST,WHITE,T8,W1,WEST,B6,W4,W6,W3,B6,W5,B4,B2,T8,B4,W3,W8,B5,B9,B7,T5,T9,W9,W6,NORTH,WHITE,W7,T7,B8,WHITE,RED,B6,T6,WEST,W5;1~W2;1~SOUTH~2;2~B9;2~WHITE~3;3~EAST;3~RED~4;4~B2;4~GREEN~1;1~B5;1~W9~2;2~T5;2~GREEN~3;3~W6;3~NORTH~4;4~B7;4~RED~1;1~B6;1~T2~2;2~T3;2~T9~3;3~T4;3~SOUTH~4;4~B1;4~W8~1;1~T3;1~B9~2;2~T7;2~SOUTH~3;3~T4;3~B8~4;4~W8;4~W8~1;1~WEST;1~WEST~2;2~B8;2~EAST~3;3~EAST,EAST,EAST~4~2~EAST~2;3~W9~4;4~GREEN;4~GREEN~1;1~WEST;1~WEST~2;2~T9;2~W5~3;3~W5,W6,W7~4~2~W5~3;3~W6~4;4~B9;4~W4~1;1~T9;1~T9~2;2~B3;2~B5~3;1~B5,B5,B5~2~2~B5~2;1~B3~2;2~B4;2~T9~3;3~B3;3~B3~4;4~GREEN;4~GREEN~1;1~T2;1~T2~2;2~B3;2~B3~3;3~T2;3~W3~4;4~NORTH;4~NORTH~1;1~SOUTH;1~SOUTH~2;2~T8;2~T8~3;3~B1;3~B1~4;4~B1,B1,B1~1~3~B1~2;4~B4~1;1~W7;1~W7~2;2~W2;2~W2~3;1~W2,W2,W2,null~2~2~W2~1;1~T6;1~T6~2;2~W1;2~W1~3;3~W1~2#EAST,EAST,EAST~NORTH,NORTH,NORTH~W2,W2,W2~B1,B1,B1~T9,T9;";
		GameRecord gameRecord = new GameRecord();
		gameRecord.setRecord(record);
		Object returnObj = (Object)privateStringMethod.invoke(pushdownWinGame, gameRecord);
		int result = Integer.valueOf(returnObj.toString());
		assertEquals(result, 4);
	}
	
	public void testGetPoints清一色() throws Exception{
		String record = "T4,B5,W9,W2,W4,B3,T2,B8,SOUTH,B9,W5,W2,T5~W9,WHITE,SOUTH,W7,EAST,W5,T6,W8,T2,B2,GREEN,T9,B5~W1,T1,NORTH,T3,W9,RED,W6,W3,W7,EAST,T4,B8,SOUTH~GREEN,B2,B1,T1,T8,W8,T3,RED,B4,W4,T1,T7,B7~W2,B9,EAST,B2,B5,T5,W6,B7,B6,T3,T4,B1,T3,T7,T4,W8,WEST,B8,GREEN,WEST,T9,B9,T9,B3,B4,B3,GREEN,T2,B3,T2,NORTH,SOUTH,T8,B1,W7,W2,T6,W1,T6,RED,B7,W3,NORTH,W1,W4,B1,T1,T7,T5,EAST,WHITE,T8,W1,WEST,B6,W4,W6,W3,B6,W5,B4,B2,T8,B4,W3,W8,B5,B9,B7,T5,T9,W9,W6,NORTH,WHITE,W7,T7,B8,WHITE,RED,B6,T6,WEST,W5;1~W2;1~SOUTH~2;2~B9;2~WHITE~3;3~EAST;3~RED~4;4~B2;4~GREEN~1;1~B5;1~W9~2;2~T5;2~GREEN~3;3~W6;3~NORTH~4;4~B7;4~RED~1;1~B6;1~T2~2;2~T3;2~T9~3;3~T4;3~SOUTH~4;4~B1;4~W8~1;1~T3;1~B9~2;2~T7;2~SOUTH~3;3~T4;3~B8~4;4~W8;4~W8~1;1~WEST;1~WEST~2;2~B8;2~EAST~3;3~EAST,EAST,EAST~4~2~EAST~2;3~W9~4;4~GREEN;4~GREEN~1;1~WEST;1~WEST~2;2~T9;2~W5~3;3~W5,W6,W7~4~2~W5~3;3~W6~4;4~B9;4~W4~1;1~T9;1~T9~2;2~B3;2~B5~3;1~B5,B5,B5~2~2~B5~2;1~B3~2;2~B4;2~T9~3;3~B3;3~B3~4;4~GREEN;4~GREEN~1;1~T2;1~T2~2;2~B3;2~B3~3;3~T2;3~W3~4;4~NORTH;4~NORTH~1;1~SOUTH;1~SOUTH~2;2~T8;2~T8~3;3~B1;3~B1~4;4~B1,B1,B1~1~3~B1~2;4~B4~1;1~W7;1~W7~2;2~W2;2~W2~3;1~W2,W2,W2,null~2~2~W2~1;1~T6;1~T6~2;2~W1;2~W1~3;3~W1~2#B1,B2,B3~B4,B5,B6~B5,B6,B7~B7,B8,B9~B8,B8;";
		GameRecord gameRecord = new GameRecord();
		gameRecord.setRecord(record);
		Object returnObj = (Object)privateStringMethod.invoke(pushdownWinGame, gameRecord);
		int result = Integer.valueOf(returnObj.toString());
		assertEquals(result, 4);
	}
	
	public void testGetPoints七对() throws Exception{
		String record = "T4,B5,W9,W2,W4,B3,T2,B8,SOUTH,B9,W5,W2,T5~W9,WHITE,SOUTH,W7,EAST,W5,T6,W8,T2,B2,GREEN,T9,B5~W1,T1,NORTH,T3,W9,RED,W6,W3,W7,EAST,T4,B8,SOUTH~GREEN,B2,B1,T1,T8,W8,T3,RED,B4,W4,T1,T7,B7~W2,B9,EAST,B2,B5,T5,W6,B7,B6,T3,T4,B1,T3,T7,T4,W8,WEST,B8,GREEN,WEST,T9,B9,T9,B3,B4,B3,GREEN,T2,B3,T2,NORTH,SOUTH,T8,B1,W7,W2,T6,W1,T6,RED,B7,W3,NORTH,W1,W4,B1,T1,T7,T5,EAST,WHITE,T8,W1,WEST,B6,W4,W6,W3,B6,W5,B4,B2,T8,B4,W3,W8,B5,B9,B7,T5,T9,W9,W6,NORTH,WHITE,W7,T7,B8,WHITE,RED,B6,T6,WEST,W5;1~W2;1~SOUTH~2;2~B9;2~WHITE~3;3~EAST;3~RED~4;4~B2;4~GREEN~1;1~B5;1~W9~2;2~T5;2~GREEN~3;3~W6;3~NORTH~4;4~B7;4~RED~1;1~B6;1~T2~2;2~T3;2~T9~3;3~T4;3~SOUTH~4;4~B1;4~W8~1;1~T3;1~B9~2;2~T7;2~SOUTH~3;3~T4;3~B8~4;4~W8;4~W8~1;1~WEST;1~WEST~2;2~B8;2~EAST~3;3~EAST,EAST,EAST~4~2~EAST~2;3~W9~4;4~GREEN;4~GREEN~1;1~WEST;1~WEST~2;2~T9;2~W5~3;3~W5,W6,W7~4~2~W5~3;3~W6~4;4~B9;4~W4~1;1~T9;1~T9~2;2~B3;2~B5~3;1~B5,B5,B5~2~2~B5~2;1~B3~2;2~B4;2~T9~3;3~B3;3~B3~4;4~GREEN;4~GREEN~1;1~T2;1~T2~2;2~B3;2~B3~3;3~T2;3~W3~4;4~NORTH;4~NORTH~1;1~SOUTH;1~SOUTH~2;2~T8;2~T8~3;3~B1;3~B1~4;4~B1,B1,B1~1~3~B1~2;4~B4~1;1~W7;1~W7~2;2~W2;2~W2~3;1~W2,W2,W2,null~2~2~W2~1;1~T6;1~T6~2;2~W1;2~W1~3;3~W1~2#B1,B1~B9,B9~B2,B2~B3,B3~B4,B4~B7,B7~B8,B8;";
		GameRecord gameRecord = new GameRecord();
		gameRecord.setRecord(record);
		Object returnObj = (Object)privateStringMethod.invoke(pushdownWinGame, gameRecord);
		int result = Integer.valueOf(returnObj.toString());
		assertEquals(result, 32);
	}
	
	public void testGetPoints字一色() throws Exception{
		String record = "T4,B5,W9,W2,W4,B3,T2,B8,SOUTH,B9,W5,W2,T5~W9,WHITE,SOUTH,W7,EAST,W5,T6,W8,T2,B2,GREEN,T9,B5~W1,T1,NORTH,T3,W9,RED,W6,W3,W7,EAST,T4,B8,SOUTH~GREEN,B2,B1,T1,T8,W8,T3,RED,B4,W4,T1,T7,B7~W2,B9,EAST,B2,B5,T5,W6,B7,B6,T3,T4,B1,T3,T7,T4,W8,WEST,B8,GREEN,WEST,T9,B9,T9,B3,B4,B3,GREEN,T2,B3,T2,NORTH,SOUTH,T8,B1,W7,W2,T6,W1,T6,RED,B7,W3,NORTH,W1,W4,B1,T1,T7,T5,EAST,WHITE,T8,W1,WEST,B6,W4,W6,W3,B6,W5,B4,B2,T8,B4,W3,W8,B5,B9,B7,T5,T9,W9,W6,NORTH,WHITE,W7,T7,B8,WHITE,RED,B6,T6,WEST,W5;1~W2;1~SOUTH~2;2~B9;2~WHITE~3;3~EAST;3~RED~4;4~B2;4~GREEN~1;1~B5;1~W9~2;2~T5;2~GREEN~3;3~W6;3~NORTH~4;4~B7;4~RED~1;1~B6;1~T2~2;2~T3;2~T9~3;3~T4;3~SOUTH~4;4~B1;4~W8~1;1~T3;1~B9~2;2~T7;2~SOUTH~3;3~T4;3~B8~4;4~W8;4~W8~1;1~WEST;1~WEST~2;2~B8;2~EAST~3;3~EAST,EAST,EAST~4~2~EAST~2;3~W9~4;4~GREEN;4~GREEN~1;1~WEST;1~WEST~2;2~T9;2~W5~3;3~W5,W6,W7~4~2~W5~3;3~W6~4;4~B9;4~W4~1;1~T9;1~T9~2;2~B3;2~B5~3;1~B5,B5,B5~2~2~B5~2;1~B3~2;2~B4;2~T9~3;3~B3;3~B3~4;4~GREEN;4~GREEN~1;1~T2;1~T2~2;2~B3;2~B3~3;3~T2;3~W3~4;4~NORTH;4~NORTH~1;1~SOUTH;1~SOUTH~2;2~T8;2~T8~3;3~B1;3~B1~4;4~B1,B1,B1~1~3~B1~2;4~B4~1;1~W7;1~W7~2;2~W2;2~W2~3;1~W2,W2,W2,null~2~2~W2~1;1~T6;1~T6~2;2~W1;2~W1~3;3~W1~2#EAST,EAST,EAST~NORTH,NORTH,NORTH~WEST,WEST,WEST~RED,RED,RED~GREEN,GREEN;";
		GameRecord gameRecord = new GameRecord();
		gameRecord.setRecord(record);
		Object returnObj = (Object)privateStringMethod.invoke(pushdownWinGame, gameRecord);
		int result = Integer.valueOf(returnObj.toString());
		assertEquals(result, 8);
	}

}
