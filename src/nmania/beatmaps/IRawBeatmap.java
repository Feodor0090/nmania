package nmania.beatmaps;

import nmania.Beatmap;

/**
 * Object that represents file with not yet read beatmap.
 * 
 * @author Feodor0090
 *
 */
public interface IRawBeatmap {
	public final static String VSRG = "vsrg";

	Beatmap ToBeatmap() throws InvalidBeatmapTypeException;

	String GetImage();

	String GetAudio();

	String GetTitle();

	String GetArtist();

	String GetMapper();

	/**
	 * 
	 * @return Mode to play this beatmap on. For now, always must be {@link #VSRG}.
	 */
	String GetMode();

	/**
	 * Obtains timing data for this map. Can be null if not supported.
	 * 
	 * @return Two arrays. First array is timings, point-length-point-length, second
	 *         is kiais, begin-end-begin-end.
	 */
	float[][] GetTimingData();
}
