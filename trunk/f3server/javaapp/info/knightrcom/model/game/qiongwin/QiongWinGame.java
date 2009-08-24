package info.knightrcom.model.game.qiongwin;

import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;

/**
 * 穷胡
 */
public class QiongWinGame extends Game<QiongWinGameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 4;
	
	@Override
	public void persistScore() {
	}

	@Override
	public void persistDisconnectScore(Player disconnectedPlayer) {
		// TODO Auto-generated method stub
		
	}

}
