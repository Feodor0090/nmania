package nmania;

public final class Beatmap {
	int id;
	String diffName;
	float starRating;
	String image;
	String audio;
	int columnsCount;
	float difficulty;
	ManiaNote[] notes;
	TimingPoint[] points;
	BeatmapSet set;
	
	
	public static final class ManiaNote {
		int column;
		int time;
		int duration;
	}
	
	public static final class TimingPoint {
		int time;
		int signature;
		int divider;
		int bpm;
	}
}
