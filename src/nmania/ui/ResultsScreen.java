package nmania.ui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nmania.AudioController;
import nmania.BeatmapSet;
import nmania.GL;
import nmania.IInputOverrider;
import nmania.ILogger;
import nmania.IScore;
import nmania.Nmania;
import nmania.Player;
import nmania.PlayerBootstrapData;
import nmania.PlayerLoader;
import nmania.Sample;
import nmania.replays.IReplayProvider;
import nmania.replays.ReplayChunk;
import nmania.replays.ReplayPlayer;
import nmania.replays.json.NmaniaReplay;
import nmania.replays.osu.OsuReplay;

public final class ResultsScreen extends Canvas implements ILogger {

	/**
	 * 
	 * @param data       Date to play the beatmap again. Must not be null.
	 * @param score      Score to show. Must not be null.
	 * @param input      Input that was used in a play.
	 * @param replay     Recorded replay. Restored replays must be passed via
	 *                   "input" param, leave this null.
	 * @param track      Track to stop when exiting.
	 * @param sample     Sample to play.
	 * @param background Image.
	 * @param menu       Next screen.
	 */
	public ResultsScreen(PlayerBootstrapData data, IScore score, IInputOverrider input, IReplayProvider replay,
			AudioController track, String sample, Image background, Displayable menu) {
		this.data = data;
		this.score = score;
		this.input = input;
		this.replay = replay;
		music = track;
		try {
			if (sample != null) {
				applause = new Sample(sample, null);
				applause.Play();
			}
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		bg = background;
		next = menu;
		if (input == null && replay == null)
			activeMenu = 1;
		setFullScreenMode(true);
	}

	int introTimer = 100;
	public IInputOverrider input;
	public IReplayProvider replay;
	public final IScore score;
	public final AudioController music;
	public final Image bg;
	public final Displayable next;
	boolean itemSelected = true;
	private PlayerBootstrapData data;
	Sample applause = null;
	String title;
	String loadingLog = null;
	/**
	 * 0 - watch/quit 1 - retry/quit 2 - save/discard -> quit 3 - save/discard ->
	 * watch
	 */
	private int activeMenu;

	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		int anchorRT = Graphics.RIGHT | Graphics.TOP;
		g.setColor(0);
		if (bg == null)
			g.fillRect(0, 0, w, h);
		else
			g.drawImage(bg, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
		int th8 = Font.getFont(0, 0, 8).getHeight();
		int th0 = Font.getFont(0, 0, 0).getHeight();
		int th16 = Font.getFont(0, 0, 16).getHeight();
		int cl = w / 2 - 10;
		int cr = w / 2 + 10;

		// header
		g.setFont(Font.getFont(0, 0, 16));
		print(g, input == null ? "Results" : input.GetName(), w / 2, 1, input == null ? -1 : (200 << 8),
				Graphics.HCENTER | Graphics.TOP);
		g.setFont(Font.getFont(0, 0, 8));
		print(g, data.set.artist + " - " + data.set.title, w / 2, th16 + 3, -1, Graphics.HCENTER | Graphics.TOP);
		int y = th16 + th8 + 4 + 1;
		print(g, BeatmapSet.GetDifficultyNameFast(data.mapFileName) + " by " + score.GetPlayerName() + " "
				+ formatDate(score.PlayedAt(), ":"), w / 2, y, -1, Graphics.HCENTER | Graphics.TOP);
		y += th8 + 2;

		// stats
		print(g, "Score", 10, y, -1, 0);
		print(g, String.valueOf(score.GetScore()), cl, y, -1, anchorRT);
		print(g, "Mods", cr, y, -1, 0);
		print(g, data.mods.toString(), w - 10, y, -1, anchorRT);

		y += th8 + 2;
		print(g, "Accuracy", 10, y, -1, 0);
		int acc = score.GetAccuracy();
		print(g, (acc / 100f) + "%", cl, y, acc == 10000 ? (255 << 8) : -1, anchorRT);
		print(g, "Combo", cr, y, -1, 0);
		print(g, score.GetCombo() + "x", w - 10, y, score.IsFC() ? (255 << 8) : -1, anchorRT);
		y += th8 + 2;

		// judgments
		print(g, Player.judgements[5], 10, y, Player.judgementColors[5], 0);
		print(g, String.valueOf(score.GetPerfects()), cl, y, -1, anchorRT);
		print(g, Player.judgements[2], cr, y, Player.judgementColors[2], 0);
		print(g, String.valueOf(score.GetOks()), w - 10, y, -1, anchorRT);
		y += th8;
		print(g, Player.judgements[4], 10, y, Player.judgementColors[4], 0);
		print(g, String.valueOf(score.GetGreats()), cl, y, -1, anchorRT);
		print(g, Player.judgements[1], cr, y, Player.judgementColors[1], 0);
		print(g, String.valueOf(score.GetMehs()), w - 10, y, -1, anchorRT);
		y += th8;
		print(g, Player.judgements[3], 10, y, Player.judgementColors[3], 0);
		print(g, String.valueOf(score.GetGoods()), cl, y, -1, anchorRT);
		print(g, Player.judgements[0], cr, y, Player.judgementColors[0], 0);
		print(g, String.valueOf(score.GetMisses()), w - 10, y, -1, anchorRT);

		if (loadingLog != null) {
			print(g, loadingLog, w / 2, h - 10, -1, Graphics.HCENTER | Graphics.BOTTOM);
			return;
		}
		String info = null;
		String left = null;
		String right = null;
		if (activeMenu == 0) {
			left = "Watch";
			right = "Quit";
		} else if (activeMenu == 1) {
			info = "No replay available.";
			left = "Retry";
			right = "Quit";
		} else if (activeMenu == 2) {
			info = "Save your replay?";
			left = "Save";
			right = "Discard";
		} else if (activeMenu == 3) {
			info = "Replay will be lost. Save?";
			left = "Save";
			right = "Discard";
		}
		if (info != null)
			print(g, info, w / 2, h - 5 - 10 - th0, -1, Graphics.HCENTER | Graphics.BOTTOM);
		g.setGrayScale(itemSelected ? 63 : 0);
		g.fillRect(10, h - 5 - 10 - th0, w / 2 - 20, th0 + 10);
		g.setColor((itemSelected ? 255 : 0), 0, 0);
		g.drawRect(9, h - 5 - 10 - th0 - 1, w / 2 - 19, th0 + 11);
		print(g, left, w / 4, h - 10, -1, Graphics.HCENTER | Graphics.BOTTOM);
		g.setGrayScale(!itemSelected ? 63 : 0);
		g.fillRect(w / 2 + 10, h - 5 - 10 - th0, w / 2 - 20, th0 + 10);
		g.setColor((!itemSelected ? 255 : 0), 0, 0);
		g.drawRect(w / 2 + 9, h - 5 - 10 - th0 - 1, w / 2 - 19, th0 + 11);
		print(g, right, w * 3 / 4, h - 10, -1, Graphics.HCENTER | Graphics.BOTTOM);
	}

	public static final String formatDate(Date d, String timeSplitter) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int hrs = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		String time = (hrs < 10 ? "0" : "") + hrs + timeSplitter + (min < 10 ? "0" : "") + min + timeSplitter
				+ (sec < 10 ? "0" : "") + sec;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int mnt = c.get(Calendar.MONTH);
		String date = (day < 10 ? "0" : "") + day + "." + (mnt < 10 ? "0" : "") + mnt + "." + c.get(Calendar.YEAR);
		return time + " " + date;
	}

	private static final void print(Graphics g, String s, int x, int y, int color, int anchor) {
		g.setColor(0);
		g.drawString(s, x - 1, y - 1, anchor);
		g.drawString(s, x - 1, y + 1, anchor);
		g.drawString(s, x + 1, y - 1, anchor);
		g.drawString(s, x + 1, y + 1, anchor);
		g.setColor(color);
		g.drawString(s, x, y, anchor);
	}

	private final void Exit() {
		Dispose();
		if (next == null)
			Nmania.PushMainScreen();
		else
			Nmania.Push(next);
	}

	private final void Dispose() {
		if (applause != null)
			applause.Dispose();
		if (music != null)
			music.Stop();
	}

	protected void keyPressed(int k) {
		if (k == -1 || k == -2 || k == -3 || k == -4 || k == '2' || k == '4' || k == '6' || k == '8') {
			itemSelected = !itemSelected;
			repaint();
			return;
		}

		if (loadingLog != null)
			return;

		if (k == -6) {
			if (activeMenu == 0) {
				if (replay != null) {
					activeMenu = 3;
					repaint();
				} else {
					// we are watching foreign replay
					Dispose();
					(new PlayerLoader(data, input, this, next)).start();
				}
			} else if (activeMenu == 1) {
				Dispose();
				(new PlayerLoader(data, null, this, next)).start();
			} else if (activeMenu == 2) {
				if (SaveReplay())
					Exit();
			} else if (activeMenu == 3) {
				if (SaveReplay()) {
					// we are watching our own replay

					ReplayChunk chunk = replay.GetReplay();
					if (chunk == null) {
						activeMenu = 1;
						Nmania.Push(new Alert("nmania", "Could not read replay.", null, AlertType.ERROR));
					} else {
						Dispose();
						(new PlayerLoader(data, new ReplayPlayer(chunk, score), this, next)).start();
					}
				}
			}
		}
		if (k == -7) {
			if (activeMenu == 0) {
				if (replay != null) {
					activeMenu = 2;
					repaint();
				} else {
					Exit();
				}
			} else if (activeMenu == 1) {
				Exit();
			} else if (activeMenu == 2) {
				Exit();
			} else if (activeMenu == 3) {
				ReplayChunk chunk = replay.GetReplay();
				if (chunk == null) {
					activeMenu = 1;
					Nmania.Push(new Alert("nmania", "Could not read replay.", null, AlertType.ERROR));
				} else {
					Dispose();
					(new PlayerLoader(data, new ReplayPlayer(chunk, score), this, next)).start();
				}
			}
		}
		if (k == -5 || k == '5' || k == 32 || k == 10) {
			keyPressed(itemSelected ? -6 : -7);
		}
	}

	private boolean SaveReplay() {
		try {
			TryWriteOsr();
		} catch (Throwable e) {
			GL.Log("OSR writing failed!");
			GL.Log(e.toString());
			e.printStackTrace();
			try {
				TryWriteNmr();
			} catch (Throwable e1) {
				GL.Log("NMR writing failed!");
				GL.Log(e1.toString());
				e1.printStackTrace();
				Nmania.Push(new Alert("Could not write replay", e1.toString(), null, AlertType.ERROR));
				return false;
			}
		}
		return true;
	}

	private void TryWriteOsr() throws Throwable {
		OsuReplay r = new OsuReplay();
		r.gameMode = 3;
		r.gameVersion = 292;
		r.WriteScoreDataFrom(score);
		r.SetMods(data.mods);
		r.beatmapHash = data.ReadBeatmapMd5();
		FileConnection fc = null;
		try {
			String path = data.set.GetFilenameForNewReplay(r, data);
			fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);
			fc.create();
			ReplayChunk rc = replay.GetReplay();
			r.write(fc.openOutputStream(), rc);
			data.set.AddLastReplay();
		} catch (Throwable e) {
			try {
				fc.delete();
			} catch (Throwable e1) {
				GL.Log("Failed to cleanup replay!");
			}
			if (fc != null) {
				try {
					fc.close();
				} catch (IOException e2) {
				}
			}
			fc = null;
			throw e;
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void TryWriteNmr() throws IOException {
		NmaniaReplay r = new NmaniaReplay();
		r.WriteScoreDataFrom(score);
		r.SetMods(data.mods);
		r.beatmapHash = data.ReadBeatmapMd5();
		FileConnection fc = null;
		try {
			String path = data.set.GetFilenameForNewReplay(r, data);
			fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);
			fc.create();
			ReplayChunk rc = replay.GetReplay();
			r.Write(fc.openOutputStream(), rc);
			data.set.AddLastReplay();
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected void pointerReleased(int x, int y) {
		if (y < getHeight() / 2)
			return;
		keyPressed(x < getWidth() / 2 ? -6 : -7);
	}

	public void logError(String s) {
		loadingLog = s;
		repaint();
	}

	public void log(String s) {
		loadingLog = s;
		repaint();
	}
}
