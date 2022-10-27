package nmania.ui.ng;

import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import nmania.Settings;

public class BrowserView extends Alert {

	private JSONObject beatmap;

	public BrowserView(JSONObject beatmap) {
		super(beatmap.optString("Artist") + " - " + beatmap.optString("Title") + " (" + beatmap.optString("Creator")
				+ ")", null);
		this.beatmap = beatmap;
		Vector v = new Vector();
		v.addElement("Title: " + beatmap.optString("Title"));
		v.addElement("Artist: " + beatmap.optString("Artist"));
		v.addElement("Mapper: " + beatmap.optString("Creator"));
		v.addElement("Source: " + beatmap.optString("Source"));
		v.addElement("Status: " + status(beatmap.optInt("RankedStatus")));
		v.addElement("ID: " + beatmap.optInt("SetID"));
		JSONArray maps = beatmap.getJSONArray("ChildrenBeatmaps");
		v.addElement("Beatmaps count: " + maps.length());
		for (int i = 0; i < maps.length(); i++) {
			JSONObject bm = maps.getJSONObject(i);
			if (bm.getInt("Mode") != 3)
				continue;
			v.addElement(bm.getString("DiffName") + " (" + bm.optDouble("DifficultyRating", 0) + "*)");
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); i++) {
			if (i != 0)
				sb.append(" \n ");
			sb.append(v.elementAt(i).toString());
		}
		SetText(sb.toString());
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "DOWNLOAD";
	}

	private String status(int s) {
		switch (s) {
		case -2:
		case -1:
		case 0:
			return "not ranked";
		case 1:
		case 2:
			return "ranked";
		case 3:
			return "qualified";
		case 4:
			return "loved";
		}
		return "unknown";
	}

	public void OnOptionActivate(IDisplay d) {
		String url = "http://nnp.nnchan.ru/glype/browse.php?u=https://kitsu.moe/api/d/" + beatmap.optInt("SetID");
		String filename = "file:///" + Settings.workingFolder + beatmap.optInt("SetID") + " "
				+ beatmap.optString("Title") + ".osz";
		d.Push(new BrowserDownloader(title, url, filename));
	}

}
