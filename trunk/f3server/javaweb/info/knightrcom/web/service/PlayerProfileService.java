package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.web.model.EntityInfo;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class PlayerProfileService extends F3SWebService<PlayerProfile> {

    @Override
    public String getNamedQuery() {
        return "PLAYER_PROFILE";
    }

    @Override
    public String getNamedQueryForCount() {
        return "PLAYER_PROFILE_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {        
    }

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class<?>[] {PlayerProfile.class};
    }

    public String CREATE_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setProfileId(UUID.randomUUID().toString());
        playerProfile.setUserId(request.getParameter("USER_ID"));
        playerProfile.setPassword(request.getParameter("PASSWORD"));
        // playerProfile.setRole(request.getParameter("ROLE"));
        playerProfile.setCreateTime(new Date());
        playerProfile.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().save(playerProfile);
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setEntity(playerProfile);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    public String READ_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        final PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("PROFILE_ID"));
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setEntity(playerProfile);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    public String UPDATE_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("PROFILE_ID"));
        playerProfile.setUserId(request.getParameter("USER_ID"));
        playerProfile.setPassword(request.getParameter("PASSWORD"));
        // playerProfile.setRole(request.getParameter("ROLE"));
        playerProfile.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().update(playerProfile);
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    public String DELETE_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("PROFILE_ID"));
        new PlayerProfileDAO().delete(playerProfile);
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }
}
