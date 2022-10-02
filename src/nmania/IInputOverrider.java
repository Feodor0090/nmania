package nmania;

/**
 * Object that manipulates player's input.
 * 
 * @author Feodor0090
 *
 */
public interface IInputOverrider {
	/**
	 * Called when starting playback.
	 */
	public void Reset();
	/**
	 * Changes player's input state.
	 * @param player Player to work with.
	 * @param time Time at which player now is.
	 * @return Time to seek player to. Normally should be the same as second argument.
	 */
	public int UpdatePlayer(Player player, int time);
}
