package info.knightrcom.model.game.pushdownwin;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Restrictions;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;

/**
 * 推到胡
 */
public class PushdownWinGame extends Game<PushdownWinGameSetting> {

    /** 番分 */
    private int multipleMark;

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 4;

	@Override
	public void persistScore() {
        log.debug("计算积分 START");
        // 取得玩家以及游戏等信息
        List<Player> players = this.getPlayers();
        String winnerNumber = getWinnerNumbers().substring(0, 1);
        boolean isFinalSettingPlayerWon = this.getSetting().getPlayerNumber().equals(winnerNumber);
        Iterator<Player> itr = players.iterator();
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(PushdownWinGame.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        // TODO DROP THIS LINE => gameRecord.setScore(score);
        // THE FOLLOWING LINES WERE SET IN THE LAST LINE OF EACH PRIVATE METHOD 
        // gameRecord.setPlayers();
        // gameRecord.setSystemScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
        // 根据当前游戏规则进行分数计算
        if (PushdownWinGameSetting.NARROW_VICTORY.equals(this.getSetting())) {
            // 自摸
            persistNarrowWinScore(itr, gameRecord);
        } else {
            // 点泡
            persistClearWinScore(itr, gameRecord, false);
        }
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        log.debug("计算积分 END");
	}

    /**
     * 计算非独牌时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     */
    private void persistNarrowWinScore(Iterator<Player> itr, GameRecord gameRecord) {
        // 不独
        int gameMark = this.getGameMark();
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            int playerPlace = getWinnerNumbers().replace("~", "").indexOf(player.getCurrentNumber()) + 1;
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            // 假设此局的大小为“X”，如果没有玩家叫牌，那么
            // 第一个出完牌的玩家赢2X，第二位出完牌的玩家赢X，第三为出完牌的玩家输X，最后一 位玩家输2X
            int resultScore = 0;
            int systemScore = 0;
            switch (playerPlace) {
            case 1:
                // 第一名
                resultScore = 2 * gameMark;
                break;
            case 2:
                // 第二名
                resultScore = 1 * gameMark;
                break;
            case 3:
                // 第三名
                resultScore = -1 * gameMark;
                break;
            case 4:
                // 第四名
                resultScore = -2 * gameMark;
                break;
            default:
                break;
            }
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(playerProfile.getNumber());
            playerScore.setScore(resultScore);
            playerScore.setSystemScore(systemScore);
            playerProfile.setCurrentScore(resultScore + playerProfile.getCurrentScore().intValue());
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(resultScore);
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(systemScore);
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算独牌时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param isFinalSettingPlayerWon
     */
    private void persistClearWinScore(Iterator<Player> itr, GameRecord gameRecord, boolean isFinalSettingPlayerWon) {
        // 独牌
        int gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“独牌”的玩家胜，那么叫牌者赢到3X+3X+3X，反之叫牌者输9X
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            // 基本分
            
            // 每番分
            int resultScore = 0;
            int systemScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 3 * 3 * gameMark;
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * 3 * gameMark;
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * 3 * gameMark;
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 3 * 3 * gameMark;
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            }
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(playerProfile.getNumber());
            playerScore.setScore(resultScore);
            playerScore.setSystemScore(systemScore);
            playerProfile.setCurrentScore(resultScore + playerProfile.getCurrentScore().intValue());
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(resultScore);
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(systemScore);
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * @return the multipleMark
     */
    public int getMultipleMark() {
        return multipleMark;
    }

    /**
     * @param multipleMark the multipleMark to set
     */
    public void setMultipleMark(int multipleMark) {
        this.multipleMark = multipleMark;
    }

//	　　88番
//	　　1．大四喜　由4副风刻（杠）组成的和牌。不计圈风刻、门风刻、三风刻、碰碰和。
//	　　2．大三元　和牌中，有中发白3副刻子。不计箭刻。
//	　　3．绿一色　由23468条及发字中的任何牌组成的顺子、刻五、将的和牌。不计混一色。如无“发”字组成的各牌，可计清一色。
//	　　4．九莲宝灯　由一种花色序数牌子按1112345678999组成的特定牌型，见同花色任何1张序数牌即成和牌。不计清一色。
//	　　5．四杠　4个杠。
//	　　6．连七对　由一种花色序数牌组成序数相连的7个对子的和牌。不计清一色、不求人、单钓。
//	　　7．十三幺　由3种序数牌的一、九牌，7种字牌及其中一对作将组成的和牌。不计五门齐、不求人、单钓。
//	　　64番
//	　　8．清幺九　由序数牌一、九刻子组成的和牌。不计碰碰和、同刻、元字。
//	　　9．小四喜　和牌时有风牌的3副刻子及将牌。不计三风刻。
//	　　10．小三元　和牌时有箭牌的两副刻子及将牌。不计箭刻。
//	　　11．字一色　由字牌的刻子（杠）、将组成的和牌。不计碰碰和。
//	　　12．四暗刻　4个暗刻（暗杠）。不计门前清、碰碰和。
//	　　13．一色双龙会　一种花色的两个老少副，5为将牌。不计平各、七对、清一色。
//	　　48番
//	　　14．一色四同顺　一种花色4副序数相同的顺子，不计一色三节高、一般高、四归一。
//	　　15．一色四节高　一种花色4副依次递增一位数的刻子不计一色三同顺、碰碰和。
//	　　32番
//	　　16．一色四步高一种花色4副依次递增一位数或依次递增二位数的顺子。
//	　　17．三杠3个杠。
//	　　18．混幺九由字牌和序数牌一、九的刻了用将牌组成的各牌。不计碰碰和。
//	　　24番
//	　　19．七对　由7个对子组成和牌。不计不求人、单钓。
//	　　20．七星不靠　必须有7个单张的东西南北中发白，加上3种花色，数位按147、258、369中的7张序数牌组成没有将牌的和牌。不计五门齐、不求人、单钓。
//	　　21．全双刻　由2、4、6、8序数牌的刻了、将牌组成的和牌。不计碰碰和、断幺。
//	　　22．清一色　由一种花色的序数牌组成和各牌。不无字。
//	　　23．一色三同顺　和牌时有一种花色3副序数相同的顺了。不计一色三节高。
//	　　24．一色三节高　和牌时有一种花色3副依次递增一位数字的刻了。不计一色三同顺。
//	　　25．全大　由序数牌789组成的顺了、刻子（杠）、将牌的和牌。不计无字。
//	　　26．全中　由序数牌456组成的顺子、刻子（杠）、将牌的和牌。不计断幺。
//	　　27．全小　由序数牌123组成的顺子、刻子（杠）将牌的的和牌。不计无字。
//	　　16番
//	　　28．清龙　和牌时，有一种花色1-9相连接的序数牌。
//	　　29．三色双龙会　2种花色2个老少副、另一种花色5作将的和牌。不计喜相逢、老少副、无字、平和。
//	　　30．一色三步高　和牌时，有一种花色3副依次递增一位或依次递增二位数字的顺子。
//	　　31．全带五　每副牌及将牌必须有5的序数牌。不计断幺。
//	　　32．三同刻　3个序数相同的刻子（杠）。
//	　　33．三暗刻　3个暗刻12番。
//	　　34．全不靠　由单张3种花色147、258、369不能错位的序数牌及东南西北中发白中的任何14张牌组成的和牌。不计五门齐、不求人、单钓。
//	　　35．组合龙　3种花色的147、258、369不能错位的序数牌。
//	　　36．大于五　由序数牌6-9的顺子、刻子、将牌组成的和牌。不计无字。
//	　　37．小于五　由序数牌1-4的顺子、刻子、将牌组成的和牌。不计无字。
//	　　38．三风刻　3个风刻。
//	　　8番
//	　　39．花龙　3种花色的3副顺子连接成1-9的序数牌。
//	　　40．推不倒　由牌面图形没有上下区别的牌组成的和牌，包括1234589饼、245689条、白板。不计缺一门。
//	　　41．三色三同顺　和牌时，有3种花色3副序数相同的顺子。
//	　　42．三色三节高　和牌时，有3种花色3副依次递增一位数的刻子。
//	　　43．无番　和和牌后，数不出任何番种分（花牌不计算在内）。
//	　　44．妙手回春　自摸牌墙上最后一张牌和牌。不计自摸。
//	　　45．海底捞月　和打出的最后一张牌。
//	　　46．杠上开花　开杠抓进的牌成和牌（不包括补花）不计自摸。
//	　　47．抢杠和　和别人自抓开明杠的牌。不计和绝张。
//	　　6番
//	　　48．碰碰和　由4副刻子（或杠）、将牌组成的和牌。
//	　　49．混一色　由一种花色序数牌及字牌组成的和牌。
//	　　50三色三步高　3种花色3副依次递增一位序数的顺子。
//	　　51．五门齐　和牌时3种序数牌、风、箭牌齐全。
//	　　52．全求人　全靠吃牌、碰牌、单钓别人批出的牌和牌。不计单钓。
//	　　53．双暗杠　2个暗杠。
//	　　54．双箭刻　2副箭刻（或杠）。
//	　　4番
//	　　55．全带幺　和牌时，每副牌、将牌都有幺牌。
//	　　56．不求人　4副牌及将中没有吃牌、碰牌（包括明杠），自摸和牌。
//	　　57．双明杠　2个明杠。
//	　　58．和绝张　和牌池、桌面已亮明的3张牌所剩的第4张牌（抢杠和不计和绝张）。
//	　　2番
//	　　59．箭刻　由中、发、白3张相同的牌组成的刻子。
//	　　60．圈风刻　与圈风相同的风刻。
//	　　61．门风刻　与本门风相同的风刻。
//	　　62．门前清　没有吃、碰、明杠，和别人打出的牌。
//	　　63．平和　由4副顺子及序数牌作将组成的和牌，边、坎、钓不影响平和。
//	　　64．四归一　和牌中，有4张相同的牌归于一家的顺、刻子、对、将牌中（不包括杠牌）。
//	　　65．双同刻　2副序数相同的刻子。
//	　　66．双暗刻　2个暗刻。
//	　　67．暗杠　自抓4张相同的牌开杠。
//	　　68．断幺　和牌中没有一、九及字牌。
//	　　1番
//	　　69．一般高　由一种花色2副相同的顺子组成的牌。
//	　　70．喜相逢　2种花色2副序数相同的顺子。
//	　　71．连六　一种花色6张相连接的序数牌。
//	　　72．老少副　一种花色牌的123、789两副顺子。
//	　　73．幺九刻　3张相同的一、九序数牌及字牌组成的刻子（或杠）。
//	　　74．明杠　自己有暗刻，碰别人打出的一张相同的牌开杠：或自己抓进一张与碰的明刻相同的牌开杠。
//	　　75．缺一门　和牌中缺少一种花色序数牌。
//	　　76．无字　和牌中没有风、箭牌。
//	　　77．边张　单和123的3及789的7或1233和3、77879和7都为张。手中有12345和3，56789和6不算边张。
//	　　78．坎张　和2张牌之间的牌。4556和5也为坎张，手中有45567和6不算坎张。
//	　　79．单钓将　钓单张牌作将成和。
//	　　80．自摸　自己抓进牌成和牌。
//	　　81．花牌　即春夏秋冬，梅兰竹菊，每花计一分。不计在起和分内，和牌后才能计分。花牌补花成和计自摸分，不计杠上开花。
}
