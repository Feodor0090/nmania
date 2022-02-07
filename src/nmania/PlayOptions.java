package nmania;

public class PlayOptions {
	public boolean autoplay = false;
	/**
	 * <ul>
	 * <li> -1 - No Fail
	 * <li> 0 - default
	 * <li> 1 - Sudden Death
	 * </ul>
	 * Other values are clamped to this range (only sign matters).
	 */
	public int failMod = 0;
	/**
	 * <ul>
	 * <li> -1 - EZ
	 * <li> 0 - default
	 * <li> 1 - HR
	 * </ul>
	 * Other values are clamped to this range (only sign matters).
	 */
	public int daMod = 0;
}
