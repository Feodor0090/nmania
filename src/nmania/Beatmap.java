package nmania;

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
		return set.wdPath+"/"+set.folderName+"/"+local;
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
}
