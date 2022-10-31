package nmania.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.Nmania;
import nmania.Settings;

public final class KeyboardSetup extends Canvas {

	public KeyboardSetup(int columns, Displayable prev) {
		this.columns = columns;
		keys = new int[columns + 1];
		this.prev = prev;
		setFullScreenMode(true);
	}

	private final int columns;
	private int currentColumn = 0;
	private final int[] keys;
	private final Displayable prev;
	private boolean touched = false;

	protected void keyPressed(int k) {
		if (currentColumn >= keys.length)
			return;
		keys[currentColumn] = k;
		currentColumn++;
		if (currentColumn > columns) {
			Settings.keyLayout[columns - 1] = keys;
			Display.getDisplay(Nmania.inst).setCurrent(prev);
		} else
			repaint();
	}
	
	protected void pointerPressed(int aX, int aY) {
		if (touched) {
			for (int i = 0; i < columns; i++) {
				keys[i] = 0;
			}
			keys[columns] = -7;
			Settings.keyLayout[columns - 1] = keys;
			Nmania.Push(prev);
		} else {
			touched = true;
			repaint();
		}
	}

	protected final void paint(Graphics g) {
		final int w = getWidth();
		final int h = getHeight();
		final Font small = Font.getFont(0, 0, 8);
		final Font large = Font.getFont(0, 0, 16);

		// bg
		g.setColor(0);
		g.fillRect(0, 0, w, h);

		// top text
		g.setColor(-1);
		g.setFont(large);
		g.drawString("keybinds setup (" + columns + "K)", w / 2, 0, 17);
		String keyCapt;
		switch (currentColumn) {
		case 0:
			keyCapt = "1st";
			break;
		case 1:
			keyCapt = "2nd";
			break;
		case 2:
			keyCapt = "3rd";
			break;
		default:
			keyCapt = (currentColumn + 1) + "th";
			break;
		}
		if (currentColumn == columns) {
			keyCapt = "pause/quit";
		} else {
			keyCapt = keyCapt + " column";
		}
		g.setFont(small);
		int lh = large.getHeight();
		int sh = small.getHeight();
		g.drawString("press a key for the " + keyCapt, w / 2, lh, 17);
		if (touched) {
			g.drawString("tap one more time", w / 2, lh + sh, 17);
			g.drawString("to use touchscreen", w / 2, lh + sh + sh, 17);
		} else {
			g.drawString("tap to use touch", w / 2, lh + sh, 17);
		}

		// cols
		int colW = w / columns;
		int sfh = small.getHeight();
		for (int i = 0; i < columns; i++) {
			g.setColor(-1);
			g.drawRect(i * colW, h - sfh * 3, colW - 1, sfh * 3 - 1);
			if (i < currentColumn) {
				g.setColor(0, 64, 0);
				g.fillRect(i * colW + 1, h - sfh * 3 + 1, colW - 1, sfh * 3 - 1);
				g.setColor(-1);
				String key;
				int k = keys[i];
				if (k == 32) {
					key = "space";
				} else if (k > 32 && k <= 255) {
					char c = (char) k;
					key = String.valueOf(c);
				} else {
					key = getKeyName(k);
					if (key == null || key.length() == 0) {
						key = "key" + k;
					}
				}
				g.drawString(key, i * colW + (colW / 2), h - sfh, Graphics.HCENTER | Graphics.BOTTOM);
			} else if (i == currentColumn) {
				g.setColor(255, 255, 0);
				g.fillRect(i * colW + 1, h - sfh * 3 + 1, colW - 1, sfh * 3 - 1);
			}
		}
		if (currentColumn == columns) {
			g.setColor(255, 255, 0);
			g.fillRect(0, h - sfh * 4, w, sfh);
		}
	}

}
