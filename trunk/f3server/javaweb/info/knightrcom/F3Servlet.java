package info.knightrcom;

import info.knightrcom.web.service.F3SWebService;
import info.knightrcom.web.service.F3SWebServiceProxy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/xml; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            F3SWebServiceProxy.doService(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        out.flush();
        out.close();
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
            Class[] classes = getClasses(F3SWebServiceProxy.getServicePackageName());
            for (Class thisClass : classes) {
                log.debug(thisClass.getName());
                if (Modifier.isAbstract(thisClass.getModifiers())) {
                    continue;
                }
                if (thisClass.getSimpleName().matches("^.*Service$")) {
                    F3SWebServiceProxy.registerWebService(thisClass.getSimpleName(), (F3SWebService)thisClass.newInstance());
                }
            }
            F3Server.main(null);
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
    private Class[] getClasses(String packageName) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = '/' + packageName.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
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