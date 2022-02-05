package nmania.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nmania.AudioController;
import nmania.Nmania;
import nmania.Player;
import nmania.Sample;
import nmania.ScoreController;

public final class ResultsScreen extends Canvas {

	public ResultsScreen(ScoreController score, AudioController music, Image bg, Displayable next,
			final String applauseFile) {
		super();
		this.score = score;
		this.music = music;
		this.bg = bg;
		this.next = next;
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
	public final ScoreController score;
	public final AudioController music;
	public final Image bg;
	public final Displayable next;
	Sample applause = null;

	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();

		g.drawImage(bg, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);

		g.setColor(-1);
		g.setFont(Font.getFont(0, 0, 16));
		g.drawString("RESULTS", w / 2, -introTimer, 17);
		g.setFont(Font.getFont(0, 0, 8));
		g.drawString("any key to proceed", w / 2, h + introTimer, Graphics.BOTTOM | Graphics.HCENTER);

		int th = Font.getFont(0, 0, 8).getHeight();
		int y = (h - th * 9) / 2;
		drawRow(g, 0, "Total score", -1, String.valueOf(score.currentHitScore), y, w);
		y += th;
		drawRow(g, 1, "Max combo", -1, score.hits[0] == 0 ? "Full combo" : String.valueOf(score.maxCombo), y, w);
		y += th;
		drawRow(g, 2, "Accuracy", -1, String.valueOf((score.GetAccuracy() / 100f) + "%"), y, w);
		y += th;
		for (int i = 0; i < 6; i++) {
			drawRow(g, 3 + i, Player.judgements[i], Player.judgementColors[i], String.valueOf(score.hits[i]), y, w);
			y += th;
		}

		if (introTimer > 50) {
			g.setColor(-1);
			g.fillRect(0, 0, w * (introTimer - 50) / 50, h);
		}
	}

	private final void drawRow(Graphics g, int i, String title, int titleColor, String value, int y, int w) {
		g.setColor(titleColor);
		int animOffset = (8 - i) * 100 - introTimer * 24;
		if (animOffset > 0)
			animOffset = 0;
		g.drawString(title, 10 + animOffset, y, 0);
		g.setColor(-1);
		g.drawString(value, w - 10 - (animOffset / 2), y, Graphics.TOP | Graphics.RIGHT);
	}

	private final void Exit() {
		applause.Dispose();
		music.Stop();
		Nmania.Push(next == null ? new MainScreen() : next);
	}

	protected void keyReleased(int arg0) {
		Exit();
	}

	protected void pointerReleased(int arg0, int arg1) {
		Exit();
	}
}
