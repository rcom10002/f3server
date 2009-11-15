package info.knightrcom.web.service;

import java.util.List;
import java.util.UUID;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

public class CustomPokerService extends F3SWebService<GlobalConfig> {

	@Override
	public Class<?>[] getAliasTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamedQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamedQueryForCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultTransformer getResultTransformer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processQuerySetting(Query query, HttpServletRequest request) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String READ_POKER(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String pokersValue = "";
    	List<GlobalConfig> list = new GlobalConfigDAO().findByType("CUSTOM_POKER");
    	if (list != null && list.size() > 0) {
    		pokersValue = list.get(0).getValue() + "~" + list.get(0).getStatus();
    	} else {
	    	Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle();
	        StringBuilder builder = new StringBuilder();
	    	for (int m = 0; m < eachShuffledPokers.length; m++) {
	            for (int n = 0; n < eachShuffledPokers[m].length; n++) {
	                builder.append(eachShuffledPokers[m][n].getValue() + ",");
	            }
	    	}
	    	pokersValue = builder.toString().replaceAll(",$", "");
    	}
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	info.setTag(pokersValue);
    	return toXML(info, GlobalConfig.class);
	}
    
    /**
     * 保存牌型
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String SAVE_CUSTOM_POKER(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String strUpCards = request.getParameter("UP_POKERS");
    	String strDownCards = request.getParameter("DOWN_POKERS");
    	String isOpen = request.getParameter("IS_OPEN");
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	try {
	    	GlobalConfig config = new GlobalConfig();
	    	config.setGlobalConfigId(UUID.randomUUID().toString());
	    	config.setName("CUSTOM_POKER");
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
