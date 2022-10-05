package nmania.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nmania.AudioController;
import nmania.IInputOverrider;
import nmania.IScore;
import nmania.Nmania;
import nmania.Player;
import nmania.PlayerBootstrapData;
import nmania.PlayerLoader;
import nmania.Sample;
import nmania.replays.IRawReplay;
import nmania.replays.ReplayPlayer;

public final class ResultsScreen extends Canvas {

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
	public ResultsScreen(PlayerBootstrapData data, IScore score, IInputOverrider input, IRawReplay replay,
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
		setFullScreenMode(true);
	}

	int introTimer = 100;
	public IInputOverrider input;
	public IRawReplay replay;
	public final IScore score;
	public final AudioController music;
	public final Image bg;
	public final Displayable next;
	boolean itemSelected = true;
	boolean replaySaveDialog = false;
	boolean willWatch = false;
	private PlayerBootstrapData data;
	Sample applause = null;
	String title;

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
		print(g, input == null ? Nmania.commonText[11] : input.GetName(), w / 2, 1, input == null ? -1 : (200 << 8),
				Graphics.HCENTER | Graphics.TOP);
		g.setFont(Font.getFont(0, 0, 8));
		print(g, data.set.artist + " - " + data.set.title, w / 2, th16 + 3, -1, Graphics.HCENTER | Graphics.TOP);
		int y = th16 + th8 + 4 + 1;
		print(g, "by " + score.GetPlayerName() + " at " + score.PlayedAt(), w / 2, y, -1,
				Graphics.HCENTER | Graphics.TOP);
		y += th8 + 2;

		// stats
		print(g, "Total score", 10, y, -1, 0);
		print(g, String.valueOf(score.GetScore()), w - 10, y, -1, anchorRT);
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

		if (replay == null && input == null) {
			itemSelected = false;
			print(g, "Replay unavailable", w / 4, h - 10, -1, Graphics.HCENTER | Graphics.BOTTOM);
		} else {
			g.setGrayScale(itemSelected ? 63 : 0);
			g.fillRect(10, h - 5 - 10 - th0, w / 2 - 20, th0 + 10);
			g.setColor((itemSelected ? 255 : 0), 0, 0);
			g.drawRect(9, h - 5 - 10 - th0 - 1, w / 2 - 19, th0 + 11);
			print(g, replaySaveDialog ? "Save" : "Watch replay", w / 4, h - 10, -1, Graphics.HCENTER | Graphics.BOTTOM);
		}
		g.setGrayScale(!itemSelected ? 63 : 0);
		g.fillRect(w / 2 + 10, h - 5 - 10 - th0, w / 2 - 20, th0 + 10);
		g.setColor((!itemSelected ? 255 : 0), 0, 0);
		g.drawRect(w / 2 + 9, h - 5 - 10 - th0 - 1, w / 2 - 19, th0 + 11);
		print(g, replaySaveDialog ? "Discard" : "Quit", w * 3 / 4, h - 10, -1, Graphics.HCENTER | Graphics.BOTTOM);
		if (replaySaveDialog) {
			print(g, willWatch ? "Save replay? It will be lost after watch." : "You are quitting. Save replay?", w / 2,
					h - 5 - 10 - th0, -1, Graphics.HCENTER | Graphics.BOTTOM);
		}
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
		if (applause != null)
			applause.Dispose();
		if (music != null)
			music.Stop();
		Nmania.Push(next == null ? new MainScreen() : next);
	}

	protected void keyPressed(int k) {
		if (k == -1 || k == -2 || k == -3 || k == -4 || k == '2' || k == '4' || k == '6' || k == '8') {
			if (replay != null)
				itemSelected = !itemSelected;
			repaint();
			return;
		}

		if (k == -6) {
			if (replaySaveDialog) {
				SaveReplay();
				if (willWatch) {
					// we are watching our own replay
					(new PlayerLoader(data, new ReplayPlayer(replay.DecodeData(), score), next)).start();
				} else {
					// we are discarding our own replay
					Exit();
				}
			} else {
				if (replay != null) {
					// we are going to watch our own replay
					replaySaveDialog = true;
					willWatch = true;
					repaint();
				} else {
					// we are watching foreign replay
					System.out.println("Player began loading");
					(new PlayerLoader(data, input, next)).start();
				}
			}
		}
		if (k == -7) {
			if (replaySaveDialog) {
				if (willWatch) {
					// we are watching our own replay
					(new PlayerLoader(data, new ReplayPlayer(replay.DecodeData(), score), next)).start();
				} else {
					// we are discarding our own replay
					Exit();
				}
			} else {
				if (replay != null) {
					replaySaveDialog = true;
					repaint();
				} else {
					Exit();
				}
			}
		}
		if (k == -5 || k == '5' || k == 32 || k == 10) {
			keyPressed(itemSelected ? -6 : -7);
		}
	}

	private void SaveReplay() {

	}

	protected void pointerReleased(int arg0, int arg1) {
		Exit();
	}
}
