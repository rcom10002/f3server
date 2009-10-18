package info.knightrcom.data.metadata;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;

/**
 * A data access object (DAO) providing persistence and search support for
 * GameRecord entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.GameRecord
 * @author MyEclipse Persistence Tools
 */

public class GameRecordDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(GameRecordDAO.class);

    // property constants
    public static final String GAME_TYPE = "gameType";

    public static final String GAME_SETTING = "gameSetting";

    public static final String WINNER_NUMBERS = "winnerNumbers";

    public static final String PLAYERS = "players";

    public static final String SCORE = "score";

    public static final String SYSTEM_SCORE = "systemScore";

    public static final String RECORD = "record";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(GameRecord transientInstance) {
        log.debug("saving GameRecord instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(GameRecord persistentInstance) {
        log.debug("deleting GameRecord instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public GameRecord findById(java.lang.String id) {
        log.debug("getting GameRecord instance with id: " + id);
        try {
            GameRecord instance = (GameRecord) getSession().get("info.knightrcom.data.metadata.GameRecord", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
	public List<GameRecord> findByExample(GameRecord instance) {
        log.debug("finding GameRecord instance by example");
        try {
            List<GameRecord> results = (List<GameRecord>) getSession().createCriteria("info.knightrcom.data.metadata.GameRecord").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
	public List<GameRecord> findByProperty(String propertyName, Object value) {
        log.debug("finding GameRecord instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from GameRecord as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<GameRecord> findByGameType(Object gameType) {
        return findByProperty(GAME_TYPE, gameType);
    }

    public List<GameRecord> findByGameSetting(Object gameSetting) {
        return findByProperty(GAME_SETTING, gameSetting);
    }

    public List<GameRecord> findByWinnerNumbers(Object winnerNumbers) {
        return findByProperty(WINNER_NUMBERS, winnerNumbers);
    }

    public List<GameRecord> findByPlayers(Object players) {
        return findByProperty(PLAYERS, players);
    }

    public List<GameRecord> findByScore(Object score) {
        return findByProperty(SCORE, score);
    }

    public List<GameRecord> findBySystemScore(Object systemScore) {
        return findByProperty(SYSTEM_SCORE, systemScore);
    }

    public List<GameRecord> findByRecord(Object record) {
        return findByProperty(RECORD, record);
    }

    public List<GameRecord> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<GameRecord> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<GameRecord> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    @SuppressWarnings("unchecked")
	public List<GameRecord> findAll() {
        log.debug("finding all GameRecord instances");
        try {
            String queryString = "from GameRecord";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public GameRecord merge(GameRecord detachedInstance) {
        log.debug("merging GameRecord instance");
        try {
            GameRecord result = (GameRecord) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(GameRecord instance) {
        log.debug("attaching dirty GameRecord instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(GameRecord instance) {
        log.debug("attaching clean GameRecord instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
