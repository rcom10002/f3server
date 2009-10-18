package info.knightrcom.command.message.game;

import info.knightrcom.command.message.F3ServerMessage;
import info.knightrcom.model.game.red5.Red5Game;

public class AbstractGameMessage extends F3ServerMessage {

    public Red5Game game;

    /**
     * @return the game
     */
    public Red5Game getGame() {
        return game;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(Red5Game game) {
        this.game = game;
    }

}
