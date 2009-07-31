/**
 * 
 */
package info.knightrcom.test;

import info.knightrcom.util.SystemParameter;
import junit.framework.TestCase;

/**
 *
 */
public class SystemParameterTest extends TestCase {

	public void testSet() {
		SystemParameter params = new SystemParameter();
		params.set("1", "A");
		params.set("2", "B");
		params.set("3", "C");
		assertEquals(params.count(), 3);
	}

	public void testGet() {
		SystemParameter params = new SystemParameter();
		params.set("1", "A");
		params.set("2", "B");
		params.set("3", "C");
		assertEquals(params.get("1"), "A");
		assertEquals(params.get("2"), "B");
		assertEquals(params.get("3"), "C");
	}
}
