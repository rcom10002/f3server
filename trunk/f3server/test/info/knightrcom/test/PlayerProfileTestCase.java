package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

public class PlayerProfileTestCase extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testCreatePlayerProfile() {
        HibernateSessionFactory.getSession().beginTransaction();
        List<PlayerProfile> players = HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).list();
        for (int i = 0; i < players.size(); i++) {
            HibernateSessionFactory.getSession().delete(players.get(i));
        }
        PlayerProfile profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("Administrator");
        profile.setUserId("admin");
        profile.setPassword("000000");
        profile.setCreateTime(new Date());
        profile.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().merge(profile);
        for (int i = 0; i < 6; i++) {
            profile = new PlayerProfile();
            profile.setProfileId(UUID.randomUUID().toString());
            profile.setName("user" + (i + 1));
            profile.setUserId("user" + (i + 1));
            profile.setPassword("user" + (i + 1));
            profile.setCurrentScore(100);
            profile.setCreateTime(new Date());
            profile.setUpdateTime(new Date());
            HibernateSessionFactory.getSession().merge(profile);
        }
        HibernateSessionFactory.getSession().getTransaction().commit();
        HibernateSessionFactory.closeSession();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
