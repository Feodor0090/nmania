package nmania;

import org.json.me.JSONArray;

import nmania.beatmaps.IRawBeatmap;
import symnovel.IComparable;

/**
 * Beatmap model. Can be created using {@link IRawBeatmap#ToBeatmap()} and
 * passed to {@link Player}.
 * 
 * @author Feodor0090
 *
 */
public final class Beatmap {
	public int id;
	public String diffName;
	public String image;
	public String audio;
	public int columnsCount;
	public float difficulty;
	public ManiaNote[] notes;
	public float[] timingPoints;
	public Break[] breaks;
	public BeatmapSet set;
	public int defaultSampleSet = 0;

	public Beatmap() {
	}

	public String ToGlobalPath(String local) {
		return set.ToGlobalPath(local);
	}

	/**
	 * Provides quick info about the beatmap.
	 * 
	 * @return Text to display in diff select.
	 */
	public String Analyze() {
		if (columnsCount > 10)
			return columnsCount + "K (not supported)";
		int hits = 0;
		int holds = 0;
		int firstNoteTime = Integer.MAX_VALUE;
		int lastNoteTime = 0;
		for (int i = 0; i < notes.length; i++) {
			ManiaNote n = notes[i];
			firstNoteTime = Math.min(n.time, firstNoteTime);
			lastNoteTime = Math.max(n.time + n.duration, lastNoteTime);
			if (n.duration <= 0)
				hits++;
			else
				holds++;
		}
		Thread.yield();
		float bl = 60000f;
		for (int i = 1; i < timingPoints.length; i += 2) {
			bl = Math.min(bl, timingPoints[i]);
		}
		int time = (lastNoteTime - firstNoteTime) / 1000;
		int min = time / 60;
		int sec = time % 60;
		Thread.yield();
		String dur = min + ":" + (sec < 10 ? "0" : "") + sec;
		int nps = (int) (((hits + holds) / (float) time) * 100);
		return columnsCount + "K, " + (int) (60000f / bl) + "BPM, " + dur + ", " + hits + "+" + holds + "notes, "
				+ (nps / 100) + "." + (nps % 100) + "n/s";
	}

	/**
	 * Nmania's the only hitobject. Zero or negative duration means that this is a
	 * hit note, else hold one.
	 * 
	 * @author Feodor0090
	 */
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

	/**
	 * Break in a map.
	 * 
	 * @author Feodor0090
	 *
	 */
	public static final class Break {
		public final int time;
		public final int duration;

		public Break(int t, int d) {
			time = t;
			duration = d;
		}

		/**
		 * Inlines set of breaks to 1d array.
		 * 
		 * @param breaks Set of breaks.
		 * @return Inlined data array.
		 */
		public final static int[] Inline(Break[] breaks) {
			int[] arr = new int[breaks.length * 2];
			for (int i = 0; i < breaks.length; i++) {
				arr[i * 2] = breaks[i].time;
				arr[i * 2 + 1] = breaks[i].duration;
			}
			return arr;
		}
	}

}
