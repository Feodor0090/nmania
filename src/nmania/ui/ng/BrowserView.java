package nmania.ui.ng;

import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import nmania.Settings;

public class BrowserView extends ListScreen {

	private JSONObject beatmap;
	private String title;

	public BrowserView(JSONObject beatmap) {
		this.beatmap = beatmap;
		title = beatmap.optString("Artist") + " - " + beatmap.optString("Title") + " (" + beatmap.optString("Creator")
				+ ")";
	}

	public String GetTitle() {
		return title;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "DOWNLOAD";
	}

	public void OnEnter(IDisplay d) {
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
		ListItem[] items = new ListItem[v.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = new ListItem(v.elementAt(i).toString(), null);
		}
		SetItems(items);
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
