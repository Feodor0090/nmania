package nmania;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

import org.json.me.JSONObject;

import nmania.Beatmap.RawOsuBeatmap;
import symnovel.SNUtils;

public class PlayerLoader extends Thread implements ILogger {

	public PlayerLoader(BeatmapSet set, String bmFileName) {
		this.set = set;
		this.bmfn = bmFileName;
	}

	BeatmapSet set;
	String bmfn;
	Alert a;

	public void run() {
		a = new Alert("nmania", "Reading beatmap file", null, AlertType.INFO);
		Nmania.Push(a);
		Beatmap b;
		{
			String raw = BeatmapManager.getStringFromFS(set.wdPath + set.folderName + bmfn);
			if (raw.startsWith("osu file format")) {
				b = new Beatmap(new RawOsuBeatmap(raw));
			} else if (raw.charAt(0) == '{') {
				b = new Beatmap(new JSONObject(raw));
			} else
				throw new IllegalArgumentException("Illiegal beatmap content");
		}
		b.set = set;
		try {
			Player p = new Player(b, this);
			Nmania.Push(p);
			Thread t = new PlayerThread(p);
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}

	public void log(String s) {
		a.setString(s);
	}
}
