package nmania.formats;

import nmania.Beatmap;

public interface IRawBeatmap {
	Beatmap ToBeatmap() throws InvalidBeatmapTypeException;
	String GetImage();
	String GetTitle();
	String GetArtist();
	String GetMapper();
}
