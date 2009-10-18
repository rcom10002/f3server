package info.knightrcom.test;

import junit.framework.Test;
import junit.framework.TestSuite;

@SuppressWarnings("unchecked")
public class ScoreTestSuite extends TestSuite {

	public static Test suite() {
		Class[] testClasses = { PlayerProfileUsersTestCase.class,
				PersistRed5GameScoreTestCase.class,
				PersistFightLandlordGameScoreTestCase.class,
				PersistPushdownWinGameScoreTestCase.class,
				PersistDisconnectScoreTestCase.class};
		MainTestSuite suite = new MainTestSuite(testClasses);
		return suite;
	}
}
