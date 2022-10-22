package nmania.beatmaps.osu;

import java.util.Vector;

import nmania.Beatmap;
import nmania.Beatmap.Break;
import nmania.Beatmap.ManiaNote;
import nmania.beatmaps.IRawBeatmap;
import nmania.beatmaps.InvalidBeatmapTypeException;
import symnovel.SNUtils;

public final class RawOsuBeatmap implements IRawBeatmap {
	public final String raw;

	public RawOsuBeatmap(String raw) {
		super();
		this.raw = raw;
	}

	public final String getValue(String key) {
		key = "\n" + key + ":";
		int b = raw.indexOf(key);
		if (b == -1) {
			throw new RuntimeException("Failed to get key " + key);
		}
		b = b + key.length();
		int cr = raw.indexOf('\r', b);
		int lf = raw.indexOf('\n', b);
		int e = lf;
		if (cr != -1)
			e = Math.min(cr, lf);
		if (lf == -1)
			e = raw.length();
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

	public final Break[] breaks() {
		int start = raw.indexOf("[Events]") + 8;
		int end = raw.indexOf("\n[", start);
		if (end == -1)
			end = raw.length();
		String[] lines = SNUtils.splitFull(raw.substring(start, end), '\n');
		Vector v = new Vector(lines.length);
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].indexOf("2,") == 0) {
				String[] values = SNUtils.splitFull(lines[i], ',');
				int st = Integer.parseInt(SNUtils.split2(values[1], '.')[0]);
				int dur = Integer.parseInt(SNUtils.split2(SNUtils.split2(values[2], '.')[0], '\r')[0]) - st;
				if (dur < 3000)
					continue;
				Break b = new Break(st, dur);
				v.addElement(b);
			}
		}
		v.addElement(new Break(Integer.MAX_VALUE - 2, 1)); // this is requered by player to optimize MORE checks
		Break[] arr = new Break[v.size()];
		v.copyInto(arr);
		return arr;
	}

	public Beatmap ToBeatmap() throws InvalidBeatmapTypeException {
		Beatmap b = new Beatmap();

		b.diffName = getValue("Version");
		b.difficulty = Float.parseFloat(getValue("OverallDifficulty"));
		float[][] td = GetTimingData();
		if (td != null)
			b.timingPoints = td[0];
		b.audio = GetAudio();
		b.image = GetImage();
		b.breaks = breaks();
		String[] rawObjs = hitObjects();
		Vector notes = new Vector();
		if (getValue("Mode").equals("1")) {
			// taiko
			b.columnsCount = 3;
			for (int i = 0; i < rawObjs.length; i++) {
				if (rawObjs[i].length() < 4)
					continue;
				String[] values = SNUtils.splitFull(rawObjs[i], ',');
				/*
				 * System.out.println("Raw obj " + i + ", vals count " + values.length);
				 * System.out.println(rawObjs[i]); System.out.println(values[values.length -
				 * 1]);
				 */
				int time = Integer.parseInt(SNUtils.split2(values[2], '.')[0].trim());
				int type = Integer.parseInt(values[3].trim());
				if ((type & 8) != 0) {
					// spinner
					int dur = Integer.parseInt(SNUtils.split2(values[5], '.')[0].trim()) - time;
					for (int j = 1; j <= 3; j++) {
						notes.addElement(new ManiaNote(time, j, dur));
					}
				} else {
					// something else
					int sound = Integer.parseInt(values[4]);
					if ((type & 2) == 0) {
						// don / kat
						if ((sound & 8) == 0) {
							notes.addElement(new ManiaNote(time, 1, 0));
						} else {
							notes.addElement(new ManiaNote(time, 3, 0));
						}
						if ((sound & 4) != 0)
							notes.addElement(new ManiaNote(time, 2, 0));
					} else {
						// drumroll
						int dur = Integer.parseInt(SNUtils.split2(values[7], '.')[0].trim());
						if ((sound & 4) == 0) {
							notes.addElement(new ManiaNote(time, 2, dur));
						} else {
							notes.addElement(new ManiaNote(time, 1, dur));
							notes.addElement(new ManiaNote(time, 3, dur));
						}
					}
				}
			}
		} else if (getValue("Mode").equals("3")) {
			// mania
			b.columnsCount = (int) Float.parseFloat(getValue("CircleSize").trim());
			if (b.columnsCount < 1 || b.columnsCount > 10)
				throw new InvalidBeatmapTypeException(
						"This is a " + b.columnsCount + "K beatmap. Only 1K-10K are supported.");

			for (int i = 0; i < rawObjs.length; i++) {
				if (rawObjs[i].length() < 4)
					continue;
				String[] values = SNUtils.splitFull(SNUtils.split2(rawObjs[i], ':')[0], ',');
				float x = Float.parseFloat(values[0].trim());
				int type = Integer.parseInt(values[3].trim());
				int time = Integer.parseInt(SNUtils.split2(values[2], '.')[0].trim());
				int dur = ((type & 128) == 0) ? 0 : Integer.parseInt(SNUtils.split2(values[5], '.')[0].trim()) - time;
				int column = (int) Math.floor(x * b.columnsCount / 512);
				notes.addElement(new ManiaNote(time, column + 1, dur));
			}
		} else {
			throw new InvalidBeatmapTypeException("This is an osu! or osu!catch beatmap. They are not supported.");
		}
		b.notes = new ManiaNote[notes.size()];
		notes.copyInto(b.notes);
		try {
			String ss = getValue("SampleSet").toLowerCase();
			if (ss.equals("drum")) {
				b.defaultSampleSet = 2;
			} else if (ss.equals("soft")) {
				b.defaultSampleSet = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public String GetAudio() {
		return getValue("AudioFilename");
	}

	public String GetMode() {
		switch (Integer.parseInt(getValue("Mode"))) {
		case 3:
			return IRawBeatmap.VSRG;
		case 0:
			return "osu!";
		case 1:
			return "taiko";
		case 2:
			return "fruits";
		default:
			return "???";
		}

	}

	public float[][] GetTimingData() {
		try {
			int start = raw.indexOf("[TimingPoints]") + "[TimingPoints]".length();
			int end = raw.indexOf('[', start + 1);
			if (end == -1)
				end = raw.length();
			String[] lines = SNUtils.splitFull(raw.substring(start, end), '\n');
			Vector timings = new Vector();
			Vector kiai = new Vector();
			float kiaiStart = -1;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].length() < 3)
					continue;
				String[] line = SNUtils.splitFull(lines[i], ',');
				if (line.length != 8)
					continue;
				float time = Float.parseFloat(line[0]);
				char c = line[7].charAt(0);
				if (c == '1' || c == '4') {
					if (kiaiStart < 0)
						kiaiStart = time;
				} else {
					if (kiaiStart >= 0) {
						kiai.addElement(new Float(kiaiStart));
						kiai.addElement(new Float(time));
						kiaiStart = -1;
					}
				}
				if (line[1].charAt(0) != '-') {
					timings.addElement(new Float(time));
					timings.addElement(new Float(Float.parseFloat(line[1])));
				}
			}
			lines = null;
			float[] timingsArr = new float[timings.size()];
			for (int i = 0; i < timingsArr.length; i++) {
				timingsArr[i] = ((Float) timings.elementAt(i)).floatValue();
			}
			timings = null;
			float[] kiaiArr = new float[kiai.size()];
			for (int i = 0; i < kiaiArr.length; i++) {
				kiaiArr[i] = ((Float) kiai.elementAt(i)).floatValue();
			}
			return new float[][] { timingsArr, kiaiArr };
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
		}
		return null;
	}

}