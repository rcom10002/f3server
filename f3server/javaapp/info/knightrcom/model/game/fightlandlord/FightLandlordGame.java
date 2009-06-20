package info.knightrcom.model.game.fightlandlord;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.plaything.PokerColor;
import info.knightrcom.model.plaything.PokerValue;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Restrictions;

/**
 * Players: P1, P2, P3
 * 
 * 游戏的规则需要在客户端实现
 */
public class FightLandlordGame extends Game<FightLandlordGameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 3;
    
    /** 游戏积分倍数 */
    private static int multiple = 1;

    /**
     * 首发扑克：红桃3
     */
    public static final FightLandlordPoker START_POKER = new FightLandlordPoker(PokerColor.HEART, PokerValue.V3);

    private FightLandlordGameSetting setting; 

    public FightLandlordGame() {
    }

    /**
     * @return
     */
    @Override
    public void persistScore() {
    	log.debug("计算积分 START");
        // 取得玩家以及游戏等信息
        List<Player> players = this.getPlayers();
        FightLandlordGameSetting setting = this.getSetting();
        int gameMark = this.getGameMark();
        String gameFinalSettingPlayerNumber = setting.getPlayerNumber();
        String winnerNumbers = this.getWinnerNumbers().replace("~", "");
        String winnerNumber = winnerNumbers.substring(0, 1);
        String playerIds = "";
        Iterator<Player> itr = players.iterator();
        GameRecord gameRecord = new GameRecord();
        gameRecord.setCreateBy("SYSTEM");
        gameRecord.setCreateTime(new Date());
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        /**
         * 一幅牌计分：
         * 基础分：叫牌的底分，有“1分”“2分”“3分”；
         * 地主胜：地主得 2 × 叫牌的底分，其余两家各得： —叫牌的底分；
         * 地主败：地主得 —2 × 叫牌的底分，其余两家各得： 叫牌的底分；
         * 每出一个有效炸弹（或火箭），分数×2 ；
         * 地主把牌出完，其余两家一张牌都没出，分数×2 ；
         * 两家中有一家出完牌，而地主仅仅出过一手牌，分数×2 。 
         */
        // 在相应几分房间的分数 * 当前局叫的牌的底分
        if (FightLandlordGameSetting.NO_RUSH.equals(setting)) {
        	gameMark *= 1;
        } else if (FightLandlordGameSetting.ONE_RUSH.equals(setting)) {
        	gameMark *= 1;
        } else if (FightLandlordGameSetting.TWO_RUSH.equals(setting)) {
        	gameMark *= 2;
        } else if (FightLandlordGameSetting.THREE_RUSH.equals(setting)) {
        	gameMark *= 3;
        }
        if (gameFinalSettingPlayerNumber.equals(winnerNumber)) {
            // 地主胜
            while (itr.hasNext()) {
                Player player = itr.next();
                String playerId = player.getId();
                PlayerProfile playerProfile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
                playerIds += player.getCurrentNumber() + "~" + playerId + "~";
                if (gameFinalSettingPlayerNumber.equals(player.getCurrentNumber())) {
                    // 为地主玩家设置积分
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + gameMark * multiple * 2);
                } else {
                    // 为其他玩家设置积分
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() - gameMark * multiple);
                }
                HibernateSessionFactory.getSession().merge(playerProfile);
            }
        } else {
            // 地主败
            while (itr.hasNext()) {
                Player player = itr.next();
                String playerId = player.getId();
                PlayerProfile playerProfile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
                playerIds += player.getCurrentNumber() + "~" + playerId + "~";
                if (!gameFinalSettingPlayerNumber.equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + gameMark * multiple);
                } else {
                    // 为地主玩家设置积分
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() - gameMark * multiple * 2);
                }
                HibernateSessionFactory.getSession().merge(playerProfile);
            }
        }
        gameRecord.setPlayers(playerIds);
        HibernateSessionFactory.getSession().merge(gameRecord);

        log.debug("计算积分 END");
    }

    /**
     * @return the setting
     */
    @Override
    public FightLandlordGameSetting getSetting() {
        return setting;
    }

    /**
     * @param setting the setting to set
     */
    @Override
    public void setSetting(FightLandlordGameSetting setting) {
        this.setting = setting;
    }
    
    /**
	 * @return the multiple
	 */
	public int getMultiple() {
		return multiple;
	}

	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(int multiple) {
		FightLandlordGame.multiple = multiple;
	}
	
	/**
	 * 翻倍积分
	 */
	public void addMultiple() {
		FightLandlordGame.multiple *= 2;
	}
}
