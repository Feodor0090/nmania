package nmania.formats;

import nmania.Beatmap;

public interface IRawBeatmap {
	Beatmap ToBeatmap() throws InvalidBeatmapTypeException;
}
