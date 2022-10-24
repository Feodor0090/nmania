package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.BeatmapSet;
import nmania.GL;
import nmania.IInputOverrider;
import nmania.ILogger;
import nmania.PlayerBootstrapData;
import nmania.PlayerLoader;
import nmania.Settings;

public class PlayerLoaderScreen implements IScreen, ILogger, Runnable {

	Font f = Font.getFont(0, 0, 8);
	String title;
	String diff;
	String state = "Waiting for loader...";
	String hash = "";
	String replay;
	boolean failed;
	int tc = Graphics.TOP | Graphics.HCENTER;
	private IInputOverrider input;
	private PlayerBootstrapData data;
	IDisplay d;
	Thread th;

	public PlayerLoaderScreen(IInputOverrider input, PlayerBootstrapData data) {
		this.input = input;
		this.data = data;
		title = data.set.artist + " - " + data.set.title;
		diff = BeatmapSet.GetDifficultyNameFast(data.mapFileName);
		if (input == null) {
			if (Settings.recordReplay)
				replay = "Replay will be recorded!";
			else
				replay = "Replay recording disabled";
		} else {
			replay = "Playing replay";
		}
	}

	public String GetTitle() {
		return state;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void Paint(Graphics g, int w, int h) {
		g.setFont(f);
		int lh = NmaniaDisplay.logo.getHeight();
		int fh = f.getHeight();
		int ch = lh + fh * (failed ? 3 : 4);
		int y = h / 2 - ch / 2;
		g.drawImage(NmaniaDisplay.logo, w / 2, y, tc);
		y += lh;
		NmaniaDisplay.print(g, title, w / 2, y, -1, 0, tc);
		y += fh;
		NmaniaDisplay.print(g, diff, w / 2, y, -1, 0, tc);
		y += fh;
		if (failed) {
			NmaniaDisplay.print(g, "Game failed to load!", w / 2, y, 0xff0000, -1, tc);
			return;
		}
		NmaniaDisplay.print(g, hash, w / 2, y, -1, 0, tc);
		y += fh;
		NmaniaDisplay.print(g, replay, w / 2, y, -1, 0, tc);
	}

	public void OnKey(IDisplay d, int k) {
	}

	public void OnEnter(IDisplay d) {
		this.d = d;
		th = new Thread(this);
		th.start();
	}

	public boolean OnExit(IDisplay d) {
		d.Throttle(false);
		th.interrupt();
		return false;
	}

	public void OnPause(IDisplay d) {
	}

	public void OnResume(IDisplay d) {
		d.Back();
	}

	public void run() {
		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			return;
		}
		d.Throttle(true);
		hash = data.ReadBeatmapMd5();
		GL.LogStats();
		GL.Log("(loading) " + data.set.toString());
		GL.Log("(loading) hash " + hash);
		GL.Log("(loading) " + data.mapFileName);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			return;
		}
		th = new PlayerLoader(data, input, this, d);
		th.start();
	}

	public void logError(String s) {
		state = s;
		failed = true;
		GL.Log("Player loading failed");
		GL.Log("(loading) " + s);
	}

	public void log(String s) {
		state = s;
		GL.Log("(loading) " + s);// ?dbg=
	}

}
