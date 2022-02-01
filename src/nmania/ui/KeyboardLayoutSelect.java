package nmania.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.Nmania;
import nmania.Settings;

public class KeyboardLayoutSelect extends Canvas {

	public KeyboardLayoutSelect(Displayable prev) {
		setFullScreenMode(true);
		this.prev = prev;
	}

	private final Displayable prev;
	int iy;
	int th;

	protected void paint(Graphics g) {
		int h = getHeight();
		Font f = Font.getFont(0, 0, 8);
		th = f.getHeight();
		g.setColor(0);
		g.fillRect(0, 0, getWidth(), h);
		iy = (h - th * items.length) / 2;
		g.setColor(MainScreen.bgColor);
		g.fillRect(5, iy + th * selected, getWidth() - 10, th);
		g.setColor(-1);
		for (int i = 0; i < items.length; i++) {
			g.drawString(items[i], 10, iy + th * i, 0);
			if (i > 0 && Settings.keyLayout[i - 1] == null) {
				g.drawString("not set", getWidth() - 10, iy + th * i, 24);
			}
		}
	}

	protected void keyPressed(int k) {
		if (k == -1 || k == '2') {
			// up
			selected--;
			if (selected < 0)
				selected = 0;
		} else if (k == -2 || k == '8') {
			// down
			selected++;
			if (selected >= items.length)
				selected = items.length - 1;
		} else if (k == -5 || k == -6 || k == 32 || k == '5') {
			if (selected == 0) {
				Settings.Save();
				Nmania.Push(prev);
				return;
			} else {
				Nmania.Push(new KeyboardSetup(selected, this));
			}
		}
		repaint();
	}

	protected void pointerPressed(int x, int y) {
		y -= iy;
		if (y < th) {
			Settings.Save();
			Nmania.Push(prev);
			return;
		}
		for (int i = 1; i <= 10; i++) {
			y -= th;
			if (y < th) {
				Nmania.Push(new KeyboardSetup(i, this));
				return;
			}
		}
	}

	int selected = 0;
	String[] items = new String[] { "<<< back", "1K", "2K", "3K", "4K", "5K", "6K", "7K", "8K", "9K", "10K" };
}
