package info.knightrcom.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

public class ModelManipulationService extends F3SWebService<Object> {

    @Override
    public Class<?>[] getAliasTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNamedQuery() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNamedQueryForCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultTransformer getResultTransformer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {
        // TODO Auto-generated method stub
        
    }

    /**
     * 读取配置信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String READ_PROPERTY(HttpServletRequest request, HttpServletResponse response) {
        // ModelUtil.readProperties(properties);
        return null;
    }
    
    /**
     * 保存配置信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String SAVE_PROPERTY(HttpServletRequest request, HttpServletResponse response) {
        // ModelUtil.saveProperties(properties);
        return null;
    }

}
