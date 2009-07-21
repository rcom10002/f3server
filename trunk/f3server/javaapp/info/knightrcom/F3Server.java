package info.knightrcom;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.model.global.Platform;
import info.knightrcom.ssl.BogusSslContextFactory;
import info.knightrcom.util.ModelUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

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
public class F3Server {

    private static final Log log = LogFactory.getLog(F3Server.class);

    /**
     * 控制台参数
     */
    public static class ConsoleArgs {

        /**
         * @param args
         * @return
         */
        public static ConsoleArgs parseArgs(String[] args) {
            try {

                return null;
            } catch (Exception e) {
                throw new RuntimeException("");
            }
        }
    }

    private static boolean USE_SSL = false;

    private static SocketAcceptor acceptor;

    private static int PORT = 2009;

    public static String SECURITY_CONFIGURATION;

    public static final int MAX_CONNECTION_LIMIT = 1000;

    public static boolean RUNNING = false;

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
    public static void startServer(String[] args) throws Exception {

    	if (RUNNING) {
    		return;
    	}

        // Hibernate初始化
        HibernateSessionFactory.init();

        // 加载Flex安全信息
        ResourceBundle bundle = ResourceBundle.getBundle("info.knightrcom.sc");
        SECURITY_CONFIGURATION = bundle.getString("SECURITY_CONFIGURATION");

        acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
        addMdc(chain, mdcInjectionFilter);

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
        Platform platform = ModelUtil.createPlatform();
        acceptor.setHandler(new ServiceHandler(platform));
        acceptor.bind(new InetSocketAddress(PORT));
        log.info("LISTENING ON PORT " + PORT);

        log.info("F3S SERVER HAS STARTED!");
        RUNNING = true;
    }

    /**
     * 关闭服务器
     */
    public static void shutdownServer() {
        try {
        	if (!RUNNING) {
        		return;
        	}
            acceptor.unbind();
            acceptor.dispose();
            log.info("F3S SERVER HAS SHUTDOWN!");
            RUNNING = false;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
    
    private static void addSSLSupport(DefaultIoFilterChainBuilder chain) throws Exception {
        SslFilter sslFilter = new SslFilter(BogusSslContextFactory.getInstance(true));
        chain.addLast("sslFilter", sslFilter);
        log.info("SSL ON");
    }

    private static void addMdc(DefaultIoFilterChainBuilder chain, MdcInjectionFilter mdcInjectionFilter) throws Exception {
        chain.addLast("mdc", mdcInjectionFilter);
        log.info("MDC ON");
    }

    private static void addCodec(DefaultIoFilterChainBuilder chain) throws Exception {
        TextLineCodecFactory tlcf = new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.NUL, LineDelimiter.NUL);
        tlcf.setDecoderMaxLineLength(Integer.MAX_VALUE);
        tlcf.setEncoderMaxLineLength(Integer.MAX_VALUE);
        chain.addLast("codec", new ProtocolCodecFilter(tlcf));
        log.info("CODEC ON");
    }

    private static void addLogger(DefaultIoFilterChainBuilder chain) throws Exception {
        chain.addLast("logger", new LoggingFilter());
        log.info("LOG ON");
    }
}
