package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.Nmania;
import nmania.Settings;
import tube42.lib.imagelib.ColorUtils;

public class SkinSelectScreen extends Screen {

	public SkinSelectScreen(IDisplay d) {
		Nmania.LoadSkin(true);
	}

	public String GetTitle() {
		return "SKIN TYPE SELECT";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "SETTINGS";
	}

	public void OnOptionActivate(IDisplay d) {
		if (Settings.rasterSkin) {
			d.Push(new RasterSkinSettings());
		} else {
			d.Push(new VectorSkinSettings());
		}
	}

	public boolean OnExit(IDisplay d) {
		Settings.Save();
		return false;
	}

	public void Paint(Graphics g, int w, int h) {
		g.setColor(NmaniaDisplay.PINK_COLOR);
		if (Settings.rasterSkin) {
			g.fillRoundRect(w / 2 + 10, 0, w / 2 - 20, h, 40, 40);
		} else {
			g.fillRoundRect(10, 0, w / 2 - 20, h, 40, 40);
		}

		drawVectorSkinIcon(g, w / 4, 55);
		drawRichSkinIcon(g, w * 3 / 4, 55);
		g.setFont(Font.getFont(0, 0, 8));
		NmaniaDisplay.print(g, "Vector", w / 4, h - 5, -1, 0, Graphics.BOTTOM | Graphics.HCENTER);
		NmaniaDisplay.print(g, "Raster", w * 3 / 4, h - 5, -1, 0, Graphics.BOTTOM | Graphics.HCENTER);
	}

	static void drawVectorSkinIcon(Graphics g, int x, int y) {
		g.setColor(NmaniaDisplay.NMANIA_COLOR);
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
			g.setColor(ColorUtils.blend(0, NmaniaDisplay.NMANIA_COLOR, bl * 255 / 50));
			g.drawLine(i, y - 50, i, y + 50);
		}
	}

	public void OnKey(IDisplay d, int k) {
		if (IsLeft(d, k) || IsRight(d, k)) {
			Settings.rasterSkin = !Settings.rasterSkin;
			Nmania.LoadSkin(true);
		}
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		if (s == 1) {
			Settings.rasterSkin = x > w / 2;
			Nmania.LoadSkin(true);
		}
	}

}
