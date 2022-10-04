package nmania;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;

import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.ui.BeatmapSetPage;
import nmania.ui.KeyboardSetup;
import nmania.ui.MainScreen;

/**
 * Utility for starting player. Construct it and start the thread to begin player preparation.
 * 
 * @author Feodor0090
 *
 */
public class PlayerLoader extends Thread implements ILogger, CommandListener {


	public PlayerLoader(PlayerBootstrapData data, IInputOverrider input, BeatmapSetPage page) {
		super("Player loader");
		this.input = input;
		this.page = page;
		this.data = data;
	}

	private final IInputOverrider input;
	Displayable page;
	final PlayerBootstrapData data;
	Alert a;
	Command cancelCmd = new Command(Nmania.commonText[8], Command.STOP, 1);

	public void run() {
		a = new Alert("nmania", "Reading beatmap file", null, AlertType.INFO);
		a.setTimeout(Alert.FOREVER);
		a.addCommand(cancelCmd);
		a.setIndicator(new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
		a.setCommandListener(this);
		Nmania.Push(a);
		Beatmap b;
		try {
			Thread.sleep(1);
			b = BeatmapManager.ReadBeatmap(data).ToBeatmap();
		} catch (InvalidBeatmapTypeException e) {
			PushWaitPush(page, new Alert("Beatmap is invalid", e.getMessage(), null, AlertType.ERROR));
			return;
		} catch (InterruptedException e) {
			Nmania.Push(page);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			PushWaitPush(page, new Alert("Failed to parse beatmap", e.toString(), null, AlertType.ERROR));
			return;
		}
		b.set = data.set;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Nmania.Push(page);
		}
		if (Settings.keyLayout[b.columnsCount - 1] == null) {
			// no keyboard layout
			KeyboardSetup kbs = new KeyboardSetup(b.columnsCount, page);
			Nmania.Push(kbs);
			return;
		}
		if (!Settings.keepMenu) {
			page = null;
		}
		try {
			Player p = new Player(b, data, Nmania.skin, this, page, input);
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

	private final static void PushWaitPush(Displayable s1, Alert s2) {
		Nmania.Push(s1);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Nmania.Push(s2);
		return;
	}

	public void log(String s) {
		a.setString(s);
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0 == cancelCmd)
			this.interrupt();
	}
}
