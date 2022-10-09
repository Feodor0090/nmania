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
	
	public ModsState mods = new ModsState();
	
	public boolean recordReplay;
}
