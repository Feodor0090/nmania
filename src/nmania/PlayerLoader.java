package nmania;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import org.json.me.JSONObject;

import nmania.Beatmap.RawOsuBeatmap;
import nmania.ui.BeatmapSetPage;
import nmania.ui.KeyboardLayoutSelect;

public class PlayerLoader extends Thread implements ILogger {

	public PlayerLoader(BeatmapSet set, String bmFileName, boolean auto, BeatmapSetPage page) {
		super("Player loader");
		this.set = set;
		this.bmfn = bmFileName;
		this.page = page;
		this.auto = auto;
	}

	final BeatmapSet set;
	final String bmfn;
	BeatmapSetPage page;
	final boolean auto;
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
		if (Settings.keyLayout[b.columnsCount - 1] == null) {
			// no keyboard layout
			KeyboardLayoutSelect kls = new KeyboardLayoutSelect(page);
			Nmania.Push(kls);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Alert a1 = new Alert("nmania", "There are no keybinds for this mode (" + b.columnsCount + "K). Set them.",
					null, AlertType.WARNING);
			Nmania.Push(a1);
			return;
		}
		if (!Settings.keepMenu) {
			page = null;
		}
		try {
			Player p = new Player(b, !auto, this, page);
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
