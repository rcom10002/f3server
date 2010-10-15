package info.knightrcom.model.game.pushdownwin;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.plaything.MahjongWinningRule;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.hibernate.criterion.Restrictions;

/**
 * 推到胡
 */
public class PushdownWinGame extends Game<PushdownWinGameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 4;

    /** 番分 */
    private int pointMark;

    /** 番数 */
    private int points = 0;

    /** 获胜者牌序 */
    private String winnerMahjongSeq;

    @Override
	public void persistScore() {
        log.debug("计算积分 START");
        // 取得玩家以及游戏等信息
        List<Player> players = this.getPlayers();
        // boolean isFinalSettingPlayerWon = this.getSetting().getPlayerNumber().equals(winnerNumber);
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
        // gameRecord.setSysScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setScore(this.getGameMark());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏规则进行分数计算
        if (PushdownWinGameSetting.NARROW_VICTORY.equals(this.getSetting())) {
            // 点泡
            String winnerNumber = getWinnerNumbers().substring(0, 1);
            String loserNumber = getWinnerNumbers().substring(2, 3);
            persistNarrowWinScore(itr, gameRecord, winnerNumber, loserNumber);
        } else if (PushdownWinGameSetting.CLEAR_VICTORY.equals(this.getSetting())) {
            // 自摸
            persistClearWinScore(itr, gameRecord, getWinnerNumbers());
        } else {
            persistDummyScore(itr, gameRecord);
        }
        log.debug("计算积分 END");
	}

    /**
     * 计算点炮时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param winnerNumber
     * @param loserNumber
     */
    private void persistNarrowWinScore(Iterator<Player> itr, GameRecord gameRecord, String winnerNumber, String loserNumber) {
        // 点炮 => 取得底分与每番分
        final int basicScore = 20;
        final int pointScore = 10;
        double resultScore = 0;
        double points = 0;
        String playerIds = "";

        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";

            // 计算番数
            points = getPoints(gameRecord);
            // 计算得分
            resultScore = basicScore + pointScore * points;
            if (loserNumber.equals(player.getCurrentNumber())) {
                resultScore *= -1;
            } else if (winnerNumber.equals(player.getCurrentNumber())) {
                resultScore = 0;
            }

            // 在赢分玩家中直接从本局当前得分中扣除系统分
            double currentSystemScore = getCustomSystemScore(resultScore);
            if (resultScore > 0) {
                resultScore -= currentSystemScore;
            }

            // 设置本场比赛得分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(currentSystemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算自摸时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param winnerNumber
     * 
     */
    private void persistClearWinScore(Iterator<Player> itr, GameRecord gameRecord, String winnerNumber) {
        // 自摸 => 取得底分与每番分
        final int basicScore = 20;
        final int pointScore = 10;
        double resultScore = 0;
        double points = 0;
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            
            // 计算番数
            points = getPoints(gameRecord);
            // 计算得分
            resultScore = basicScore + pointScore * points;
            if (winnerNumber.equals(player.getCurrentNumber())) {
                resultScore *= 3;
            } else {
                resultScore *= -1;
            }

            // 在赢分玩家中直接从本局当前得分中扣除系统分
            double currentSystemScore = getCustomSystemScore(resultScore);
            if (resultScore > 0) {
                resultScore -= currentSystemScore;
            }

            // 设置本场比赛得分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(currentSystemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算流局时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     */
    public void persistDummyScore(Iterator<Player> itr, GameRecord gameRecord) {
        // 自摸 => 取得底分与每番分
        double resultScore = 0;
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";

            // 设置本场比赛得分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(0d); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

	/**
	 * 番数计算
	 */
	private int getPoints(GameRecord gameRecord) {
        // 记录格式说明：常规记录~最后一条常规记录~(逗号分隔的玩家手中牌;逗号分隔的玩家亮出牌;){4}
        // 4~W3~1~21;1~W1;1~W1~2;2~T7;2~T7~3;3~W2;3~W2~4;4~W5;4~W5~1;1~EAST;1~EAST~2;2~T8;2~T8~3;3~W1;3~W1~4;4~B5;4~B5~1;1~T1;1~T1~2;2~B7;2~B7~3;1~B7,B7,B7~2~2~B7~2;1~B9~2;2~W7;2~W7~3;3~T1;3~T1~4;4~W9;4~W9~1;1~NORTH;1~NORTH~2;2~WEST;2~WEST~3;3~T2;3~T2~4;1~T2~3~
        // B6,B6,T3,T4;W2,W3,W4,W8,W8,W8,B7,B7,B7;EAST,EAST,WEST,RED,WHITE,W3,W3,W8,B2,B6,B7,T5,T7;;SOUTH,NORTH,W2,B1,B1,B2,B4,B5,T1,T4,T6,T6,T7;;SOUTH,WEST,RED,WHITE,W6,W6,W6,B8,B8,T3,T4,T8,T9;;

	    // 字一色　由字牌的刻子（杠）、将组成的和牌。不计碰碰和。
	    // 碰碰和　由4副刻子（或杠）、将牌组成的和牌。
	    // 清一色　由一种花色的序数牌组成和各牌。不无字。
	    // 七对　由7个对子组成和牌。不计不求人、单钓。
		String result = gameRecord.getRecord().replaceAll(".*#(.*)", "$1").replaceAll(";$", "");
		if (points == 0) {
			points = 1;
		}
		if (MahjongWinningRule.十三幺(result)) {
			points *= 100;
			return points;
		}
		if (MahjongWinningRule.字一色(result)) {
			points *= 8;
			return points;
		} 
		if (MahjongWinningRule.碰碰和(result)) {
			points *= 4;
		}
		if (MahjongWinningRule.清一色(result)) {
			points *= 4;
		}
		if (MahjongWinningRule.七对(result)) {
			points *= 8;
		}
		return points;
		/*
	    if (true) {
	        return 0;
	    }
		if (points > 0) {
			return points;
		}
		// this.winnerMahjongSeq : split
		return (points = new Random().nextInt(10));
		*/
	}
	
	public int testGetPoints(GameRecord gameRecord) {
		return this.getPoints(gameRecord);
	}

	/**
	 * @return
	 */
	public int getPointMark() {
		return pointMark;
	}

	/**
	 * @param pointMark
	 */
	public void setPointMark(int pointMark) {
		this.pointMark = pointMark;
	}

	/**
	 * @return
	 */
	public String getWinnerMahjongSeq() {
		return winnerMahjongSeq;
	}

	/**
	 * @param winnerMahjongSeq
	 */
	public void setWinnerMahjongSeq(String winnerMahjongSeq) {
		this.winnerMahjongSeq = winnerMahjongSeq;
	}

	@Override
	public void persistDisconnectScore(Player disconnectedPlayer) {
	    // TODO test this function
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(PushdownWinGame.class.getSimpleName());
//        if (this.getSetting() != null) {
//            // this.setSetting(PushdownWinGameSetting.); TODO delete this line ?
//            gameRecord.setGameSetting((short)this.getSetting().ordinal());
//        } else {
//            // this.setSetting(Red5GameSetting.NO_RUSH);
//            gameRecord.setGameSetting((short)Red5GameSetting.NO_RUSH.ordinal());
//        }
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        gameRecord.setSystemScore(this.getGameMark());
        gameRecord.setStatus("DISCONNECTED");
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏设置，取得惩罚标准，默认扣一倍
        double deductStandard = 1;
//        if (this.getSetting().equals(Red5GameSetting.RUSH)) {
//            // 独牌
//            deductStandard = this.getLowLevelMark();
//        } else if (this.getSetting().equals(Red5GameSetting.DEADLY_RUSH)) {
//            // 天独
//            deductStandard = this.getMidLevelMark();
//        } else if (this.getSetting().equals(Red5GameSetting.EXTINCT_RUSH)) {
//            // 天外天
//            deductStandard = this.getHighLevelMark();
//        }
        synchronized(this.getPlayers()) {
            String playerIds = "";
            String playerId = null;
            double resultScore = 0;
            // 掉线玩家需要为其他玩家补偿积分，补偿标准为基本分 × 当前设置等级
            double deductedMark = this.getGameMark() * deductStandard;
            for (Player player : this.getPlayers()) {
                playerId = player.getId();
                PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
                playerIds += player.getCurrentNumber() + "~" + player.getId() + "~";
                if (player.getId().equals(disconnectedPlayer.getId())) {
                    // 掉线玩家
                    resultScore = -1 * (deductedMark * 3);
                } else {
                    // 非掉线玩家
                    resultScore = deductedMark;
                }
    
                // 在赢分玩家中直接从本局当前得分中扣除系统分
                double currentSystemScore = getCustomSystemScore(resultScore);
                if (resultScore > 0) {
                    resultScore -= currentSystemScore;
                }

                playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
                // 保存玩家得分信息
                PlayerScore playerScore = new PlayerScore();
                playerScore.setScoreId(UUID.randomUUID().toString());
                playerScore.setProfileId(playerProfile.getProfileId());
                playerScore.setGameId(gameRecord.getGameId());
                playerScore.setUserId(playerProfile.getUserId());
                playerScore.setCurrentNumber(player.getCurrentNumber());
                playerScore.setCurScore(resultScore); // 玩家当前得分
                playerScore.setSysScore(currentSystemScore); // 系统当前得分
                playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
                playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
                HibernateSessionFactory.getSession().merge(playerProfile);
                HibernateSessionFactory.getSession().merge(playerScore);
            }
            gameRecord.setPlayers(playerIds);
        }
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
