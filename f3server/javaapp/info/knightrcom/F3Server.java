package info.knightrcom;

import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.ssl.BogusSslContextFactory;
import info.knightrcom.util.DeadlockMonitor;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.util.StringHelper;
import info.knightrcom.util.SystemLogger;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * f3server - Flex Simple Socket Server
 */
class F3Server {

    private static final Log log = LogFactory.getLog(F3Server.class);

    public static boolean USE_SSL = false;

    public static int PORT = 2009;

    public static String SECURITY_CONFIGURATION;

    public static final int MAX_CONNECTION_LIMIT = 1000;

    public static boolean RUNNING = false;

    public static SocketAcceptor acceptor;

    /**
     * 控制台参数
     */
    public static class AccessArgs {
        /**
         * @param args
         * @return
         */
        public static AccessArgs parseArgs(String[] args) {
            try {

                return null;
            } catch (Exception e) {
                throw new RuntimeException("");
            }
        }
    }

    /**
     * 启动应用服务器
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        startServer(args);
    }

    /**
     * 判断服务器是否处于运行状态
     * 
     * @return
     */
    public static boolean isRunning() {
    	return RUNNING;
    }

    /**
     * 启动服务器
     * 
     * @param args 启动参数
     */
    static void startServer(String[] args) {
    	// 服务器运行状态判断
    	if (RUNNING) {
    		return;
    	}
    	try {
	        // 加载Flex安全信息
            // FIXME properties file can be deleted because this property can be fetched from model utility.
	        // ResourceBundle bundle = ResourceBundle.getBundle("info.knightrcom.sc");
	        // SECURITY_CONFIGURATION = bundle.getString("SECURITY_CONFIGURATION");
	        SECURITY_CONFIGURATION = ModelUtil.getSystemParameter("SECURITY_CONFIGURATION", "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>");

	        acceptor = new NioSocketAcceptor();
	        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

	        MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
	        addMdc(chain, mdcInjectionFilter);
	        // addExecutor(chain);
	        // 启用SSL功能
	        // TODO 该功能暂时不启用，因为as3对SSL支持尚不成熟
	        // Keep in mind most web servers with TLS support are not serving a crossdomain.xml policy file that
	        // would allow a flash movie loaded from the internet to access them.
	        if (USE_SSL) {
	            addSSLSupport(chain);
	        }
	        addCodec(chain);
	        addLogger(chain);

	        // 绑定处理器和监听
	        acceptor.setHandler(new F3ServerServiceHandler(ModelUtil.getPlatform()));
	        acceptor.bind(new InetSocketAddress(PORT));
	        log.info("LISTENING ON PORT " + PORT);

	        // 启动成功日志
	        LogInfo logInfo = SystemLogger.createLog("F3Server started successfully!", null, null, LogType.SYSTEM_LOG);
	        HibernateSessionFactory.getSession().save(logInfo);
	        HibernateSessionFactory.closeSession();

	        log.info("F3S SERVER HAS STARTED!");
	        RUNNING = true;
	        
	        if (!"false".equalsIgnoreCase(ModelUtil.getSystemParameter("DEADLOCK_MONITOR_ENABLED", true))) {
	            DeadlockMonitor.go();
	        }
	        log.info("DEADLOCK MONITOR ENABLED!");
    	} catch (Exception e) {
    		// 启动失败日志
	        LogInfo logInfo = SystemLogger.createLog("F3Server was failed to start!", 
	        		e.getMessage(), StringHelper.convertExceptionStack2String(e), LogType.SYSTEM_ERROR);
	        HibernateSessionFactory.getSession().save(logInfo);
	        HibernateSessionFactory.closeSession();

    		throw new RuntimeException(e);
    	}
    }

    /**
     * 关闭服务器
     */
    static void shutdownServer() {
        try {
        	// 服务器运行状态判断
        	if (!RUNNING) {
        		return;
        	}
            acceptor.unbind();
            acceptor.dispose();
            acceptor = null;
            log.info("F3S SERVER HAS SHUTDOWN!");
            // 清理内存模型
            ModelUtil.resetModels();
            RUNNING = false;

            // 关闭成功日志
	        LogInfo logInfo = SystemLogger.createLog("F3Server stopped successfully!", null, null, LogType.SYSTEM_LOG);
	        HibernateSessionFactory.getSession().save(logInfo);
	        HibernateSessionFactory.closeSession();

        } catch (Exception e) {
        	// 启动失败日志
	        LogInfo logInfo = SystemLogger.createLog("F3Server was failed to stop!", 
	        		e.getMessage(), StringHelper.convertExceptionStack2String(e), LogType.SYSTEM_ERROR);
	        HibernateSessionFactory.getSession().save(logInfo);
	        HibernateSessionFactory.closeSession();

            log.error(e.getMessage());
        }
    }

    /**
     * @param chain
     * @throws Exception
     */
    private static void addSSLSupport(DefaultIoFilterChainBuilder chain) throws Exception {
        SslFilter sslFilter = new SslFilter(BogusSslContextFactory.getInstance(true));
        chain.addLast("sslFilter", sslFilter);
        log.info("SSL ON");
    }

    /**
     * @param chain
     * @param mdcInjectionFilter
     * @throws Exception
     */
    private static void addMdc(DefaultIoFilterChainBuilder chain, MdcInjectionFilter mdcInjectionFilter) throws Exception {
        chain.addLast("mdc", mdcInjectionFilter);
        log.info("MDC ON");
    }

//    /**
//     * @param chain
//     * @throws Exception
//     */
//    private static void addExecutor(DefaultIoFilterChainBuilder chain) throws Exception {
//        // TODO 控制线程池大小？
//        // http://mina.apache.org/using-an-executor-filter.html
//        // - Using an Executor Filter - Controlling the size of thread pool and choosing the right thread model
//        // *** ExecutorService executor = Executors.newFixedThreadPool(MAX_CONNECTION_LIMIT); ***
//        // About this section: refer to http://mina.apache.org/report/trunk/apidocs/index.html?org/apache/mina/filter/executor/OrderedThreadPoolExecutor.html
//        // USELESS
//        ExecutorService executor = new OrderedThreadPoolExecutor(MAX_CONNECTION_LIMIT < 100 ? MAX_CONNECTION_LIMIT : (MAX_CONNECTION_LIMIT / 2 < 100 ? 100 : MAX_CONNECTION_LIMIT / 2));
//        chain.addLast("executor", new ExecutorFilter(executor));
//    }

    /**
     * @param chain
     * @throws Exception
     */
    private static void addCodec(DefaultIoFilterChainBuilder chain) throws Exception {
        TextLineCodecFactory tlcf = new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.NUL, LineDelimiter.NUL);
        tlcf.setDecoderMaxLineLength(Integer.MAX_VALUE);
        tlcf.setEncoderMaxLineLength(Integer.MAX_VALUE);
        chain.addLast("codec", new ProtocolCodecFilter(tlcf));
        log.info("CODEC ON");
    }

    /**
     * @param chain
     * @throws Exception
     */
    private static void addLogger(DefaultIoFilterChainBuilder chain) throws Exception {
        chain.addLast("logger", new LoggingFilter());
        log.info("LOG ON");
    }
}
