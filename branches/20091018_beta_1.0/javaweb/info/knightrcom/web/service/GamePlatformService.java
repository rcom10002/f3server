package info.knightrcom.web.service;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

public class GamePlatformService extends F3SWebService<Object> {

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

}
