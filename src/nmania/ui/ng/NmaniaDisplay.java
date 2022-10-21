package nmania.ui.ng;

import java.io.IOException;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

import nmania.Nmania;
import nmania.Settings;
import symnovel.SNUtils;
import tube42.lib.imagelib.ColorUtils;
import tube42.lib.imagelib.ImageFxUtils;
import tube42.lib.imagelib.ImageUtils;
import tube42.lib.imagelib.ImageFxUtils.PixelModifier;

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

	// drawing vars
	static Font header = Font.getFont(0, 0, 0);
	int headerH = header.getHeight();
	static Font buttons = Font.getFont(0, 1, 8);
	private float leftButtonContract = 1f;
	private float leftButtonState = 0f;
	private String lastValidLeftButton = "";
	private boolean leftButtonActive = false;
	private float rightButtonState = 0f;
	public static Image logo;
	int w;
	int h;
	long time = System.currentTimeMillis();
	long trFrw = -1, trBrw = -1;
	boolean cycle = true;
	boolean pause = false;
	Image bg;
	Thread th;

	public void run() {
		while (cycle) {
			if (pause) {
				try {
					Thread.sleep(Integer.MAX_VALUE*10L);
				} catch (InterruptedException e) {
					pause = false;
				}
				if (!cycle) {
					stack = null;
					g = null;
					bg = null;
					th = null;
					System.gc();
					return;
				}
			}
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

			if (trFrw != -1) {
				PlayForwardTransition((time - trFrw) / 250f, stack[top - 1], stack[top]);
			} else if (trBrw != -1) {
				PlayBackwardsTransition((time - trBrw) / 250f, stack[top + 1], stack[top]);
			} else {
				if (bg == null) {
					g.setColor(BG_COLOR);
					g.fillRect(0, 0, w, h);
				} else {
					g.drawImage(bg, 0, 0, 0);
				}
				g.translate(0, headerH + 10);
				stack[top].Paint(g, w, h - headerH - 10 - keysH);
				g.translate(0, -g.getTranslateY());
				DrawButtons();
				DrawHeader(stack[top].GetTitle());
				if (stack[top].ShowLogo())
					g.drawImage(logo, w - logo.getWidth(), 0, 0);
			}
			flushGraphics();
			if (throttle)
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
				}
		}
		stack = null;
		g = null;
		bg = null;
		th = null;
		System.gc();
	}

	public int keysH = buttons.getHeight() * 2, keysH2 = buttons.getHeight();
	int keysW = 20, keysW2 = 15;
	private boolean throttle;

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
			if (bg == null) {
				g.setColor(BG_COLOR);
				g.fillRect(0, 0, w, h);
			} else {
				g.drawImage(bg, 0, 0, 0);
			}
			g.translate((int) (-w * progress), headerH + 10);
			prev.Paint(g, w, h - headerH - 10 - keysH);
			g.translate(-g.getTranslateX(), -g.getTranslateY());
			DrawHeader(prev.GetTitle());
			g.setColor(NMANIA_COLOR);
			int fw = (int) (w * progress);
			g.fillRect(w - fw, 0, fw, h);
			DrawButtons();
			g.drawImage(logo, lerp(w - (prev.ShowLogo() ? logo.getWidth() : 0), w / 2 - logo.getWidth() / 2, progress),
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
			if (bg == null) {
				g.setColor(BG_COLOR);
				g.fillRect(0, 0, w, h);
			} else {
				g.drawImage(bg, 0, 0, 0);
			}
			g.translate(0, headerH + 10);
			next.Paint(g, w, h - headerH - 10 - keysH);
			g.translate(0, -g.getTranslateY());
			DrawHeader(next.GetTitle());
			int fw = (int) ((1f - (progress - 3f)) * (w / 2));
			g.setColor(DARKER_COLOR);
			g.fillRect(0, 0, fw, h);
			g.fillRect(w - fw, 0, fw, h);
			DrawButtons();
			g.drawImage(logo, lerp(w - (next.ShowLogo() ? logo.getWidth() : 0), w / 2 - logo.getWidth() / 2,
					1f - (progress - 3f)), lerp(0, h / 2 - logo.getHeight() / 2, 1f - (progress - 3f)), 0);
			return;
		}
		if (bg == null) {
			g.setColor(BG_COLOR);
			g.fillRect(0, 0, w, h);
		} else {
			g.drawImage(bg, 0, 0, 0);
		}
		g.translate(0, headerH + 10);
		next.Paint(g, w, h - headerH - 10 - keysH);
		g.translate(0, -g.getTranslateY());
		DrawButtons();
		DrawHeader(next.GetTitle());
		if (next.ShowLogo())
			g.drawImage(logo, w - logo.getWidth(), 0, 0);
		trFrw = -1;
	}

	public void PlayBackwardsTransition(float progress, IScreen top, IScreen target) {
		if (progress < 1f) {
			if (bg == null) {
				g.setColor(BG_COLOR);
				g.fillRect(0, 0, w, h);
			} else {
				g.drawImage(bg, 0, 0, 0);
			}
			g.translate(0, headerH + 10);
			top.Paint(g, w, h - headerH - 10 - keysH);
			g.translate(-g.getTranslateX(), -g.getTranslateY());
			DrawHeader(top.GetTitle());
			if (top.ShowLogo())
				g.drawImage(logo, w - logo.getWidth(), 0, 0);
			int fh = (int) (h * progress);
			g.setColor(DARKER_COLOR);
			g.fillRect(0, 0, w / 2, fh);
			g.fillRect(w / 2, h - fh, w / 2, fh);
			DrawButtons();
			return;
		}
		if (progress < 2f) {
			if (bg == null) {
				g.setColor(BG_COLOR);
				g.fillRect(0, 0, w, h);
			} else {
				g.drawImage(bg, 0, 0, 0);
			}
			g.translate((int) (-w * (2f - progress)), headerH + 10);
			target.Paint(g, w, h - headerH - 10 - keysH);
			g.translate(-g.getTranslateX(), -g.getTranslateY());
			DrawHeader(target.GetTitle());
			if (top.ShowLogo())
				g.drawImage(logo, w - logo.getWidth(), 0, 0);
			int fw = (int) (w * (2f - progress));
			g.setColor(DARKER_COLOR);
			g.fillRect(w - fw, 0, fw, h);
			DrawButtons();
			return;
		}
		if (bg == null) {
			g.setColor(BG_COLOR);
			g.fillRect(0, 0, w, h);
		} else {
			g.drawImage(bg, 0, 0, 0);
		}
		g.translate(0, headerH + 10);
		target.Paint(g, w, h - headerH - 10 - keysH);
		g.translate(0, -g.getTranslateY());
		DrawButtons();
		DrawHeader(target.GetTitle());
		if (target.ShowLogo())
			g.drawImage(logo, w - logo.getWidth(), 0, 0);
		trBrw = -1;
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
		if (pause)
			ResumeRendering();
		if (trFrw != -1)
			return;
		if (trBrw != -1)
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
		th = new Thread(this);
		th.start();
		return th;
	}

	public void Back() {
		stack[top].OnExit(this);
		if (top == 0) {
			cycle = false;
			pause = false;
			Nmania.exit();
			return;
		}
		stack[top + 1] = null;
		top--;
		stack[top].OnResume(this);
		trBrw = System.currentTimeMillis();
	}

	public void Push(IScreen s) {
		stack[top].OnPause(this);
		top++;
		stack[top] = s;
		s.OnEnter(this);
		trFrw = System.currentTimeMillis();
	}

	public void SetBg(Image bg) {
		this.bg = CreateBackground(bg);
	}

	public Displayable GetDisplayable() {
		return this;
	}

	public void PauseRendering() {
		pause = true;
		th.setPriority(Thread.MIN_PRIORITY);
	}

	public void ResumeRendering() {
		if (!pause)
			return;
		pause = false;
		th.interrupt();
		th.setPriority(Thread.NORM_PRIORITY);
	}

	public void Throttle(boolean e) {
		throttle = e;
		if (e)
			th.setPriority(Thread.MIN_PRIORITY);
		else
			th.setPriority(Thread.NORM_PRIORITY);
	}

	/**
	 * Creates background from arbitary image.
	 * 
	 * @param raw Image to work on.
	 * @return Image to use, null if solid color must be used instead.
	 */
	public Image CreateBackground(Image raw) {
		if (raw == null)
			return null;
		if (Settings.bgDim >= 1f)
			return null;
		try {
			int scrW = getWidth(), scrH = getHeight();
			final float screenAR = scrW / (float) scrH;
			final float bgAR = raw.getWidth() / (float) raw.getHeight();
			int tw;
			int th;
			if (screenAR == bgAR) {
				tw = scrW;
				th = scrH;
			} else if (screenAR > bgAR) {
				// screen is wider
				tw = scrW;
				th = (int) (tw / bgAR);
			} else {
				// screen is taller
				th = scrH;
				tw = (int) (th * bgAR);
			}
			raw = ImageUtils.resize(raw, tw, th, Settings.bgDim <= 0.95f, false);
			if (tw != scrW || th != scrH) {
				int x0 = (tw - scrW) / 2;
				int y0 = (th - scrH) / 2;
				raw = ImageUtils.crop(raw, x0, y0, x0 + scrW, y0 + scrH);
			}
			if (Settings.bgDim > 0.01f) {
				raw = ImageFxUtils.applyModifier(raw, new PixelModifier() {
					final int blendLevel = (int) ((1f - Settings.bgDim) * 255);

					public void apply(int[] p, int[] o, int count, int y) {
						for (int i = 0; i < p.length; i++) {
							o[i] = ColorUtils.blend(p[i], BG_COLOR, blendLevel);
						}
					}
				});
			}
			return raw;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public void Destroy() {
		 cycle = false;
		 ResumeRendering();
	}
}
