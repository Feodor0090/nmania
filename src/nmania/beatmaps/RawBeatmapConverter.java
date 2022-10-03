package nmania.beatmaps;

import nmania.beatmaps.json.RawNmaniaBeatmap;
import nmania.beatmaps.osu.RawOsuBeatmap;

/**
 * Tiny library for creating {@link IRawBeatmap}s from unknown text.
 * 
 * @author Feodor0090
 *
 */
public class RawBeatmapConverter {
	public static IRawBeatmap FromText(String raw) throws InvalidBeatmapTypeException {
		if (raw.charAt(0) == '{') {
			return new RawNmaniaBeatmap(raw);
		} else if (raw.indexOf("osu file format") < 33) {
			return new RawOsuBeatmap(raw);
		} else
			throw new InvalidBeatmapTypeException("This is not osu! nor nmania beatmap. Is the file damaged?");
	}

	public static boolean CanReadFile(String fileName) {
		return fileName.endsWith(".osu") || fileName.endsWith(".nmbm");
	}
}
