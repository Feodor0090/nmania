package nmania;

import org.json.me.JSONArray;

import nmania.formats.IRawBeatmap;
import symnovel.IComparable;

/**
 * Beatmap model. Can be created using {@link IRawBeatmap#ToBeatmap()} and passed to
 * {@link Player}.
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
	public TimingPoint[] points;
	public BeatmapSet set;
	public int defaultSampleSet = 0;

	public Beatmap() {
	}

	public String ToGlobalPath(String local) {
		return set.wdPath + set.folderName + local;
	}

	/**
	 * Nmania's the only hitobject.
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

	// TODO use somewhere or delete at all
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
