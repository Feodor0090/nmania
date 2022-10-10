package nmania.ui.ng;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

import symnovel.SNUtils;
import tube42.lib.imagelib.ColorUtils;

public class NmaniaDisplay extends GameCanvas implements Runnable {

	public NmaniaDisplay() {
		super(false);
		setFullScreenMode(true);
		g = getGraphics();
		g.setColor(0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(Font.getFont(0, 0, 8));
		try {
			logo = Image.createImage("/ui/nmania-logo-0.12x.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Graphics g;
	private IScreen[] stack = new IScreen[8];
	private int top = -1;
	private IOverlay overlay = null;

	// drawing vars
	static Font header = Font.getFont(0);
	int headerH = header.getHeight();
	static Font buttons = Font.getFont(9);
	private float leftButtonContract = 0;
	private float rightButtonContract = 0;
	private float leftButtonState = 0f;
	private float rightButtonState = 0f;
	Image logo;
	int w;
	int h;
	long time = System.currentTimeMillis();

	public void run() {
		while (true) {
			long delta = System.currentTimeMillis() - time;
			time += delta;
			w = getWidth();
			h = getHeight();
			if (leftButtonState > 0f) {
				leftButtonState -= 0.004f * delta;
				if (leftButtonState <=0f)
					leftButtonState = 0f;
			}
			if (rightButtonState > 0f) {
				rightButtonState -= 0.004f * delta;
				if (rightButtonState <=0f)
					rightButtonState = 0f;
			}
			g.setColor(BG_COLOR);
			g.fillRect(0, 0, w, h);
			DrawButtons();
			DrawHeader();
			flushGraphics();
		}
	}

	int keysH = buttons.getHeight() * 2, keysH2 = buttons.getHeight();
	int keysW = 20, keysW2 = 15;

	private void DrawButtons() {
		int clri = ColorUtils.blend(NEGATIVE_COLOR, PINK_COLOR, (int) (255 * leftButtonState));
		g.setColor(clri);
		g.fillTriangle(0, h - keysH, 0, h, w / 2 - keysW, h);
		g.fillTriangle(0, h - keysH, w / 2 - keysW2, h - keysH2, w / 2 - keysW, h);
		print(g, "PLAY ONLINE", 1, h - 1, clri, 0, Graphics.BOTTOM | Graphics.LEFT);

		clri = ColorUtils.blend(NEGATIVE_COLOR, PINK_COLOR, (int) (255 * rightButtonState));
		g.setColor(clri);
		g.fillTriangle(w, h - keysH, w, h, w / 2 + keysW, h);
		g.fillTriangle(w, h - keysH, w / 2 + keysW2, h - keysH2, w / 2 + keysW, h);
		print(g, "BACK", w - 1, h - 1, clri, 0, Graphics.BOTTOM | Graphics.RIGHT);
	}

	private void DrawHeader() {
		for (int i = 0; i <= headerH; i++) {
			g.setColor(ColorUtils.blend(NMANIA_COLOR, DARKER_COLOR, (i * 255 / headerH)));
			g.drawLine(0, i, w, i);
		}
		g.drawImage(logo, 0, -15, 0);
		g.setFont(header);
		print(g, "SELECT A BEATMAPSET", logo.getWidth(), 0, -1, BG_COLOR, 0);
	}

	protected void keyPressed(int k) {
		if (k == -6)
			leftButtonState = 1f;
		if (k == -7)
			rightButtonState = 1f;
	}

	public final static int NMANIA_COLOR = SNUtils.toARGB("0xffbd55");
	public final static int DARKER_COLOR = SNUtils.toARGB("0xffa311");
	public final static int BG_COLOR = SNUtils.toARGB("0x2a2115");
	public final static int PINK_COLOR = SNUtils.toARGB("0xe75480");
	public final static int NEGATIVE_COLOR = SNUtils.toARGB("0x0042aa");

	public static final void print(Graphics g, String s, int x, int y, int color, int bgColor, int anchor) {
		g.setColor(bgColor);
		g.drawString(s, x - 1, y - 1, anchor);
		g.drawString(s, x - 1, y + 1, anchor);
		g.drawString(s, x + 1, y - 1, anchor);
		g.drawString(s, x + 1, y + 1, anchor);
		g.setColor(color);
		g.drawString(s, x, y, anchor);
	}

	public Thread Start() {
		Thread t = new Thread(this);
		t.start();
		return t;
	}
}
