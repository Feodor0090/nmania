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
import nmania.Sample;
import nmania.ScoreController;
import nmania.replays.IRawReplay;

public final class ResultsScreen extends Canvas {

	public ResultsScreen(IScore score, IInputOverrider input, IRawReplay replay, AudioController track, String sample,
			Image background, Displayable menu) {
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
	}

	/**
	 * @deprecated
	 * @param score
	 * @param music
	 * @param bg
	 * @param next
	 * @param applauseFile
	 * @param input
	 */
	public ResultsScreen(ScoreController score, AudioController music, Image bg, Displayable next,
			final String applauseFile, IInputOverrider input) {
		super();
		this.score = score;
		this.music = music;
		this.bg = bg;
		this.next = next;
		if (input == null)
			title = Nmania.commonText[11];
		else
			title = input.GetName();
		setFullScreenMode(true);
		(new Thread() {
			public void run() {
				try {
					while (introTimer > 0) {
						repaint();
						Thread.sleep(10);
						introTimer--;
					}
					repaint();
					if (applauseFile != null) {
						applause = new Sample(applauseFile, null);
						applause.Play();
					}
				} catch (Exception e) {
					return;
				}
			}
		}).start();
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
	boolean replayCanBeSaved = false;
	Sample applause = null;
	String title;

	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		int anchorRT = Graphics.RIGHT | Graphics.TOP;
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
		print(g, "by " + score.GetPlayerName() + " at " + score.PlayedAt(), w / 2, th16 + 3, -1,
				Graphics.HCENTER | Graphics.TOP);
		int y = th16 + th8 + 4 + 1;

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
		y += th8 + 2;
		print(g, Player.judgements[4], 10, y, Player.judgementColors[4], 0);
		print(g, String.valueOf(score.GetGreats()), cl, y, -1, anchorRT);
		print(g, Player.judgements[1], cr, y, Player.judgementColors[1], 0);
		print(g, String.valueOf(score.GetMehs()), w - 10, y, -1, anchorRT);
		y += th8 + 2;
		print(g, Player.judgements[3], 10, y, Player.judgementColors[3], 0);
		print(g, String.valueOf(score.GetGoods()), cl, y, -1, anchorRT);
		print(g, Player.judgements[0], cr, y, Player.judgementColors[0], 0);
		print(g, String.valueOf(score.GetMisses()), w - 10, y, -1, anchorRT);
		y += th8 + 2;

		if (replay == null) {
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
		music.Stop();
		Nmania.Push(next == null ? new MainScreen() : next);
	}

	protected void keyReleased(int k) {
		if (k == -1 || k == -2 || k == -3 || k == -4 || k == '2' || k == '4' || k == '6' || k == '8') {
			if (replay != null)
				itemSelected = !itemSelected;
			repaint();
			return;
		}

		if (k == -6) {
			if (replaySaveDialog) {

			} else {

			}
		}
		if (k == -7) {
			if (replaySaveDialog) {
				Exit();
			} else {
				if (replayCanBeSaved) {
					replaySaveDialog = true;
					repaint();
				} else {
					Exit();
				}
			}
		}
		if (k == -5 || k == '5' || k == 32 || k == 10) {
			keyReleased(itemSelected ? -6 : -7);
		}
	}

	protected void pointerReleased(int arg0, int arg1) {
		Exit();
	}
}
