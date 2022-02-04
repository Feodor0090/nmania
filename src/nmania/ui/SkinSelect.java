package nmania.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.Nmania;
import nmania.Skin;
import tube42.lib.imagelib.ColorUtils;

public class SkinSelect extends Canvas {

	public SkinSelect() {
		setFullScreenMode(true);
		if (Nmania.skin == null)
			Nmania.skin = new Skin();
		repaint();
		(new Thread() {
			public void run() {
				try {
					while (introTimer > 0) {
						repaint();
						Thread.sleep(10);
						introTimer--;
					}
					repaint();
				} catch (Exception e) {
					return;
				}
			}
		}).start();
	}

	boolean useRich = false;
	boolean selectionFocused = true;
	int th = 80;
	int introTimer = 30;
	int outroTimer = 0;

	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		Font f = Font.getFont(0, 0, 8);
		th = f.getHeight();
		g.setColor(0);
		g.fillRect(0, 0, w, h);
		g.setColor(-1);

		g.drawString("Choose skin type", w / 2, -outroTimer, 17);
		int x1 = w / 4;
		int x2 = w * 3 / 4;
		if (outroTimer != 0) {
			x1 -= (w / 4 + 120) * outroTimer / 30;
			x2 += (w / 4 + 120) * outroTimer / 30;
		}

		g.drawString("Vector skin", x1, h / 2 - 55, 33);
		g.drawString("Rich skin", x2, h / 2 - 55, 33);

		drawVectorSkinIcon(g, x1, h / 2);
		drawRichSkinIcon(g, x2, h / 2);

		g.setColor(MainScreen.bgColor);
		for (int i = 0; i < 5; i++) {
			int x0 = useRich ? x2 : x1;
			x0 = x0 - 60;
			int y0 = h / 2 - 55 - th - 5 - 5;
			int h0 = 110 + th * 2 + 20;
			g.drawRect(x0 + i, y0 + i, 120 - i * 2, h0 - i * 2);
		}
		if (selectionFocused) {
			g.fillRect((useRich ? x2 : x1) - 50, h / 2 + 55, 100, th);
		}
		g.setColor(-1);
		g.drawString("settings", (useRich ? x2 : x1), h / 2 + 55, 17);
		g.setColor(MainScreen.bgColor);

		if (!selectionFocused && outroTimer == 0)
			g.fillRect(5, h - th - 5, w - 10, th);
		g.setColor(-1);
		g.drawString("Save & exit", w / 2, h - 5 + outroTimer, 33);

		if (introTimer > 0) {
			g.setColor(-1);
			int rw = w * introTimer / 60;
			g.fillRect(w / 2 - rw, 0, rw * 2, h);
		}

	}

	static void drawVectorSkinIcon(Graphics g, int x, int y) {
		g.setColor(MainScreen.bgColor);
		g.fillRect(x - 50, y - 45, 50, 20);
		g.fillRect(x, y - 15, 50, 20);
		g.fillRect(x - 50, y + 15, 50, 20);
		g.setColor(-1);
		g.drawLine(x - 50, y - 50, x - 50, y + 50);
		g.drawLine(x, y - 50, x, y + 50);
		g.drawLine(x + 50, y - 50, x + 50, y + 50);
		g.drawLine(x - 50, y + 50, x + 50, y + 50);

	}

	static void drawRichSkinIcon(Graphics g, int x, int y) {
		for (int i = x - 50; i < x + 49; i++) {
			int bl = Math.abs(i - x);
			g.setColor(ColorUtils.blend(0, MainScreen.bgColor, bl * 255 / 50));
			g.drawLine(i, y - 50, i, y + 50);
		}
	}

	protected void keyPressed(int k) {
		if (k == -3 || k == -4 || k == '4' || k == '6') {
			if (selectionFocused) {
				useRich = !useRich;
			}
			repaint();
			return;
		}
		if (k == -1 || k == -2 || k == '2' || k == '8') {
			selectionFocused = !selectionFocused;
			repaint();
			return;
		}
		if (k == -5 || k == -6 || k == 32 || k == '5' || k == 10) {
			if (selectionFocused) {
				if (useRich) {
					Nmania.Push(new Alert("nmania", "Rich skin is not available yet. Check newer versions of the game.",
							null, AlertType.ERROR));
				} else {
					Nmania.Push(new VectorSkinSetup(this));
				}
			} else {
				Exit();
			}
		}
	}

	void Exit() {
		if (useRich) {
			Nmania.Push(new Alert("nmania", "Rich skin is not available yet. Check newer versions of the game.", null,
					AlertType.ERROR));
			return;
		}
		(new Thread() {
			public void run() {
				try {
					while (outroTimer <= 30) {
						repaint();
						Thread.sleep(10);
						outroTimer++;
					}
					repaint();
					Nmania.Push(new MainScreen());
				} catch (Exception e) {
					return;
				}
			}
		}).start();
	}

	protected void pointerPressed(int x, int y) {
		if (y > getHeight() - th * 2) {
			Exit();
		}
		if (x < getWidth() / 2) {
			if (useRich) {
				useRich = false;
			} else {
				Nmania.Push(new VectorSkinSetup(this));
			}
		} else {
			if (useRich) {
				Nmania.Push(new Alert("nmania", "Rich skin is not available yet. Check newer versions of the game.",
						null, AlertType.ERROR));
			} else {
				useRich = true;
			}
		}
	}

}
