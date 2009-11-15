package info.knightrcom.data.metadata;

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;

/**
 	* A data access object (DAO) providing persistence and search support for PeriodlySumExt entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see info.knightrcom.data.metadata.PeriodlySumExt
  * @author MyEclipse Persistence Tools 
 */

public class PeriodlySumExtDAO extends BaseHibernateDAO  {
    private static final Log log = LogFactory.getLog(PeriodlySumExtDAO.class);
	//property constants
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
	public static final String CURRENT_SCORE = "currentScore";
	public static final String RECHARGE_SUM = "rechargeSum";
	public static final String EXPRESSION = "expression";
	public static final String RESULT_SCORE = "resultScore";
	public static final String CREATE_BY = "createBy";
	public static final String UPDATE_BY = "updateBy";



    
    public void save(PeriodlySumExt transientInstance) {
        log.debug("saving PeriodlySumExt instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(PeriodlySumExt persistentInstance) {
        log.debug("deleting PeriodlySumExt instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public PeriodlySumExt findById( java.lang.String id) {
        log.debug("getting PeriodlySumExt instance with id: " + id);
        try {
            PeriodlySumExt instance = (PeriodlySumExt) getSession()
                    .get("info.knightrcom.data.metadata.PeriodlySumExt", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    @SuppressWarnings("unchecked")
	public List<PeriodlySumExt> findByExample(PeriodlySumExt instance) {
        log.debug("finding PeriodlySumExt instance by example");
        try {
        	List<PeriodlySumExt> results = (List<PeriodlySumExt>) getSession().createCriteria("info.knightrcom.data.metadata.PeriodlySumExt").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }    
    
    @SuppressWarnings("unchecked")
	public List<PeriodlySumExt> findByProperty(String propertyName, Object value) {
      log.debug("finding PeriodlySumExt instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from PeriodlySumExt as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<PeriodlySumExt> findByProfileId(Object profileId
	) {
		return findByProperty(PROFILE_ID, profileId
		);
	}
	
	public List<PeriodlySumExt> findByNumber(Object number
	) {
		return findByProperty(NUMBER, number
		);
	}
	
	public List<PeriodlySumExt> findByTitle(Object title
	) {
		return findByProperty(TITLE, title
		);
	}
	
	public List<PeriodlySumExt> findByWinTimes(Object winTimes
	) {
		return findByProperty(WIN_TIMES, winTimes
		);
	}
	
	public List<PeriodlySumExt> findByWinScores(Object winScores
	) {
		return findByProperty(WIN_SCORES, winScores
		);
	}
	
	public List<PeriodlySumExt> findByLoseTimes(Object loseTimes
	) {
		return findByProperty(LOSE_TIMES, loseTimes
		);
	}
	
	public List<PeriodlySumExt> findByLoseScores(Object loseScores
	) {
		return findByProperty(LOSE_SCORES, loseScores
		);
	}
	
	public List<PeriodlySumExt> findByDrawTimes(Object drawTimes
	) {
		return findByProperty(DRAW_TIMES, drawTimes
		);
	}
	
	public List<PeriodlySumExt> findByDrawScores(Object drawScores
	) {
		return findByProperty(DRAW_SCORES, drawScores
		);
	}
	
	public List<PeriodlySumExt> findByTotalTimes(Object totalTimes
	) {
		return findByProperty(TOTAL_TIMES, totalTimes
		);
	}
	
	public List<PeriodlySumExt> findByTotalScores(Object totalScores
	) {
		return findByProperty(TOTAL_SCORES, totalScores
		);
	}
	
	public List<PeriodlySumExt> findByTotalSystemScore(Object totalSystemScore
	) {
		return findByProperty(TOTAL_SYSTEM_SCORE, totalSystemScore
		);
	}
	
	public List<PeriodlySumExt> findByStatus(Object status
	) {
		return findByProperty(STATUS, status
		);
	}
	
	public List<PeriodlySumExt> findByCurrentScore(Object currentScore
	) {
		return findByProperty(CURRENT_SCORE, currentScore
		);
	}
	
	public List<PeriodlySumExt> findByRechargeSum(Object rechargeSum
	) {
		return findByProperty(RECHARGE_SUM, rechargeSum
		);
	}
	
	public List<PeriodlySumExt> findByExpression(Object expression
	) {
		return findByProperty(EXPRESSION, expression
		);
	}
	
	public List<PeriodlySumExt> findByResultScore(Object resultScore
	) {
		return findByProperty(RESULT_SCORE, resultScore
		);
	}
	
	public List<PeriodlySumExt> findByCreateBy(Object createBy
	) {
		return findByProperty(CREATE_BY, createBy
		);
	}
	
	public List<PeriodlySumExt> findByUpdateBy(Object updateBy
	) {
		return findByProperty(UPDATE_BY, updateBy
		);
	}
	

	@SuppressWarnings("unchecked")
	public List<PeriodlySumExt> findAll() {
		log.debug("finding all PeriodlySumExt instances");
		try {
			String queryString = "from PeriodlySumExt";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public PeriodlySumExt merge(PeriodlySumExt detachedInstance) {
        log.debug("merging PeriodlySumExt instance");
        try {
            PeriodlySumExt result = (PeriodlySumExt) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(PeriodlySumExt instance) {
        log.debug("attaching dirty PeriodlySumExt instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(PeriodlySumExt instance) {
        log.debug("attaching clean PeriodlySumExt instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}