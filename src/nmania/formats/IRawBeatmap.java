package nmania.formats;

import nmania.Beatmap;

/**
 * Object that represents file with not yet read beatmap.
 * 
 * @author Feodor0090
 *
 */
public interface IRawBeatmap {
	Beatmap ToBeatmap() throws InvalidBeatmapTypeException;
	String GetImage();
	String GetTitle();
	String GetArtist();
	String GetMapper();
}
