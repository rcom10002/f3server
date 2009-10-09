package info.knightrcom.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
abstract class F3SServlet extends HttpServlet {

	/**
     * 
     */
    private static final long serialVersionUID = -4114083020111046180L;

    /**
	 * 
	 */
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * 
	 */
	private static Map<String, F3SServlet> servlets = new HashMap<String, F3SServlet>();

	/**
     * Initialization of the servlet. <br>
     */
    public void init() throws ServletException {
        super.init();
        servlets.put(this.getClass().getName(), this);
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
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
        _doProcess(request, response);
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
        _doProcess(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void _doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 执行处理
        doProcess(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected abstract void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    /**
     * @param type
     * @return
     */
    protected F3SServlet getF3SServlet(Class<?> type) {
    	return servlets.get(type.getName());
    }
}
