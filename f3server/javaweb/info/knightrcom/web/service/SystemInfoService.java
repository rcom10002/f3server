package info.knightrcom.web.service;

import java.util.UUID;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameFeedback;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.GameRecordDAO;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

public class SystemInfoService extends F3SWebService<GameRecord> {

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

    public String SUBMIT_CHEAT(HttpServletRequest request, HttpServletResponse response) {
        String gameId = request.getParameter("GAME_ID");
        String cheatDesc = request.getParameter("CHEAT_DESC");
        GameFeedback gameFeedback = new GameFeedback();
        gameFeedback.setFeedbackId(UUID.randomUUID().toString());
        gameFeedback.setGameId(gameId);
        gameFeedback.setCheatDesc(cheatDesc);
        HibernateSessionFactory.getSession().save(gameFeedback);
        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
        return toXML(info, GameRecord.class);
    }

    public String GAME_INFO(HttpServletRequest request, HttpServletResponse response) {
        GameRecord gameRecord = new GameRecordDAO().findById(request.getParameter("GAME_ID"));
        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
        info.setEntity(gameRecord);
        return toXML(info, GameRecord.class);
    }

    public String GAME_RUNTIME_INFO(HttpServletRequest request, HttpServletResponse response) {
        // ROOM_NAME, PLAYING_NUM, WAITING_NUM, WONDER_NUM, TOTAL_NUM
        // 10 3 HU
//        ModelUtil.
//        Map<String, String> gameRunningInfo = new HashMap<String, String>();
//        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
//        info.setEntity(entity)
//        return toXML();
        return null;
    }
}
