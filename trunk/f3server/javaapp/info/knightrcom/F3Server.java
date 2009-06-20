package info.knightrcom;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.model.global.Platform;
import info.knightrcom.ssl.BogusSslContextFactory;
import info.knightrcom.util.ModelUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
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
 * 
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

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        ResourceBundle bundle = ResourceBundle.getBundle("info.knightrcom.sc");
        SECURITY_CONFIGURATION = bundle.getString("SECURITY_CONFIGURATION");

        acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
        addMdc(chain, mdcInjectionFilter);

        // 启用SSL功能
        if (USE_SSL) {
            addSSLSupport(chain);
        }
        addCodec(chain);
        addLogger(chain);

        // 监听绑定
        Platform platform = ModelUtil.createPlatform();
        acceptor.setHandler(new ServiceHandler(platform));
        acceptor.bind(new InetSocketAddress(PORT));
        log.info("LISTENING ON PORT " + PORT);

        // 启动Hibernate
        HibernateSessionFactory.init();
    }

    public static void shutdownServer() {
        try {
            acceptor.unbind();

            log.info("GFS Server has shutdown");
            System.exit(0);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    // FIXME CORRECT THIS SECTION WITH SSL
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