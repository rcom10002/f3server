package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PlayerProfileService extends F3SWebService<PlayerProfile> {

    @Override
    public String getNamedQuery() {
        return "PLAYER_PROFILE";
    }

    @Override
    public String getNamedQueryForCount() {
        return "PLAYER_PROFILE_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {        
    }

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class<?>[] {PlayerProfile.class};
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String RETRIEVE_PLAYER_RLS_PATH(HttpServletRequest request, HttpServletResponse response) {
        Query query = null;
        // 设置玩家关系路径
        if (StringHelper.isEmpty(request.getParameter("CURRENT_RLS_PATH"))) {
            // 管理员
            query = HibernateSessionFactory.getSession().getNamedQuery("PLAYER_PROFILE_ALL_RLS_PATH");
        } else {
            // 组用户
            query = HibernateSessionFactory.getSession().getNamedQuery("PLAYER_PROFILE_CURRENT_RLS_PATH");
            query.setString(0, request.getParameter("CURRENT_RLS_PATH"));
        }
        query.setResultTransformer(this.getResultTransformer());
        EntityInfo<PlayerProfile> info = createEntityInfo(new PlayerProfile(), F3SWebServiceResult.SUCCESS);
        info.setTag(query.list().toArray());
        return toXML(info, getAliasTypes());
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String RETRIEVE_PLAYER_ROLE(HttpServletRequest request, HttpServletResponse response) {
    	List<GlobalConfig> roleList = new GlobalConfigDAO().findByType(GameConfigureConstant.PLAYER_ROLE);
    	String roles = "";
    	Iterator<GlobalConfig> itr = roleList.iterator();
    	while (itr.hasNext()) {
    		GlobalConfig config = itr.next();
    		if ("GroupUser".equals(request.getParameter("CURRENT_ROLE"))) {
    			if (config.getName().indexOf("User") > -1) {
    				roles += config.getName() + "~" + config.getValue() + ";";
    			}
				continue;
    		}
    		roles += config.getName() + "~" + config.getValue() + ";"; 
    	}
    	roles = roles.substring(0, roles.length() - 1);
        EntityInfo<PlayerProfile> info = createEntityInfo(new PlayerProfile(), F3SWebServiceResult.SUCCESS);
        info.setTag(roles.split(";"));
        return toXML(info, getAliasTypes());
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String CREATE_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setProfileId(UUID.randomUUID().toString());
        playerProfile.setUserId(request.getParameter("USER_ID"));
        playerProfile.setPassword(request.getParameter("PASSWORD"));
        playerProfile.setRlsPath(request.getParameter("RLS_PATH"));
        playerProfile.setCurrentScore(Integer.valueOf(request.getParameter("CURRENT_SCORE")));
        playerProfile.setInitLimit(Integer.valueOf(request.getParameter("INIT_LIMIT")));
        playerProfile.setRole(request.getParameter("ROLE"));
        playerProfile.setStatus(request.getParameter("STATUS")); // 0:禁用、1:启用
        HibernateSessionFactory.getSession().save(playerProfile);
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setEntity(playerProfile);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String READ_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        final PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("PROFILE_ID"));
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setEntity(playerProfile);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String UPDATE_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("PROFILE_ID"));
        playerProfile.setUserId(request.getParameter("USER_ID"));
        playerProfile.setPassword(request.getParameter("PASSWORD"));
        // playerProfile.setRlsPath(request.getParameter("RLS_PATH"));
        // playerProfile.setCurrentScore(Integer.valueOf(request.getParameter("CURRENT_SCORE")));
        // playerProfile.setInitLimit(Integer.valueOf(request.getParameter("INIT_LIMIT")));
        playerProfile.setRole(request.getParameter("ROLE"));
        playerProfile.setStatus(request.getParameter("STATUS")); // 0:禁用、1:启用
        playerProfile.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().update(playerProfile);
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String DELETE_PLAYER_PROFILE(HttpServletRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("PROFILE_ID"));
        new PlayerProfileDAO().delete(playerProfile);
        EntityInfo<PlayerProfile> info = new EntityInfo<PlayerProfile>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {PlayerProfile.class});
    }

    /**
	 * @param request
	 * @param response
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String SHOW_RLS_PATH(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// http://www.liquid-technologies.com/XmlDataBinding/Xml-Schema-To-Java.aspx
		// http://chaithanyat.rediffiland.com/blogs/2008/07/23/Generating-XML-file-using-JAVA.html
		Query query = HibernateSessionFactory.getSession().getNamedQuery("RLS_PATH_DIAGRAM");
		List<Object[]> resultList = (List<Object[]>)query.list();
		Map<String, Element> parents = new HashMap<String, Element>();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElement("root");
		document.appendChild(rootElement);

		for (Object[] eachRow : resultList) {
			// key is the user_id and value is the "root!" + rls_path
			String pathKeyStr = eachRow[0].toString();
			String pathValStr = eachRow[1].toString();
			String pathRole = eachRow[2].toString();
			String parentPathValStr = pathValStr.replaceFirst("![^!]+$", "");
			Element path = document.createElement("rlspath");
			path.setAttribute("key", pathKeyStr);
			path.setAttribute("val", pathValStr);
			path.setAttribute("role", pathRole);
			path.setAttribute("parent", parentPathValStr);
			// eachRow为数组，对应的列分别为PATH_KEY, PATH_VALUE, ROLE
			if ("GroupUser".equals(pathRole)) {
				// 将当前元素添加到父节点集合中
				parents.put(pathValStr, path);
			}
			// 将当前节点与父节点关联起来
			if (parentPathValStr != null) {
				parents.get(parentPathValStr).appendChild(path);
			} else {
				rootElement.appendChild(path);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(bytes);
		transformer.transform(source, result);
		System.out.println(bytes.toString());
		return null;
	}

	public static void main(String[] args) throws Exception {
		new PlayerProfileService().SHOW_RLS_PATH(null, null);
	}
}
