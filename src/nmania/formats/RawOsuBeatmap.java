package nmania.formats;

import java.util.Vector;

import nmania.Beatmap;
import nmania.Beatmap.ManiaNote;
import nmania.Beatmap.TimingPoint;
import symnovel.SNUtils;

public final class RawOsuBeatmap implements IRawBeatmap {
	public final String raw;

	public RawOsuBeatmap(String raw) {
		super();
		this.raw = raw;
	}

	public final String getValue(String key) {
		key = "\n" + key + ":";
		int b = raw.indexOf(key) + key.length();
		int e = Math.min(raw.indexOf('\n', b), raw.indexOf('\r', b));
		String data = raw.substring(b, e);
		return data.trim();
	}

	public final String GetImage() {
		int imageI = raw.indexOf("\n0,", raw.indexOf("[Events]")) + 3;

		String image = raw.substring(raw.indexOf(',', imageI) + 1, raw.indexOf('\n', imageI));
		int ci = image.indexOf('\r');
		if (ci != -1)
			image = image.substring(0, ci);
		ci = image.indexOf(',');
		if (ci != -1)
			image = image.substring(0, ci);
		if (image.charAt(0) == '\"')
			image = image.substring(1, image.length() - 1);
		return image;
	}

	public final String[] hitObjects() {
		int start = raw.indexOf("[HitObjects]") + 12;
		int end = raw.indexOf('[', start + 1);
		if (end == -1)
			end = raw.length();
		return SNUtils.splitFull(raw.substring(start, end), '\n');
	}

	public Beatmap ToBeatmap() throws InvalidBeatmapTypeException {
		Beatmap b = new Beatmap();

		b.diffName = getValue("Version");
		b.difficulty = Float.parseFloat(getValue("OverallDifficulty"));
		b.points = new TimingPoint[0];
		b.audio = getValue("AudioFilename");
		b.image = GetImage();
		String[] rawObjs = hitObjects();
		Vector notes = new Vector();
		if (getValue("Mode").equals("3")) {
			b.columnsCount = (int) Float.parseFloat(getValue("CircleSize"));
			for (int i = 0; i < rawObjs.length; i++) {
				if (rawObjs[i].length() < 4)
					continue;
				String[] values = SNUtils.splitFull(SNUtils.split2(rawObjs[i], ':')[0], ',');
				float x = Float.parseFloat(values[0]);
				int type = Integer.parseInt(values[3]);
				int time = Integer.parseInt(values[2]);
				int dur = ((type & 128) == 0) ? 0 : Integer.parseInt(values[5]) - time;
				int column = (int) Math.floor(x * b.columnsCount / 512);
				notes.addElement(new ManiaNote(time, column + 1, dur));
			}
		} else {
			if (getValue("Mode").equals("1"))
				throw new InvalidBeatmapTypeException("This is an osu!taiko beatmap. They are not supported.");
			if (getValue("Mode").equals("2"))
				throw new InvalidBeatmapTypeException("This is an osu!catch beatmap. They are not supported.");
			b.columnsCount = 2;
			for (int i = 0; i < rawObjs.length; i++) {
				if (rawObjs[i].length() < 4)
					continue;
				String[] values = SNUtils.splitFull(SNUtils.split2(rawObjs[i], ':')[0], ',');
				//int type = Integer.parseInt(values[3]);
				int time = Integer.parseInt(values[2]);
				// sliders are not supported yet
				//int dur = ((type & 2) == 0) ? 0 : Integer.parseInt(values[5]) - time;
				notes.addElement(new ManiaNote(time, i%2 + 1, 0));
			}
		}
		b.notes = new ManiaNote[notes.size()];
		notes.copyInto(b.notes);
		return b;
	}

	public String GetTitle() {
		return getValue("Title");
	}

	public String GetArtist() {
		return getValue("Artist");
	}

	public String GetMapper() {
		return getValue("Creator");
	}

}