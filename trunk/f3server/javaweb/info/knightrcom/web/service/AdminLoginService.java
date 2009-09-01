package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

/**
 * 管理界面的后台登录服务
 */
public class AdminLoginService extends F3SWebServiceAdaptor<Object> {

	public String LOGIN_ADMIN_SERVER(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("USERNAME");
		String password = request.getParameter("PASSWORD");
        final PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(
                Restrictions.and(Property.forName("userId").eq(username), 
                                 Property.forName("password").eq(password))).uniqueResult();
        EntityInfo<Object> info = new EntityInfo<Object>();
        if (profile != null && !"User".equals(profile.getRole())) {
            info.setEntity(new Object() {
                @SuppressWarnings("unused")
                String profileId = profile.getRole();
                @SuppressWarnings("unused")
                String userId = profile.getUserId();
                @SuppressWarnings("unused")
                String rslPath = profile.getRlsPath();
                @SuppressWarnings("unused")
                String role = profile.getRole();
            });
            request.getSession().setAttribute("PROFILE", profile);
        	info.setResult(F3SWebServiceResult.SUCCESS);
        } else {
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info);
	}
}
