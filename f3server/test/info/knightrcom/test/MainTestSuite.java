/**
 * 
 */
package info.knightrcom.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 初始化环境参数
 */
@SuppressWarnings("unchecked")
public class MainTestSuite extends TestSuite {

	public MainTestSuite(Class[] classes) {
		super(classes);
	}

	public static Test suite() {
		Class[] testClasses = { PlayerProfileTestCase.class,
				PersistScoreTestCase.class, 
				PersistDisconnectScoreTestCase.class,
				ModelDescriptorManipulateTest.class };
		MainTestSuite suite = new MainTestSuite(testClasses);
		return suite;
	}
}
