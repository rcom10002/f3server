/**
 * 
 */
package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.thoughtworks.xstream.XStream;

/**
 * 
 */
public class HibernateNamedQueryTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testNamedQuery() throws ParseException {
        HibernateSessionFactory.getConfiguration().configure("info/knightrcom/test/hibernate-test.cfg.xml");
        Query query = HibernateSessionFactory.getSession().getNamedQuery("mySqlQuery");
        // query.setMaxResults(2);
        // query.setFirstResult(2 * 1);
        query.setResultTransformer(Transformers.aliasToBean(Entity.class));
        query.setTimestamp(0, new SimpleDateFormat("yyyy-MM-dd").parse("2009-03-08"));
        query.setTimestamp(1, new Date());
        List list = query.list();
        XStream stream = new XStream();
        stream.setMode(XStream.NO_REFERENCES);
        stream.alias(Entity.class.getSimpleName(), Entity.class);
        System.out.println(stream.toXML(list));
    }
    public static class Entity {

        private String name;

        private String createTime;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the createTime
         */
        public String getCreateTime() {
            return createTime;
        }

        /**
         * @param createTime the createTime to set
         */
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

    }
}
