package nmania;

import javax.microedition.lcdui.Displayable;

import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.ui.KeyboardSetup;
import nmania.ui.ng.Alert;
import nmania.ui.ng.IDisplay;
import nmania.ui.ng.PlayerLoaderScreen;

/**
 * Utility for starting player. Construct it and start the thread to begin
 * player preparation.
 * 
 * @author Feodor0090
 *
 */
public class PlayerLoader extends Thread {

	public PlayerLoader(PlayerBootstrapData data, ILogger log, IDisplay disp) {
		super("Player loader");
		this.log = log;
		this.data = data;
		this.back = disp.GetDisplayable();
		display = disp;
	}

	public PlayerLoader(PlayerBootstrapData data, ILogger log, Displayable back) {
		super("Player loader");
		this.log = log;
		this.data = data;
		this.back = back;
		display = null;
	}

	private ILogger log;
	private final PlayerBootstrapData data;
	private Displayable back;
	private IDisplay display;

	private void alert(String title, String msg) {
		if (display == null)
			return;
		display.Push(new Alert(title, msg));
	}

	public void run() {
		try {
			Beatmap b;
			try {
				Thread.sleep(1);
				b = data.ReadBeatmap().ToBeatmap();
			} catch (InvalidBeatmapTypeException e) {
				log.logError("Beatmap is invalid");
				alert("Beatmap is invalid", e.getMessage());
				return;
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				e.printStackTrace();
				log.logError("Failed to parse beatmap! " + e.toString());
				return;
			}
			b.set = data.set;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return;
			}
			if (data.input == null && Settings.keyLayout[b.columnsCount - 1] == null) {
				// no keyboard layout
				if (display != null) {
					display.Back();
				}
				GL.Log("(player) No layout, pushing setup screen");
				KeyboardSetup kbs = new KeyboardSetup(b.columnsCount, back);
				Nmania.Push(kbs);
				return;
			}
			if (!data.keepBackScreen) {
				back = null;
			}
			try {
				Player p = new Player(b, data, Nmania.LoadSkin(false), log, back, data.input);
				if (display != null) {
					display.SetAudio(null);
					if (!data.keepBackScreen)
						display.Destroy();
					display = null;
				}

				if (log instanceof PlayerLoaderScreen) {
					((PlayerLoaderScreen) log).StartTransition();
					Thread.sleep(PlayerLoaderScreen.TRANSITION_DUR);
				} else {
					Thread.sleep(1);
				}
				GL.Log("(player) Gameplay displayable is being pushed...");
				Nmania.Push(p);
				GL.Log("(player) Gameplay displayable pushed!");
				Thread t = new PlayerThread(p);
				t.start();
				Thread.sleep(250);
				if (log instanceof PlayerLoaderScreen)
					((PlayerLoaderScreen) log).EndTransition();
			} catch (InterruptedException e) {
				Nmania.Push(back);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				Nmania.Push(back);
				log.logError(e.toString());
				alert("Failed to load player", e.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			alert("Failed to load player", e.toString());
			log.logError(e.toString());
		} catch (OutOfMemoryError e) {
			log.logError("Out of memory");
			alert("Failed to load player", e.toString());
		}
	}
}
