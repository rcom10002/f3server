package info.knightrcom.web.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.red5.Red5Poker;
import info.knightrcom.web.model.EntityInfo;

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
    	Red5Poker[][] eachShuffledPokers = Red5Poker.shuffle();
        StringBuilder builder = new StringBuilder();
    	for (int m = 0; m < eachShuffledPokers.length; m++) {
            for (int n = 0; n < eachShuffledPokers[m].length; n++) {
                builder.append(eachShuffledPokers[m][n].getValue() + ",");
            }
    	}
    	String pokersValue = builder.toString().replaceAll(",$", "");
    	EntityInfo<GlobalConfig> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	info.setTag(pokersValue);
    	return toXML(info, GlobalConfig.class);
	}

}
