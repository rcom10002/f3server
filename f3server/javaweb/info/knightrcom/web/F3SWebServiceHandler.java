package info.knightrcom.web;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.web.service.F3SWebService;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class F3SWebServiceHandler {

    protected static Log log = LogFactory.getLog(F3SWebServiceHandler.class);

    private static Map<String, F3SWebService<?>> services = new HashMap<String, F3SWebService<?>>();

    private static Map<String, Method> methods = new HashMap<String, Method>();

    public static String getServicePackageName() {
        return F3SWebService.class.getPackage().getName();
    }

    /**
     * @param serviceName
     * @param service
     */
    public static void registerWebService(String serviceName, F3SWebService<?> service) {
        if (services.containsKey(serviceName)) {
            // FIXME 把下面异常改用WARNING
        	log.warn("发现有重复的Web服务");
        }
        services.put(serviceName, service);
    }

    /**
     * @param serviceName
     * @param processName
     * @param process
     */
    public static void registerWebServiceProcesser(String serviceName, String processName, Method process) {
        String key = serviceName + "#" + processName;
        if (methods.containsKey(key)) {
        	log.warn("发现有重复的Web服务");
        }
        methods.put(key, process);
    }

    /**
     * @param request
     * @param response
     * @throws Exception 
     */
    public static void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 参数准备
        String serviceName = request.getServletPath().replaceAll("/|\\.f3s", "");
        F3SWebService<?> service = services.get(serviceName);
        Method process = methods.get(serviceName + "#" + request.getParameter("PROCESS"));
        PrintWriter out = response.getWriter();
        if (process != null) {
            try {
                HibernateSessionFactory.getSession().beginTransaction();
                String responseText = (String)process.invoke(service, request, response);
                out.println(responseText);
                log.debug(responseText);
                if (HibernateSessionFactory.getSession().getTransaction().isActive()) {
                    HibernateSessionFactory.getSession().getTransaction().commit();
                }
            } catch (Exception ex) {
                if (HibernateSessionFactory.getSession().getTransaction().isActive()) {
                    HibernateSessionFactory.getSession().getTransaction().rollback();
                }
                // 记录日志
                LogInfo logInfo = F3ServerProxy.createLogInfo(ex.getCause().getClass().getName(), ex.getMessage(), ex.getCause().toString(), LogType.WEB_ERROR);
                HibernateSessionFactory.getSession().save(logInfo);
                HibernateSessionFactory.getSession().flush();
                throw ex;
            } finally {
        		HibernateSessionFactory.closeSession();
            }
        } else {
            String responseText = (String)service.serializeResponseStream(request, response);
            log.debug(responseText);
            out.println(responseText);
        }
    }

}
