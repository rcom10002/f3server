package info.knightrcom.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

public class GameConfigureService extends F3SWebService<Object> {

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

    public String UPDATE_RED5_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String UPDATE_FIGHT_LANDLORD_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String UPDATE_PUSHDOWN_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String UPDATE_QIONG_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

}
