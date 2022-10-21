package nmania.beatmaps.json;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import nmania.Beatmap;
import nmania.Beatmap.ManiaNote;
import nmania.Beatmap.TimingPoint;
import nmania.beatmaps.IRawBeatmap;
import symnovel.SNUtils;

public class RawNmaniaBeatmap extends JSONObject implements IRawBeatmap {

	public RawNmaniaBeatmap(String text) {
		super(text);
	}
	
	public String GetImage() {
		return getString("image");
	}

	public Beatmap ToBeatmap() {
		Beatmap b = new Beatmap();
		b.id = getInt("id");
		b.diffName = getString("name");
		b.image = GetImage();
		b.audio = getString("audio");
		b.columnsCount = getInt("columns");
		b.difficulty = Float.parseFloat(getString("difficulty"));

		final JSONArray p = getJSONArray("timings");
		b.points = new TimingPoint[p.length()];
		for (int i = 0; i < p.length(); i++) {
			String[] s = SNUtils.split(p.getString(i), ';', 4);
			float beatLen = s[2].equals("bpm") ? (60000f / Float.parseFloat(s[3])) : Float.parseFloat(s[3]);
			b.points[i] = new TimingPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]), beatLen);
		}

		final JSONArray n = getJSONArray("notes");
		b.notes = new ManiaNote[n.length()];
		for (int i = 0; i < n.length(); i++) {
			b.notes[i] = new ManiaNote(n.getJSONArray(i));
		}
		return b;
	}

	public String GetTitle() {
		return optString("title");
	}

	public String GetArtist() {
		return optString("artist");
	}

	public String GetMapper() {
		return optString("author");
	}

	public String GetMode() {
		return IRawBeatmap.VSRG;
	}

	public String GetAudio() {
		return getString("audio");
	}

}
