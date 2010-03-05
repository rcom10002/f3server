package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.util.EncryptionUtil;

import java.util.UUID;

import junit.framework.TestCase;

/**
 * 
 * 
 */
public class PlayerProfilePuppetsTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HibernateSessionFactory.getSession().createSQLQuery("delete from player_profile where status like 'puppet%'").executeUpdate();
    }

    /**
     * It had better generate the puppets with a dynamic script such as JRuby
     */
    public void testCreatePlayerProfile() {
        // 添加组用户
    	PlayerProfile profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("puppet0");
        profile.setUserId("puppet0");
        profile.setPassword(EncryptionUtil.encryptSHA("puppet0"));
        profile.setRole("GroupUser");
        profile.setRlsPath("puppet0");
        profile.setCurrentScore(50000d);
        profile.setInitLimit(50000);
        profile.setLevel(0);
        profile.setStatus("puppet~Red5~Red5Fresh~currently_game_type_is_unused"); // puppet~class_prefix~room_id~game_type
        HibernateSessionFactory.getSession().save(profile);
        // 添加用户
        for (int i = 1; i <= 100; i++) {
            profile = new PlayerProfile();
            profile.setProfileId(UUID.randomUUID().toString());
            profile.setName("puppet" + i);
            profile.setUserId("puppet" + i);
            profile.setPassword(EncryptionUtil.encryptSHA("puppet" + i));
            profile.setRole("GroupUser");
            profile.setRlsPath("puppet0!puppet" + i);
            profile.setCurrentScore(5000d);
            profile.setInitLimit(5000);
            profile.setLevel(0);
            profile.setStatus("puppet~Red5~Red5Fresh~1~custom_poker_id");
            HibernateSessionFactory.getSession().save(profile);
        }
        HibernateSessionFactory.getSession().save(profile);
    }

    @Override
    protected void tearDown() throws Exception {
        HibernateSessionFactory.closeSession();
        super.tearDown();
    }
}
