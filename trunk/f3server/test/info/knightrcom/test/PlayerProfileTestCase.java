package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.util.EncryptionUtil;

import java.util.UUID;

import junit.framework.TestCase;

public class PlayerProfileTestCase extends TestCase {

    public PlayerProfileTestCase() {
        HibernateSessionFactory.getSession().createSQLQuery("delete from player_score").executeUpdate();
        HibernateSessionFactory.getSession().createSQLQuery("delete from game_record").executeUpdate();
        HibernateSessionFactory.getSession().createSQLQuery("delete from periodly_sum").executeUpdate();
        HibernateSessionFactory.getSession().createSQLQuery("delete from player_profile").executeUpdate();
    }

    public void testCreateAdministratorProfile() {
        PlayerProfile profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("admin");
        profile.setUserId("admin");
        profile.setPassword(EncryptionUtil.encryptSHA("bountyofking:)123"));
        profile.setRole("Administrator");
        profile.setRlsPath("Administrator");
        profile.setCurrentScore(-1d);
        profile.setInitLimit(-1);
        profile.setLevel(100);
        profile.setStatus("1");
        HibernateSessionFactory.getSession().save(profile);
    }

    public void testCreateGameMasterProfile() {
        PlayerProfile profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("SuperGameMaster");
        profile.setUserId("SuperGameMaster");
        profile.setPassword(EncryptionUtil.encryptSHA("SuperGameMaster"));
        profile.setRole("SuperGameMaster");
        profile.setRlsPath("Administrator");
        profile.setCurrentScore(-1d);
        profile.setInitLimit(-1);
        profile.setLevel(100);
        profile.setStatus("0");
        HibernateSessionFactory.getSession().save(profile);
        profile = new PlayerProfile();
        profile.setProfileId(UUID.randomUUID().toString());
        profile.setName("GameMaster");
        profile.setUserId("GameMaster");
        profile.setPassword(EncryptionUtil.encryptSHA("GameMaster"));
        profile.setRole("GameMaster");
        profile.setRlsPath("Administrator");
        profile.setCurrentScore(-1d);
        profile.setInitLimit(-1);
        profile.setLevel(100);
        profile.setStatus("1");
        HibernateSessionFactory.getSession().save(profile);
    }

    @Override
    protected void tearDown() throws Exception {
        HibernateSessionFactory.closeSession();
        super.tearDown();
    }
}
