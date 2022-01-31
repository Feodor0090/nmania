package nmania;

import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import symnovel.IComparable;
import symnovel.SNUtils;

public final class Beatmap {
	int id;
	String diffName;
	String image;
	String audio;
	int columnsCount;
	float difficulty;
	ManiaNote[] notes;
	TimingPoint[] points;
	BeatmapSet set;

	public Beatmap() {
	}

	public Beatmap(RawOsuBeatmap r) {
		if (!r.getValue("Mode").equals("3"))
			throw new IllegalArgumentException("This is osu! beatmap, not osu!mania !");
		diffName = r.getValue("Version");
		columnsCount = (int) Float.parseFloat(r.getValue("CircleSize"));
		difficulty = Float.parseFloat(r.getValue("OverallDifficulty"));
		points = new TimingPoint[0];
		audio = r.getValue("AudioFilename");
		image = r.getImage();
		String[] rawObjs = r.hitObjects();
		Vector notes = new Vector();
		for (int i = 0; i < rawObjs.length; i++) {
			if (rawObjs[i].length() < 4)
				continue;
			String[] values = SNUtils.splitFull(SNUtils.split2(rawObjs[i], ':')[0], ',');
			float x = Float.parseFloat(values[0]);
			int type = Integer.parseInt(values[3]);
			int time = Integer.parseInt(values[2]);
			int dur = ((type & 128) == 0) ? 0 : Integer.parseInt(values[5]) - time;
			int column = (int) Math.floor(x * columnsCount / 512);
			notes.addElement(new ManiaNote(time, column + 1, dur));
		}
		this.notes = new ManiaNote[notes.size()];
		notes.copyInto(this.notes);
	}

	public Beatmap(JSONObject j) {
		id = j.getInt("id");
		diffName = j.getString("name");
		image = j.getString("image");
		audio = j.getString("audio");
		columnsCount = j.getInt("columns");
		difficulty = Float.parseFloat(j.getString("difficulty"));

		final JSONArray p = j.getJSONArray("timings");
		points = new TimingPoint[p.length()];
		for (int i = 0; i < p.length(); i++) {
			String[] s = SNUtils.split(p.getString(i), ';', 4);
			float beatLen = s[2].equals("bpm") ? (60000f / Float.parseFloat(s[3])) : Float.parseFloat(s[3]);
			points[i] = new TimingPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]), beatLen);
		}

		final JSONArray n = j.getJSONArray("notes");
		notes = new ManiaNote[n.length()];
		for (int i = 0; i < n.length(); i++) {
			notes[i] = new ManiaNote(n.getJSONArray(i));
		}
	}

	public String ToGlobalPath(String local) {
		return set.wdPath + set.folderName + local;
	}

	public static final class ManiaNote implements IComparable {
		int time;
		int column;
		int duration;

		public ManiaNote(int time, int column, int duration) {
			super();
			this.time = time;
			this.column = column;
			this.duration = duration;
		}

		public ManiaNote(JSONArray a) {
			time = a.getInt(0);
			column = a.getInt(1);
			duration = a.length() > 2 ? a.getInt(2) : 0;
		}

		public int compareTo(IComparable o) {
			if (o instanceof ManiaNote) {
				return time - ((ManiaNote) o).time;
			}
			return 0;
		}
	}

	public static final class TimingPoint {
		final int time;
		final int signature;
		final float msPerBeat;

		public TimingPoint(int time, int signature, float msPerBeat) {
			super();
			this.time = time;
			this.signature = signature;
			this.msPerBeat = msPerBeat;
		}

		public float GetBpm() {
			return 60000f / msPerBeat;
		}
	}

	public final static class RawOsuBeatmap {
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

		public final String getImage() {
			int imageI = raw.indexOf("\n0,", raw.indexOf("[Events]")) + 3;

			String image = raw.substring(raw.indexOf(',', imageI) + 1, raw.indexOf('\n', imageI));
			image = image.substring(0, image.indexOf(','));
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

	}
}
