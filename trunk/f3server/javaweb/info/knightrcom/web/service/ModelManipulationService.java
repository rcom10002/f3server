package info.knightrcom.web.service;

import info.knightrcom.util.ModelUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

    public static void main(String[] args) {
        new ModelManipulationService().STORE_PROPERTY(null, null);
    }
    
    public String STORE_PROPERTY(HttpServletRequest request, HttpServletResponse response) {
        String pathPrefix = ModelUtil.class.getPackage().getName();
        // String path = pathPrefix + ".model_defination.properties";
        String path = pathPrefix.replace(".", "/") + "/model_defination.properties";
        try {
            Properties properties = new Properties();
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(path));
            properties.store(new FileOutputStream("filename.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
