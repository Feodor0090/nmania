package nmania.formats;

public class RawBeatmapConverter {
	public static IRawBeatmap FromText(String raw) throws InvalidBeatmapTypeException {
		IRawBeatmap rb;
		if (raw.charAt(0) == '{') {
			rb = new RawNmaniaBeatmap(raw);
		} else if (raw.indexOf("osu file format") < 33) {
			rb = new RawOsuBeatmap(raw);
		} else
			throw new InvalidBeatmapTypeException("This is not osu! nor nmania beatmap. Is the file damaged?");
		return rb;
	}

	public static boolean CanReadFile(String fileName) {
		return fileName.endsWith(".osu") || fileName.endsWith(".nmbm");
	}
}
