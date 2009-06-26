package info.knightrcom.model.game.pushdownwin;

import info.knightrcom.model.game.Game;
import info.knightrcom.model.game.red5.Red5GameSetting;

/**
 * 推到胡
 */
public class PushdownWinGame extends Game<Red5GameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 4;
	
	@Override
	public void persistScore() {
	}

}
