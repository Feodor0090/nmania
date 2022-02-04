package nmania.formats;

public class RawBeatmapConverter {
	public static IRawBeatmap FromText(String raw) throws InvalidBeatmapTypeException {
		IRawBeatmap rb;
		if (raw.startsWith("osu file format")) {
			rb = new RawOsuBeatmap(raw);
		} else if (raw.charAt(0) == '{') {
			rb = new RawNmaniaBeatmap(raw);
		} else
			throw new InvalidBeatmapTypeException("This is not osu! nor nmania beatmap. Is the file damaged?");
		return rb;
	}
}
