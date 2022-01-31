package nmania;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class ResultsScreen extends Canvas {

	public ResultsScreen(ScoreController score, AudioController music, Image bg) {
		super();
		this.score = score;
		this.music = music;
		this.bg = bg;
		setFullScreenMode(true);
	}

	public final ScoreController score;
	public final AudioController music;
	public final Image bg;

	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		g.setColor(0);
		g.fillRect(0, 0, w, h);
		g.drawImage(bg, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);

		g.setColor(-1);
		g.setFont(Font.getFont(0, 0, 16));
		g.drawString("RESULTS", w / 2, 0, 17);
		g.setFont(Font.getFont(0, 0, 8));
		g.drawString("any key to proceed", w / 2, h, Graphics.BOTTOM | Graphics.HCENTER);

		int th = Font.getFont(0, 0, 8).getHeight();
		int y = (h - th * 9) / 2;
		drawRow(g, "Total score", -1, String.valueOf(score.currentHitScore), y, w);
		y += th;
		drawRow(g, "Max combo", -1, score.hits[0] == 0 ? "Full combo" : String.valueOf(score.maxCombo), y, w);
		y += th;
		drawRow(g, "Accuracy", -1, String.valueOf((score.GetAccuracy() / 100f) + "%"), y, w);
		y += th;
		for (int i = 0; i < 6; i++) {
			drawRow(g, Player.judgements[i], Player.judgementColors[i], String.valueOf(score.hits[i]), y, w);
			y += th;
		}
	}

	private final void drawRow(Graphics g, String title, int titleColor, String value, int y, int w) {
		g.setColor(titleColor);
		g.drawString(title, 10, y, 0);
		g.setColor(-1);
		g.drawString(value, w - 10, y, Graphics.TOP | Graphics.RIGHT);
	}

	private final void Exit() {
		music.Stop();
	}

	protected void keyReleased(int arg0) {
		Exit();
	}

	protected void pointerReleased(int arg0, int arg1) {
		Exit();
	}
}
