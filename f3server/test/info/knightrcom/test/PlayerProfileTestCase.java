package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;

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
        profile.setRole("Administrator");
        HibernateSessionFactory.getSession().merge(profile);
        for (int i = 1; i <= 6; i++) {
            profile = new PlayerProfile();
            profile.setProfileId(UUID.randomUUID().toString());
            profile.setName("user" + i);
            profile.setUserId("user" + i);
            profile.setPassword("user" + i);
            profile.setRole("GroupUser");
            profile.setRlsPath("user" + i);
            profile.setCurrentScore(500);
            HibernateSessionFactory.getSession().merge(profile);
        }
        // 添加组用户
        profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("user33");
        profile.setUserId("user33");
        profile.setPassword("user33");
        profile.setRole("GroupUser");
        profile.setRlsPath("user3!user33");
        profile.setCurrentScore(300);
        HibernateSessionFactory.getSession().merge(profile);
        profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("user44");
        profile.setUserId("user44");
        profile.setPassword("user44");
        profile.setRole("User");
        profile.setRlsPath("user4!user44");
        profile.setCurrentScore(300);
        HibernateSessionFactory.getSession().merge(profile);
        HibernateSessionFactory.getSession().getTransaction().commit();
        HibernateSessionFactory.closeSession();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
