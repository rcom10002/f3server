package info.knightrcom.web;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.web.service.F3SWebService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.AccessController;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.security.action.GetPropertyAction;

/**
 * 
 */
public class F3Servlet extends HttpServlet {

    private Log log = LogFactory.getLog(F3Servlet.class);

    /**
     * 
     */
    private static final long serialVersionUID = 4202843891802661706L;

    /**
     * Constructor of the object.
     */
    public F3Servlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 输出流编码设置
    	PrintWriter out = null;
        try {
            response.setCharacterEncoding("utf-8");
            request.setCharacterEncoding("utf-8");
            response.setContentType("text/html; charset=utf-8");
            if (request.getRequestURI().matches("^/.*/[a-z]*\\.f3s$")) {
            	// html文件请求
                // out = response.getWriter();
                String deletePattern = "^/.*/|.{4}$";
                String forwardURI = request.getRequestURI().replaceAll(deletePattern, "");
                String realForwardURI = getServletConfig().getInitParameter(forwardURI);
                request.getRequestDispatcher("/WEB-INF/" + realForwardURI).forward(request, response);
            } else {
            	// f3s服务
                out = response.getWriter();
                F3SWebServiceHandler.doService(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
        	if (out != null) {
        		out.close();
        	}
        }
    }

    /**
     * @param relativeUrl
     * @return
     * @throws Exception
     */
    public String getStaticText(String relativeUrl) throws Exception {
        URL staticTextConnection = new URL(relativeUrl);
        URLConnection yahooConnection = staticTextConnection.openConnection();
        BufferedReader reader =  new BufferedReader(new InputStreamReader(yahooConnection.getInputStream()));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
        	builder.append(inputLine);
        }
        reader.close();
        return builder.toString();
    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        try {
            Class[] classes = getClasses(F3SWebServiceHandler.getServicePackageName());
            for (Class thisClass : classes) {
                log.debug(thisClass.getName());
                if (Modifier.isAbstract(thisClass.getModifiers())) {
                    continue;
                }
                if (thisClass.getSimpleName().matches("^.*Service$")) {
                    F3SWebServiceHandler.registerWebService(thisClass.getSimpleName(), (F3SWebService)thisClass.newInstance());
                }
            }
            if (!F3ServerProxy.isServerRunning()) {
            	F3ServerProxy.startServer();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            // TODO 
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
