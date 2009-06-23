package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class AdminLoginService extends F3SWebService<Object> {

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

	public String LOGIN_ADMIN_SERVER(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("USERNAME");
		String password = request.getParameter("PASSWORD");
        PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.and(
                Property.forName("userId").eq(username), 
                Property.forName("password").eq(password))).uniqueResult();
        EntityInfo<Object> info = new EntityInfo<Object>();
        if (profile != null) {
        	info.setResult(F3SWebServiceResult.SUCCESS);
        } else {
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info);
	}
}
