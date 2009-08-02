package info.knightrcom.data.metadata;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;

/**
 * A data access object (DAO) providing persistence and search support for
 * PeriodlySum entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.PeriodlySum
 * @author MyEclipse Persistence Tools
 */

public class PeriodlySumDAO extends BaseHibernateDAO {
	private static final Log log = LogFactory.getLog(PeriodlySumDAO.class);
	// property constants
	public static final String PROFILE_ID = "profileId";
	public static final String NUMBER = "number";
	public static final String TITLE = "title";
	public static final String WIN_TIMES = "winTimes";
	public static final String WIN_SCORES = "winScores";
	public static final String LOSE_TIMES = "loseTimes";
	public static final String LOSE_SCORES = "loseScores";
	public static final String DRAW_TIMES = "drawTimes";
	public static final String DRAW_SCORES = "drawScores";
	public static final String TOTAL_TIMES = "totalTimes";
	public static final String TOTAL_SCORES = "totalScores";
	public static final String TOTAL_SYSTEM_SCORE = "totalSystemScore";
	public static final String STATUS = "status";
	public static final String CREATE_BY = "createBy";
	public static final String UPDATE_BY = "updateBy";

	public void save(PeriodlySum transientInstance) {
		log.debug("saving PeriodlySum instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(PeriodlySum persistentInstance) {
		log.debug("deleting PeriodlySum instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public PeriodlySum findById(java.lang.String id) {
		log.debug("getting PeriodlySum instance with id: " + id);
		try {
			PeriodlySum instance = (PeriodlySum) getSession().get(
					"info.knightrcom.data.metadata.PeriodlySum", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<PeriodlySum> findByExample(PeriodlySum instance) {
		log.debug("finding PeriodlySum instance by example");
		try {
			List<PeriodlySum> results = getSession().createCriteria(
					"info.knightrcom.data.metadata.PeriodlySum").add(
					Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<PeriodlySum> findByProperty(String propertyName, Object value) {
		log.debug("finding PeriodlySum instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from PeriodlySum as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<PeriodlySum> findByProfileId(Object profileId) {
        return findByProperty(PROFILE_ID, profileId);
    }

	public List<PeriodlySum> findByNumber(Object number) {
		return findByProperty(NUMBER, number);
	}

	public List<PeriodlySum> findByTitle(Object title) {
		return findByProperty(TITLE, title);
	}

	public List<PeriodlySum> findByWinTimes(Object winTimes) {
		return findByProperty(WIN_TIMES, winTimes);
	}

	public List<PeriodlySum> findByWinScores(Object winScores) {
		return findByProperty(WIN_SCORES, winScores);
	}

	public List<PeriodlySum> findByLoseTimes(Object loseTimes) {
		return findByProperty(LOSE_TIMES, loseTimes);
	}

	public List<PeriodlySum> findByLoseScores(Object loseScores) {
		return findByProperty(LOSE_SCORES, loseScores);
	}

	public List<PeriodlySum> findByDrawTimes(Object drawTimes) {
		return findByProperty(DRAW_TIMES, drawTimes);
	}

	public List<PeriodlySum> findByDrawScores(Object drawScores) {
		return findByProperty(DRAW_SCORES, drawScores);
	}

	public List<PeriodlySum> findByTotalTimes(Object totalTimes) {
		return findByProperty(TOTAL_TIMES, totalTimes);
	}

	public List<PeriodlySum> findByTotalScores(Object totalScores) {
		return findByProperty(TOTAL_SCORES, totalScores);
	}

	public List<PeriodlySum> findByTotalSystemScore(Object totalSystemScore) {
		return findByProperty(TOTAL_SYSTEM_SCORE, totalSystemScore);
	}

	public List<PeriodlySum> findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List<PeriodlySum> findByCreateBy(Object createBy) {
		return findByProperty(CREATE_BY, createBy);
	}

	public List<PeriodlySum> findByUpdateBy(Object updateBy) {
		return findByProperty(UPDATE_BY, updateBy);
	}

	public List<PeriodlySum> findAll() {
		log.debug("finding all PeriodlySum instances");
		try {
			String queryString = "from PeriodlySum";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public PeriodlySum merge(PeriodlySum detachedInstance) {
		log.debug("merging PeriodlySum instance");
		try {
			PeriodlySum result = (PeriodlySum) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(PeriodlySum instance) {
		log.debug("attaching dirty PeriodlySum instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(PeriodlySum instance) {
		log.debug("attaching clean PeriodlySum instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}