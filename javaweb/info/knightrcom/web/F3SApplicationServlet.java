package info.knightrcom.web;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.web.service.F3SWebService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.security.AccessController;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.action.GetPropertyAction;

/**
 * Application Servlet
 */
public class F3SApplicationServlet extends F3SServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 4202843891802661706L;

    /**
     * Constructor of the object.
     */
    public F3SApplicationServlet() {
        super();
    }

    /* (non-Javadoc)
     * @see info.knightrcom.web.F3SServlet#doProcess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
//            response.getWriter();
//            if (request.getRequestURI().matches("^/.*/[a-z]*\\.f3s$")) {
//            	// html文件请求
//                // out = response.getWriter();
//                String deletePattern = "^/.*/|.{4}$";
//                String forwardURI = request.getRequestURI().replaceAll(deletePattern, "");
//                String realForwardURI = getServletConfig().getInitParameter(forwardURI);
//                request.getRequestDispatcher("/WEB-INF/" + realForwardURI).forward(request, response);
//            } else {
            	// f3s服务
                F3SWebServiceHandler.doService(request, response);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see info.knightrcom.web.F3SServlet#init()
     */
    @SuppressWarnings("unchecked")
    @Override
	public void init() throws ServletException {
        LogInfo log = null;
        try {
        	super.init();
            Class[] classes = getClasses(F3SWebServiceHandler.getServicePackageName());
            for (Class thisClass : classes) {
                if (Modifier.isAbstract(thisClass.getModifiers())) {
                    continue;
                }
                if (thisClass.getSimpleName().matches("^.*Service$")) {
                    F3SWebServiceHandler.registerWebService(thisClass.getSimpleName(), (F3SWebService)thisClass.newInstance());
                }
            }
	        // Hibernate初始化
	        HibernateSessionFactory.init();
	        // 应用服务器初始化
        	F3ServerProxy.startServer();
        	// 记录日志
        	log = F3ServerProxy.createLogInfo("Web Server", "Web server started successfully!", null, LogType.SYSTEM_LOG);
        } catch (Exception e) {
            // 记录日志
            log = F3ServerProxy.createLogInfo("Web Server", "Web server started successfully!", null, LogType.SYSTEM_LOG);
            throw new RuntimeException(e);
        } finally {
            if (log != null) {
                HibernateSessionFactory.getSession().save(log);
                HibernateSessionFactory.closeSession();
            }
        }
    }

    /* (non-Javadoc)
     * @see info.knightrcom.web.F3SServlet#destroy()
     */
    @Override
    public void destroy() {
    	super.destroy();
        if (F3ServerProxy.isServerRunning()) {
        	// 停止应用服务器
        	F3ServerProxy.stopServer();
        	// 停止Hibernate服务
        	HibernateSessionFactory.getSessionFactory().close();
        }
        // 记录日志
        LogInfo log = F3ServerProxy.createLogInfo("Web Server", "Web server shutdown successfully!", null, LogType.SYSTEM_LOG);
        HibernateSessionFactory.getSession().save(log);
        HibernateSessionFactory.closeSession();
    }

    /**
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private Class[] getClasses(String packageName) throws Exception {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            // create the package path 
            String path = '/' + packageName.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(URLDecoder.decode(
            		resource.getPath().replaceFirst("^/", ""), 
            		(String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"))));
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package");
        }
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(packageName + " does not appear to be a valid package");
        }
        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }
}
