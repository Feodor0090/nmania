package nmania.ui.ng;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

import nmania.Nmania;
import symnovel.SNUtils;
import tube42.lib.imagelib.ColorUtils;

public class NmaniaDisplay extends GameCanvas implements Runnable, IDisplay {

	public NmaniaDisplay(IScreen first) {
		super(false);
		setFullScreenMode(true);
		stack[0] = first;
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
	private IScreen[] stack = new IScreen[10];
	private int top = 0;
	private IOverlay overlay = null;

	// drawing vars
	static Font header = Font.getFont(0, 0, 0);
	int headerH = header.getHeight();
	static Font buttons = Font.getFont(0, 1, 8);
	private float leftButtonContract = 1f;
	private float leftButtonState = 0f;
	private String lastValidLeftButton = "";
	private boolean leftButtonActive = false;
	private float rightButtonState = 0f;
	Image logo;
	int w;
	int h;
	long time = System.currentTimeMillis();
	long trFrw = -1;
	boolean cycle = true;

	public void run() {
		while (cycle) {
			long delta = System.currentTimeMillis() - time;
			time += delta;
			w = getWidth();
			h = getHeight();
			if (leftButtonState > 0f) {
				leftButtonState -= 0.004f * delta;
				if (leftButtonState <= 0f)
					leftButtonState = 0f;
			}
			if (rightButtonState > 0f) {
				rightButtonState -= 0.004f * delta;
				if (rightButtonState <= 0f)
					rightButtonState = 0f;
			}
			{
				String o = stack[top].GetOption();
				if (o == null) {
					leftButtonActive = false;
				} else {
					lastValidLeftButton = o;
					leftButtonActive = true;
				}
			}
			if (leftButtonActive) {
				if (leftButtonContract < 1f)
					leftButtonContract += delta * 0.002f;
				if (leftButtonContract > 1f)
					leftButtonContract = 1f;
			} else {
				if (leftButtonContract > 0f)
					leftButtonContract -= delta * 0.002f;
			}

			if (trFrw == -1) {
				g.setColor(BG_COLOR);
				g.fillRect(0, 0, w, h);
				g.translate(0, headerH + 10);
				stack[top].Paint(g, w, h - headerH - 10 - keysH);
				g.translate(0, -g.getTranslateY());
				DrawButtons();
				DrawHeader(stack[top].GetTitle());
				g.drawImage(logo, w - logo.getWidth(), 0, 0);
			} else {
				PlayForwardTransition((time - trFrw) / 250f, stack[top - 1], stack[top]);
			}
			flushGraphics();
		}
	}

	int keysH = buttons.getHeight() * 2, keysH2 = buttons.getHeight();
	int keysW = 20, keysW2 = 15;

	private static float clamp1(float val) {
		if (val < 0f)
			return 0f;
		if (val > 1f)
			return 1f;
		return val;
	}

	private static int lerp(int a, int b, float p) {
		return (int) (a + p * (b - a));
	}

	/**
	 * 0-1 screen shifting to left 1-2 triangles pass 2-3 fade 3-4 opening to
	 * top-bottom
	 * 
	 * @param progress
	 * @param prev
	 * @param next
	 */
	public void PlayForwardTransition(float progress, IScreen prev, IScreen next) {
		if (progress < 1f) {
			g.setColor(BG_COLOR);
			g.fillRect(0, 0, w, h);
			g.translate((int) (-w * progress), headerH + 10);
			prev.Paint(g, w, h - headerH - 10 - keysH);
			g.translate(-g.getTranslateX(), -g.getTranslateY());
			DrawHeader(prev.GetTitle());
			g.setColor(NMANIA_COLOR);
			int fw = (int) (w * progress);
			g.fillRect(w - fw, 0, fw, h);
			DrawButtons();
			g.drawImage(logo, lerp(w - logo.getWidth(), w / 2 - logo.getWidth() / 2, progress),
					lerp(0, h / 2 - logo.getHeight() / 2, progress), 0);
			return;
		}
		if (progress < 2f) {
			int fh = (int) ((progress - 1f) * (h + 40)) - 40;
			g.setColor(NMANIA_COLOR);
			g.fillRect(0, fh, w / 2, h - fh);
			g.fillRect(w / 2, 0, w / 2, h - fh);

			g.setColor(PINK_COLOR);
			g.fillRect(0, 0, w / 2, fh);
			g.fillTriangle(0, fh, w / 2, fh, w / 4, fh + 40);

			g.setColor(NEGATIVE_COLOR);
			g.fillRect(w / 2, h - fh, w / 2, fh);
			g.fillTriangle(w / 2, h - fh, w, h - fh, w * 3 / 4, h - fh - 40);

			DrawButtons();
			g.drawImage(logo, w / 2, h / 2, Graphics.VCENTER | Graphics.HCENTER);
			return;
		}
		if (progress < 3f) {
			g.setColor(ColorUtils.blend(DARKER_COLOR, PINK_COLOR, (int) ((progress - 2f) * 255)));
			g.fillRect(0, 0, w / 2, h);
			g.setColor(ColorUtils.blend(DARKER_COLOR, NEGATIVE_COLOR, (int) ((progress - 2f) * 255)));
			g.fillRect(w / 2, 0, w / 2, h);
			DrawButtons();
			g.drawImage(logo, w / 2, h / 2, Graphics.VCENTER | Graphics.HCENTER);
			return;
		}
		if (progress < 4f) {
			g.setColor(BG_COLOR);
			g.fillRect(0, 0, w, h);
			g.translate(0, headerH + 10);
			next.Paint(g, w, h - headerH - 10 - keysH);
			g.translate(0, -g.getTranslateY());
			DrawHeader(next.GetTitle());
			int fw = (int) ((1f - (progress - 3f)) * (w / 2));
			g.setColor(DARKER_COLOR);
			g.fillRect(0, 0, fw, h);
			g.fillRect(w - fw, 0, fw, h);
			DrawButtons();
			g.drawImage(logo, lerp(w - logo.getWidth(), w / 2 - logo.getWidth() / 2, 1f - (progress - 3f)),
					lerp(0, h / 2 - logo.getHeight() / 2, 1f - (progress - 3f)), 0);
			return;
		}
		g.translate(0, headerH + 10);
		next.Paint(g, w, h - headerH - 10 - keysH);
		g.translate(0, -g.getTranslateY());
		DrawButtons();
		DrawHeader(next.GetTitle());
		g.drawImage(logo, w - logo.getWidth(), 0, 0);
		trFrw = -1;
	}

	private void DrawButtons() {
		g.setFont(buttons);
		int clri = ColorUtils.blend(NEGATIVE_COLOR, PINK_COLOR, (int) (255 * leftButtonState));
		g.setColor(clri);
		int lkh = (int) (keysH * clamp1(leftButtonContract * 2f));
		int lkh2 = (int) (keysH2 * clamp1(leftButtonContract * 2f - 1f));
		g.fillTriangle(0, h - lkh, 0, h, w / 2 - keysW, h);
		g.fillTriangle(0, h - lkh, w / 2 - keysW2, h - lkh2, w / 2 - keysW, h);
		print(g, lastValidLeftButton, 1, (int) (h - 1 + keysH2 * (1f - leftButtonContract)), clri, 0,
				Graphics.BOTTOM | Graphics.LEFT);

		clri = ColorUtils.blend(NEGATIVE_COLOR, PINK_COLOR, (int) (255 * rightButtonState));
		g.setColor(clri);
		g.fillTriangle(w, h - keysH, w, h, w / 2 + keysW, h);
		g.fillTriangle(w, h - keysH, w / 2 + keysW2, h - keysH2, w / 2 + keysW, h);
		print(g, top == 0 ? "QUIT" : "BACK", w - 1, h - 1, clri, 0, Graphics.BOTTOM | Graphics.RIGHT);
	}

	private void DrawHeader(String title) {
		for (int i = 0; i <= headerH; i++) {
			g.setColor(ColorUtils.blend(NMANIA_COLOR, DARKER_COLOR, (i * 255 / headerH)));
			g.drawLine(0, i, w, i);
		}
		g.setFont(header);
		print(g, title, 1, 1, -1, BG_COLOR, 0);
	}

	protected void keyPressed(int k) {
		if (trFrw != -1)
			return;
		if (k == -6) {
			leftButtonState = 1f;
			if (leftButtonActive)
				stack[top].OnOptionActivate(this);
			return;
		}
		if (k == -7) {
			rightButtonState = 1f;
			Back();
			return;
		}
		stack[top].OnKey(this, k);
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

	public void Back() {
		stack[top].OnExit(this);
		if (top == 0) {
			Nmania.exit();
		}
		stack[top + 1] = null;
		top--;
		stack[top].OnResume(this);
	}

	public void Push(IScreen s) {
		stack[top].OnPause(this);
		top++;
		stack[top] = s;
		s.OnEnter(this);
		trFrw = System.currentTimeMillis();
	}

	public void CloseOverlay() {
		// TODO Auto-generated method stub

	}

	public void OpenOverlay(IOverlay o) {
		// TODO Auto-generated method stub

	}

	public void SetBg(Image bg) {
		// TODO Auto-generated method stub

	}

	public void SetBg(int color) {
		// TODO Auto-generated method stub

	}
}
