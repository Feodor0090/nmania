package nmania;

import javax.microedition.lcdui.Displayable;

import nmania.beatmaps.IRawBeatmap;
import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.ui.ng.IDisplay;

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

	public IInputOverrider input;

	public boolean recordReplay;

	/**
	 * If true, columns won't be fit into screen.
	 */
	public boolean forbidAftoFit;

	/**
	 * If false, previous {@link IDisplay} or {@link Displayable} will not be kept
	 * when swithing displayables.
	 */
	public boolean keepBackScreen;

	public final IRawBeatmap ReadBeatmap() throws InvalidBeatmapTypeException {
		return set.ReadBeatmap(mapFileName);
	}

	public final String ReadBeatmapMd5() {
		return BeatmapManager.getMD5FromFs(set.wdPath + set.folderName + mapFileName);
	}
}
