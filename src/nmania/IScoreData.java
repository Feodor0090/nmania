package nmania;

import java.util.Date;

public interface IScoreData {
	/**
	 * Gets player's name for this overrider.
	 * @return Name of the player.
	 */
	public String GetPlayerName();
	Date PlayedAt();
}
