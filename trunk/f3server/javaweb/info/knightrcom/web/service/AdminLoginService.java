package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.Restrictions;

/**
 * 管理界面的后台登录服务
 */
public class AdminLoginService extends F3SWebServiceAdaptor<Object> {

	public String LOGIN_ADMIN_SERVER(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("USERNAME");
		String password = EncryptionUtil.encryptSHA(request.getParameter("PASSWORD"));
		String userClientVersion = request.getParameter("CLIENTVERSION").replaceAll("^.*\\((.*?)\\).*$", "$1");

		final PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(
                PlayerProfile.class).add(
                        Restrictions.eq("userId", username)).add(
                        Restrictions.eq("password", password)).add(
                        Restrictions.ne("role", "User")).add(
                        Restrictions.eq("status", "1")).uniqueResult();
        EntityInfo<Object> info = new EntityInfo<Object>();

        if (profile != null && !"User".equals(profile.getRole())) {
            info.setEntity(new Object() {
                @SuppressWarnings("unused")
                String profileId = profile.getProfileId();
                @SuppressWarnings("unused")
                String userId = profile.getUserId();
                @SuppressWarnings("unused")
                String rslPath = profile.getRlsPath();
                @SuppressWarnings("unused")
                String role = profile.getRole();
            });
            if (userClientVersion.matches(ModelUtil.getSystemParameter("ALLOWED_ADMIN_CLIENT_VERSION", "^.*$"))) {
            	info.setResult(F3SWebServiceResult.SUCCESS);
            } else {
            	info.setResult(F3SWebServiceResult.WARNING);
            }
        } else {
        	info.setResult(F3SWebServiceResult.FAIL);
        }

        return toXML(info);
	}
}
