package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.model.plaything.PokerColor;
import info.knightrcom.model.plaything.PokerValue;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.EntityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;

public class CustomPokerService extends F3SWebService<GlobalConfig> {
	
	private final String prioritySequence = "V10,VJ,VQ,VK,VA,V2,V5,VX,VY,VM";
	private final String[] red5GameSettingStrs = {"NO_RUSH", "RUSH", "DEADLY_RUSH", "EXTINCT_RUSH"};
	private final int pokerCount = 15;

	@Override
	public Class<?>[] getAliasTypes() {
		return new Class<?>[] {GlobalConfig.class};
	}

	@Override
	public String getNamedQuery() {
		return null;
	}

	@Override
	public String getNamedQueryForCount() {
		return null;
	}

	@Override
	public ResultTransformer getResultTransformer() {
		return null;
	}

	@Override
	public void processQuerySetting(Query query, HttpServletRequest request) {
	}
	
	/**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String READ_POKER(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	String method = request.getParameter("METHOD");
    	if (method != null && method.length() > 0) {
    		Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle(false);
	        StringBuilder builder = new StringBuilder();
	    	for (int m = 0; m < eachShuffledPokers.length; m++) {
	            for (int n = 0; n < eachShuffledPokers[m].length; n++) {
	                builder.append(eachShuffledPokers[m][n].getValue() + ",");
	            }
	    	}
	    	info.setTag(builder.toString().replaceAll(",$", ""));
    	} else {
    		List<GlobalConfig> list = new GlobalConfigDAO().findByType("CUSTOM_POKER");
    		// 排序
    		Collections.sort(list, new Comparator<GlobalConfig>() {
				public int compare(GlobalConfig o1, GlobalConfig o2) {
					if (o1.getCreateTime().getTime() > o2.getCreateTime().getTime()) {
						return -1;
					} else if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
						return 1;
					}
					return 0;
				}
    		});
    		info.setTag(list.toArray());
    	}
    	return toXML(info, GlobalConfig.class);
	}
    
    /**
     * 保存牌型
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public String SAVE_CUSTOM_POKER(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String id = request.getParameter("ID");
    	String name = request.getParameter("NAME");
    	String strUpCards = request.getParameter("UP_POKERS");
    	String strDownCards = request.getParameter("DOWN_POKERS");
    	String isOpen = request.getParameter("IS_OPEN");
    	String type = request.getParameter("TYPE");
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	try {
    		GlobalConfig config = new GlobalConfigDAO().findById(id);
    		if (config == null) {
    			// 新增时判断自主牌型名称是否重复
    			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
    			criteria.add(Expression.eq("name", name));
    			criteria.add(Expression.eq("type", "CUSTOM_POKER"));
    			List<GlobalConfig> result = criteria.list();
    			if (result != null && result.size() > 0) {
    				info.setResult(F3SWebServiceResult.WARNING);
    				return toXML(info, getAliasTypes());
    			}
		    	config = new GlobalConfig();
	    		config.setGlobalConfigId(UUID.randomUUID().toString());
    		} 
	    	config.setName(name);
	    	config.setValue(strUpCards + "~" + strDownCards + "~" + type);
	    	config.setStatus(isOpen);
	    	config.setType("CUSTOM_POKER");
	    	HibernateSessionFactory.getSession().save(config);
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
    		info.setResult(F3SWebServiceResult.FAIL);
    	}
    	return toXML(info, getAliasTypes());
	}
    
    /**
     * 批量生成
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	public String BATCH_CUSTOM_POKER(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String[] num = request.getParameterValues("NUM");
    	String count = request.getParameter("COUNT");
    	String type = request.getParameter("TYPE");
    	String isOpen = String.valueOf(true);
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	try {
    		for (int times = 0; times < Integer.valueOf(count); times++) {
    			Date date = new Date();
    			String strUpDownCards = genRandomPokersByRate(num);
//    			
//				Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
//				criteria.add(Expression.eq("name", "CUSTOM_POKER_RED5_" + name));
//				criteria.add(Expression.eq("type", "CUSTOM_POKER"));
//				List<GlobalConfig> result = criteria.list();
//				if (result != null && result.size() > 0) {
//					info.setResult(F3SWebServiceResult.WARNING);
//					return toXML(info, getAliasTypes());
//				}
				GlobalConfig config = new GlobalConfig();
	    		config.setGlobalConfigId(UUID.randomUUID().toString());
		    	config.setName("CUSTOM_POKER_RED5_" + StringHelper.formatDate(date).replaceAll("-", "") + "_" + red5GameSettingStrs[Integer.valueOf(type)] + "_" + date.getTime());
		    	config.setValue(strUpDownCards + "~" + type); // FIXME
		    	// 2010-03-02 ADD BY ZWREN BEGIN
		    	config.setValue(strUpDownCards);
		    	// 2010-03-02 ADD BY ZWREN END
		    	config.setStatus(isOpen);
		    	config.setType("CUSTOM_POKER");
		    	HibernateSessionFactory.getSession().save(config);
    		}
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
    		info.setResult(F3SWebServiceResult.FAIL);
    	}
    	return toXML(info, getAliasTypes());
    }
	
	 /**
     * 删除牌型
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	public String DELETE_CUSTOM_POKER(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String id = request.getParameter("ID");
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	try {
    		GlobalConfigDAO dao = new GlobalConfigDAO();
    		GlobalConfig config = dao.findById(id);
    		if (config != null) {
    			dao.delete(config);
    		} 
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
    		info.setResult(F3SWebServiceResult.FAIL);
    	}
    	return toXML(info, getAliasTypes());
	}
    
    /**
     * 根据各种扑克出现比率生成相应牌型
     * @param num
     * @return
     */
    public String genRandomPokersByRate(String[] num) {
    	Map<String, Integer> map = genRandomPokerCountByRate(num);
    	StringBuffer strBuf = new StringBuffer();
		for (String key : map.keySet()) {
			int count = map.get(key);
			int color = 0;
			PokerColor[] colors = PokerColor.values();
			while (count > 0) {
				if (key.equals("VM")) {
					color = PokerColor.HEART.ordinal();
				} else if (key.equals("VX") || key.equals("VY")) {
					color = PokerColor.NO_COLOR.ordinal();
				} else if (key.equals("V5")) {
					color = colors[count > 4 ? count-2 : count].ordinal();
					color = color == 1 ? 2 : color;
				} else {
					color = colors[count > 4 ? count-4 : count].ordinal();
				}
				strBuf.append(color + key);
				strBuf.append(",");
				count--;
			}
		}
		String newPokers = strBuf.toString().replaceAll("M", "5").replaceAll(",$", "");
		// 排序
		return sortPoker(newPokers);
    }
    
    /**
     * @param pokers
     * @return
     */
    private String sortPoker(String pokers) {
    	String[] pokerArr = pokers.split(",");
    	List<String> pokerList = Arrays.asList(pokerArr);
    	Collections.sort(pokerList, new Comparator<String>() {
			public int compare(String card1, String card2) {
				return cardSorter(card1, card2);
			}
    	});
    	StringBuffer strBuf = new StringBuffer();
    	String allPokers = getAllPokers();
//    	System.out.println(allPokers);
    	pokerArr = pokerList.toArray(new String[]{});
    	for (String poker : pokerArr) {
    		strBuf.append(poker);
    		allPokers = allPokers.replaceFirst(poker + ",", "");
    		strBuf.append(",");
    	}
    	return strBuf.toString().replaceAll(",$", "") + "~" + allPokers.replaceAll(",$", "");
    }
    
    /**
     * 获取两副红五扑克[排序后]
     * @param poker
     * @return
     */
    private String getAllPokers() {
    	StringBuffer strBuf = new StringBuffer();
    	// 准备洗牌用的扑克
        List<Red5Poker> pokers = new ArrayList<Red5Poker>();
        // 10至大王的索引为7到13
        List<PokerValue> list = Arrays.asList(PokerValue.values()).subList(7, 13);
        // 准备两副扑克
        for (PokerValue pokerValue : list) {
            Red5Poker a = new Red5Poker(PokerColor.HEART, pokerValue);
            Red5Poker b = new Red5Poker(PokerColor.DIAMOND, pokerValue);
            Red5Poker c = new Red5Poker(PokerColor.CLUB, pokerValue);
            Red5Poker d = new Red5Poker(PokerColor.SPADE, pokerValue);
            for (int i = 0; i < 2; i++) {
            	pokers.add(a);
            }
            for (int i = 0; i < 2; i++) {
            	pokers.add(b);
            }
            for (int i = 0; i < 2; i++) {
            	pokers.add(c);
            }
            for (int i = 0; i < 2; i++) {
            	pokers.add(d);
            }
        }
        Red5Poker b = new Red5Poker(PokerColor.DIAMOND, PokerValue.V5);
        Red5Poker c = new Red5Poker(PokerColor.CLUB, PokerValue.V5);
        Red5Poker d = new Red5Poker(PokerColor.SPADE, PokerValue.V5);
        for (int i = 0; i < 2; i++) {
        	pokers.add(b);
        }
        for (int i = 0; i < 2; i++) {
        	pokers.add(c);
        }
        for (int i = 0; i < 2; i++) {
        	pokers.add(d);
        }
        for (int i = 0; i < 2; i++) {
        	pokers.add(new Red5Poker(PokerColor.NO_COLOR, PokerValue.VX));
        }
        for (int i = 0; i < 2; i++) {
        	pokers.add(new Red5Poker(PokerColor.NO_COLOR, PokerValue.VY));
        }
        for (int i = 0; i < 2; i++) {
        	pokers.add(new Red5Poker(PokerColor.HEART, PokerValue.V5));
        }
    	for (Red5Poker poker : pokers) {
    		strBuf.append(poker.getValue());
    		strBuf.append(",");
    	}
    	return strBuf.toString();
    }
    
    /**
     * @param card1
     * @param card2
     * @return
     */
    private int cardSorter(String card1, String card2) {
        if (card1 == card2) {
            // 值与花色都相同时
            return 0;
        } else if ("1V5".equals(card1)) {
            // 第一张牌为红五时
            return 1;
        } else if ("1V5".equals(card2)) {
            // 第二张牌为红五时
            return -1;
        }
        // 实现排序功能
        int pri1 = prioritySequence.indexOf(card1.replaceAll("^[0-4]", ""));
        int pri2 = prioritySequence.indexOf(card2.replaceAll("^[0-4]", ""));
        // 值比较
        if (pri1 > pri2) {
            return 1;
        } else if (pri1 < pri2) {
            return -1;
        }
        // 值相同时，进行花色比较
        if (card1.charAt(0) > card2.charAt(0)) {
            return 1;
        } else if (card1.charAt(0) < card2.charAt(0)) {
            return -1;
        }
        return 0;
    }
    
    
    /**
     * 根据各种扑克出现比率形成牌值及其个数的数组
     * @param num
     */
    public Map<String, Integer> genRandomPokerCountByRate(String[] num) {
    	int endValue = 0;
    	Map<String, String> pokerMap = new HashMap<String, String>();
    	Map<String, Integer> resultMap = new HashMap<String, Integer>();
    	// 牌值与其范围放到集合中
    	for (int x = 0; x < num.length; x++) {
    		int value = Integer.valueOf(num[x]);
    		int startValue = endValue + 1;
    		endValue+=value;
    		pokerMap.put(prioritySequence.split(",")[x] , startValue + "~" + endValue);
    	}
    	// 将生成的随机数在其范围内查找
    	return genPoker(pokerCount, pokerMap, resultMap);
    	
    }
    
    private Map<String, Integer> genPoker(int pokerCount, Map<String, String> pokerMap, Map<String, Integer> resultMap) {
    	while (pokerCount > 0) {
    		int random = (int)(new Random().nextInt(100) + 1);
    		for (String key : pokerMap.keySet()) {
    			String[] rates = pokerMap.get(key).split("~");
	    		if (random >= Integer.valueOf(rates[0]) && random <= Integer.valueOf(rates[1])) {
	    			// 统计每个牌值的数量
	    			if (resultMap.containsKey(key)) {
	    				int val = resultMap.get(key).intValue();
	    				if (getMaxCountByKey(key) == val) {
	    					return genPoker(pokerCount, pokerMap, resultMap);
	    				}
	    				resultMap.put(key, ++val);
	    			} else {
	    				resultMap.put(key, 1);
	    			}
	    			pokerCount--;
	    		}
    		}
    	}
    	return resultMap;
    }
    
    /**
     * 根据牌值获取相应牌值的最大牌数
     * @param arr
     * @param key
     * @return
     */
    private int getMaxCountByKey(String key) {
    	int maxCount = 6;
    	String[] pokerKey = prioritySequence.split(",");
    	for (int i = 0; i < pokerKey.length; i++) {
    		if (key.equals(pokerKey[i])) {
    			// 统计每个牌值的最大数量
    			if (i < 6) {
    				maxCount = 8;
    			} else if (i > 6){
    				maxCount = 2;
    			} else {
    				maxCount = 6;
    			}
    		}
    	}
    	return maxCount;
    }
}
