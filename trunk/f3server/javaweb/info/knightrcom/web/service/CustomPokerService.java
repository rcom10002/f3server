package info.knightrcom.web.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;

public class CustomPokerService extends F3SWebService<GlobalConfig> {

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
    		Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle();
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
	    	config.setValue(strUpCards + "~" + strDownCards);
	    	config.setStatus(isOpen);
	    	config.setType("CUSTOM_POKER");
	    	HibernateSessionFactory.getSession().save(config);
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
    		info.setResult(F3SWebServiceResult.FAIL);
    	}
    	return toXML(info, getAliasTypes());
	}

}
