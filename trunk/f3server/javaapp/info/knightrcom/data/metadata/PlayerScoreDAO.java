package info.knightrcom.data.metadata;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;

/**
 * A data access object (DAO) providing persistence and search support for
 * PlayerScore entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.PlayerScore
 * @author MyEclipse Persistence Tools
 */

public class PlayerScoreDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(PlayerScoreDAO.class);

    // property constants
    public static final String PROFILE_ID = "profileId";

    public static final String GAME_ID = "gameId";

    public static final String USER_ID = "userId";

    public static final String CURRENT_NUMBER = "currentNumber";

    public static final String SCORE = "score";

    public static final String SYSTEM_SCORE = "systemScore";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(PlayerScore transientInstance) {
        log.debug("saving PlayerScore instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(PlayerScore persistentInstance) {
        log.debug("deleting PlayerScore instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public PlayerScore findById(java.lang.String id) {
        log.debug("getting PlayerScore instance with id: " + id);
        try {
            PlayerScore instance = (PlayerScore) getSession().get("info.knightrcom.data.metadata.PlayerScore", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<PlayerScore> findByExample(PlayerScore instance) {
        log.debug("finding PlayerScore instance by example");
        try {
            List<PlayerScore> results = (List<PlayerScore>) getSession().createCriteria("info.knightrcom.data.metadata.PlayerScore").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List findByProperty(String propertyName, Object value) {
        log.debug("finding PlayerScore instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from PlayerScore as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<PlayerScore> findByProfileId(Object profileId) {
        return findByProperty(PROFILE_ID, profileId);
    }

    public List<PlayerScore> findByGameId(Object gameId) {
        return findByProperty(GAME_ID, gameId);
    }

    public List<PlayerScore> findByUserId(Object userId) {
        return findByProperty(USER_ID, userId);
    }

    public List<PlayerScore> findByCurrentNumber(Object currentNumber) {
        return findByProperty(CURRENT_NUMBER, currentNumber);
    }

    public List<PlayerScore> findByScore(Object score) {
        return findByProperty(SCORE, score);
    }

    public List<PlayerScore> findBySystemScore(Object systemScore) {
        return findByProperty(SYSTEM_SCORE, systemScore);
    }

    public List<PlayerScore> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<PlayerScore> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<PlayerScore> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    public List findAll() {
        log.debug("finding all PlayerScore instances");
        try {
            String queryString = "from PlayerScore";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public PlayerScore merge(PlayerScore detachedInstance) {
        log.debug("merging PlayerScore instance");
        try {
            PlayerScore result = (PlayerScore) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(PlayerScore instance) {
        log.debug("attaching dirty PlayerScore instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(PlayerScore instance) {
        log.debug("attaching clean PlayerScore instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
