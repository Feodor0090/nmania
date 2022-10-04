package nmania;

/**
 * This object contains all the data player need to be able to play something.
 * @author Feodor0090
 *
 */
public class PlayerBootstrapData {
	/**
	 * Beatmapset that we are going to play.
	 */
	public BeatmapSet set;
	/**
	 * Filename of the beatmap.
	 */
	public String mapFileName;
	/**
	 * <ul>
	 * <li>-1 - No Fail
	 * <li>0 - default
	 * <li>1 - Sudden Death
	 * </ul>
	 * Other values are clamped to this range (only sign matters).
	 */
	public int failMod = 0;
	/**
	 * <ul>
	 * <li>-1 - EZ
	 * <li>0 - default
	 * <li>1 - HR
	 * </ul>
	 * Other values are clamped to this range (only sign matters).
	 */
	public int daMod = 0;
}
