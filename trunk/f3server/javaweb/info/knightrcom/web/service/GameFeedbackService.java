package info.knightrcom.web.service;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import info.knightrcom.data.metadata.GameFeedback;

public class GameFeedbackService extends F3SWebService<GameFeedback>{

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class[]{GameFeedback.class};
    }

    @Override
    public String getNamedQuery() {
        return "";
    }

    @Override
    public String getNamedQueryForCount() {
        return "";
    }

    @Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {
    }

}
