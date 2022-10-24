package nmania;

import nmania.beatmaps.IRawBeatmap;
import nmania.beatmaps.InvalidBeatmapTypeException;

/**
 * This object contains all the data player need to be able to play something.
 * 
 * @author Feodor0090
 *
 */
public final class PlayerBootstrapData {
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

	public final IRawBeatmap ReadBeatmap() throws InvalidBeatmapTypeException {
		return set.ReadBeatmap(mapFileName);
	}

	public final String ReadBeatmapMd5() {
		return BeatmapManager.getMD5FromFs(set.wdPath + set.folderName + mapFileName);
	}
}
