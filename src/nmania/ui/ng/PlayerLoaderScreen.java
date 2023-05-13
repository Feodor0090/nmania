package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.BeatmapSet;
import nmania.GL;
import nmania.ILogger;
import nmania.PlayerBootstrapData;
import nmania.PlayerLoader;

public class PlayerLoaderScreen extends Screen implements ILogger, Runnable {

	public static final int TRANSITION_DUR = 400;
	private final Font f = Font.getFont(0, 0, 8);
	private final int tc = Graphics.TOP | Graphics.HCENTER;

	private final String title;
	private final String diff;
	private String state = "Waiting for loader...";
	private String hash;
	private final String replay;

	private final PlayerBootstrapData data;

	private boolean failed;
	private long stime = -1;
	private IDisplay d;
	private Thread th;

	public PlayerLoaderScreen(PlayerBootstrapData data) {
		this.data = data;
		title = data.set.artist + " - " + data.set.title;
		diff = BeatmapSet.GetDifficultyNameFast(data.mapFileName);
		if (data.input == null) {
			if (data.recordReplay)
				replay = "Replay will be recorded!";
			else
				replay = "Recording disabled.";
		} else {
			replay = "Running replay";
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
		int fh = f.getHeight();
		int ch = NmaniaDisplay.logo.getHeight() + fh * (failed ? 3 : 4);
		int y = h / 2 - ch / 2;
		int trofs = 0;
		if (stime == 0) {
			trofs = w;
		} else if (stime != -1) {
			int n = (int) (System.currentTimeMillis() - stime);
			trofs = w * n / TRANSITION_DUR;
		}
		g.drawImage(NmaniaDisplay.logo, w / 2 - trofs, y, tc);
		y += NmaniaDisplay.logo.getHeight();
		NmaniaDisplay.print(g, title, w / 2 + trofs, y, -1, 0, tc);
		y += fh;
		NmaniaDisplay.print(g, diff, w / 2 - trofs, y, -1, 0, tc);
		y += fh;
		if (failed) {
			NmaniaDisplay.print(g, "Game failed to load!", w / 2, y, 0xff0000, -1, tc);
			return;
		}
		if (hash != null)
			NmaniaDisplay.print(g, hash, w / 2 + trofs, y, -1, 0, tc);
		y += fh;
		NmaniaDisplay.print(g, replay, w / 2 - trofs, y, -1, 0, tc);
	}

	public void OnKey(IDisplay d, int k) {
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
	}

	public void OnEnter(IDisplay d) {
		this.d = d;
		th = new Thread(this);
		th.start();
	}

	public boolean OnExit(IDisplay d) {
		d.Throttle(false);
		Thread t = th;
		if (t != null)
			t.interrupt();
		return false;
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
		GL.Log("(player) " + data.set.toString());
		GL.Log("(player) hash " + hash);
		GL.Log("(player) " + data.mapFileName);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			return;
		}
		th = new PlayerLoader(data, this, d);
		th.start();
	}

	public void logError(String s) {
		state = s;
		failed = true;
		GL.Log("(player) Player loading failed");
		GL.Log("(player) " + s);
	}

	public void log(String s) {
		state = s;
		GL.Log("(player) " + s);// ?dbg
	}

	public void StartTransition() {
		d.Throttle(false);
		stime = System.currentTimeMillis();
	}

	public void EndTransition() {
		stime = 0;
		th = null;
	}

	public int DecorationsXOffset() {
		if (stime > 0) {
			int n = (int) (System.currentTimeMillis() - stime);
			return d.GetDisplayable().getWidth() * n / TRANSITION_DUR;
		}
		return 0;
	}

}
