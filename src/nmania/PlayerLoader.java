package nmania;

import javax.microedition.lcdui.Displayable;

import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.ui.KeyboardSetup;
import nmania.ui.ng.IDisplay;

/**
 * Utility for starting player. Construct it and start the thread to begin
 * player preparation.
 * 
 * @author Feodor0090
 *
 */
public class PlayerLoader extends Thread {

	public PlayerLoader(PlayerBootstrapData data, IInputOverrider input, ILogger log, IDisplay disp) {
		super("Player loader");
		this.input = input;
		this.log = log;
		this.data = data;
		display = disp;
		this.back = disp.GetDisplayable();
	}

	public PlayerLoader(PlayerBootstrapData data, IInputOverrider input, ILogger log, Displayable back) {
		super("Player loader");
		this.input = input;
		this.log = log;
		this.data = data;
		this.back = back;
	}

	private final IInputOverrider input;
	private ILogger log;
	final PlayerBootstrapData data;
	private Displayable back;
	private IDisplay display;

	public void run() {
		Beatmap b;
		try {
			Thread.sleep(1);
			b = BeatmapManager.ReadBeatmap(data).ToBeatmap();
		} catch (InvalidBeatmapTypeException e) {
			log.logError("Beatmap is invalid");
			return;
		} catch (InterruptedException e) {
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log.logError("Failed to parse beatmap!");
			return;
		}
		b.set = data.set;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			return;
		}
		if (Settings.keyLayout[b.columnsCount - 1] == null) {
			// no keyboard layout
			if (display != null)
				display.PauseRendering();
			KeyboardSetup kbs = new KeyboardSetup(b.columnsCount, back);
			Nmania.Push(kbs);
			return;
		}
		if (!Settings.keepMenu) {
			back = null;
		}
		try {
			Player p = new Player(b, data, Nmania.skin, log, back, input);
			if (display != null) {
				if (Settings.keepMenu)
					display.PauseRendering();
				else
					display.Destroy();
				display = null;
			}
			Nmania.Push(p);
			Thread t = new PlayerThread(p);
			t.start();
		} catch (InterruptedException e) {
			Nmania.Push(back);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			Nmania.Push(back);
			log.logError(e.toString());
		}
	}
}
