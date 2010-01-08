package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import junit.framework.TestCase;

public class LogInfoTest extends TestCase {

    public void testSaveLogInfo() {
        LogInfo log = new LogInfo();
        log.setLogId(UUID.randomUUID().toString());
        log.setCaption("TEST " + new Date().toString());
        log.setType("TEST_DATA");
        log.setInfo("This info was generated at " + DateFormat.getDateTimeInstance().format(new Date()));
        HibernateSessionFactory.getSession().save(log);
        HibernateSessionFactory.closeSession();
    }
}
