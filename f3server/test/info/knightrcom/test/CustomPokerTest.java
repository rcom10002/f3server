package info.knightrcom.test;

import info.knightrcom.web.service.CustomPokerService;
import junit.framework.TestCase;

public class CustomPokerTest extends TestCase {
//	public void testGetRed5Pokers() {
//		List<Red5Poker> pokers = new CustomPokerService().getRed5Pokers();
//		for (Red5Poker poker : pokers) {
//			System.out.println(poker.getValueStyle());
//		}
//	}
	
//	public void testGenRandomCountByRate() {
//		String[] num = {"10", "10", "10", "10", "10", "10", "10", "10", "10", "10"};
//		Map<String, Integer> map = new CustomPokerService().genRandomPokerCountByRate(num);
//		int total = 0;
//		for (String key : map.keySet()) {
//			int count = map.get(key);
//			int color = 0;
//			total += count;
//			PokerColor[] colors = PokerColor.values();
//			while (count > 0) {
//				if (key.equals("VM")) {
//					color = PokerColor.HEART.ordinal();
//				} else if (key.equals("VX") || key.equals("VY")) {
//					color = PokerColor.NO_COLOR.ordinal();
//				} else if (key.equals("V5")) {
//					color = colors[count > 4 ? count-2 : count].ordinal();
//					color = color == 1 ? 2 : color;
//				} else {
//					color = colors[count > 4 ? count-4 : count].ordinal();
//				}
//				System.out.println(color + key + "~" + map.get(key));
//				count--;
//			}
//		}
//		System.out.println("total: " + total);
//	}
	
	public void testGenRandomPokersByRate() {
		String[] num = {"10", "10", "10", "10", "10", "10", "10", "10", "10", "10"};
		String str = new CustomPokerService().genRandomPokersByRate(num);
		System.out.println(str);
	}

}
