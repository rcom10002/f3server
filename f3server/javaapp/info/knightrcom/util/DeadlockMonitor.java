package info.knightrcom.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeadlockMonitor extends TimerTask {

    private static boolean running = false;

    private static final Log log = LogFactory.getLog(DeadlockMonitor.class);

    @Override
    public void run() {
        log.debug("monitoring ...");
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        long[] ids = tmx.findDeadlockedThreads();
        if (ids != null) {
            ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
            System.err.println("The following threads are deadlocked:");
            for (ThreadInfo ti : infos) {
                System.err.println(ti);
            }
        }
    }

    public static void go() {
        if (running) {
            return;
        }
        new Timer().scheduleAtFixedRate(new DeadlockMonitor(), 30 * 1000, 30 * 1000);
        running = true;
    }
}
