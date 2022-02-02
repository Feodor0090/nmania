package nmania;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.json.me.JSONObject;

import nmania.Beatmap.RawOsuBeatmap;
import nmania.ui.BeatmapSetPage;
import nmania.ui.KeyboardSetup;
import nmania.ui.MainScreen;

public class PlayerLoader extends Thread implements ILogger, CommandListener {

	public PlayerLoader(BeatmapSet set, String bmFileName, boolean auto, BeatmapSetPage page) {
		super("Player loader");
		this.set = set;
		this.bmfn = bmFileName;
		this.page = page;
		this.auto = auto;
	}

	final BeatmapSet set;
	final String bmfn;
	Displayable page;
	final boolean auto;
	Alert a;
	Command cancelCmd = new Command("Cancel", Command.STOP, 1);

	public void run() {
		a = new Alert("nmania", "Reading beatmap file", null, AlertType.INFO);
		a.setTimeout(Alert.FOREVER);
		a.addCommand(cancelCmd);
		a.setCommandListener(this);
		Nmania.Push(a);
		Beatmap b;
		{
			String raw = BeatmapManager.getStringFromFS(set.wdPath + set.folderName + bmfn);
			if (raw.startsWith("osu file format")) {
				b = new Beatmap(new RawOsuBeatmap(raw));
			} else if (raw.charAt(0) == '{') {
				b = new Beatmap(new JSONObject(raw));
			} else
				throw new IllegalArgumentException("Illegal beatmap content");
		}
		b.set = set;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Nmania.Push(page);
		}
		if (Settings.keyLayout[b.columnsCount - 1] == null) {
			// no keyboard layout
			KeyboardSetup kbs = new KeyboardSetup(b.columnsCount, page);
			Nmania.Push(kbs);
			try {
				Thread.sleep(50);
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
		} catch (InterruptedException e) {
			Displayable next = page == null ? new MainScreen() : page;
			Nmania.Push(next);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}

	public void log(String s) {
		a.setString(s);
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0 == cancelCmd)
			this.interrupt();
	}
}
