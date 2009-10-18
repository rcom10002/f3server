package info.knightrcom.web.service;

import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.GameRecordDAO;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

/**
 *
 */
public class SystemInfoService extends F3SWebServiceAdaptor<GameRecord> {

	@Override
	public Class<?>[] getAliasTypes() {
		return new Class[]{GameRecord.class};
	}

    @Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    /**
     * 获取游戏录像中的游戏记录信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String LOAD_GAME_RECORD(HttpServletRequest request, HttpServletResponse response) {
        GameRecord gameRecord = new GameRecordDAO().findById(request.getParameter("GAME_ID"));
        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
        info.setEntity(gameRecord);
        return toXML(info, getAliasTypes());
    }

    /**
     * 查询游戏运行时信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String RETRIEVE_GAME_RUNTIME_INFO(HttpServletRequest request, HttpServletResponse response) {
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
