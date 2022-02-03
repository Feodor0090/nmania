package nmania.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import nmania.Nmania;
import nmania.Settings;

public class SettingsScreen extends Canvas implements CommandListener {

	public SettingsScreen(boolean touch) {
		setFullScreenMode(true);
		selected = touch ? -1 : 0;
	}

	private final Command dimOk = new Command("OK", Command.OK, 1);
	private final Command scrollOk = new Command("OK", Command.OK, 1);
	private final Command dirOk = new Command("OK", Command.OK, 1);

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
		if (selected >= 0)
			g.fillRect(5, iy + th * selected, getWidth() - 10, th);
		g.setColor(-1);
		g.drawString("nmania settings", getWidth() / 2, 0, Graphics.HCENTER | Graphics.TOP);
		for (int i = 0; i < items.length; i++) {
			g.drawString(items[i], 10, iy + th * i, 0);
		}
		g.drawString(((int) (Settings.bgDim * 100)) + "%", getWidth() - 10, iy + th, Graphics.TOP | Graphics.RIGHT);
		drawCheckbox(g, Settings.hitSamples, iy + th * 2, th);
		drawCheckbox(g, Settings.gameplaySamples, iy + th * 3, th);
		drawCheckbox(g, Settings.keepMenu, iy + th * 4, th);
		g.setColor(-1);
		g.drawString("x" + Settings.speedDiv, getWidth() - 10, iy + th * 5, Graphics.TOP | Graphics.RIGHT);
	}

	void drawCheckbox(Graphics g, boolean ok, int y, int th) {
		g.setColor(-1);
		g.fillArc(getWidth() - th - 9, y + 1, th - 2, th - 2, 0, 360);
		if (ok) {
			g.setColor(0, 200, 0);
		} else {
			g.setColor(200, 0, 0);
		}
		g.fillArc(getWidth() - th - 5, y + 5, th - 10, th - 10, 0, 360);
	}

	protected void keyPressed(int k) {
		if (k == -1 || k == '2') {
			// up
			selected--;
			if (selected < 0)
				selected = items.length - 1;
		} else if (k == -2 || k == '8') {
			// down
			selected++;
			if (selected >= items.length)
				selected = 0;
		} else if (k == -5 || k == -6 || k == 32 || k == '5' || k == 10) {
			activateItem();
		} else if (k == -7) {
			Settings.Save();
			Nmania.Push(new MainScreen());
		}
		repaint();
	}

	protected void pointerPressed(int x, int y) {
		y -= iy;
		if (y < 0)
			return;
		for (int i = 0; i < items.length; i++) {
			if (y < th) {
				selected = i;
				activateItem();
				repaint();
				return;
			}
			y -= th;
		}
	}

	private void activateItem() {
		switch (selected) {
		case 0:
			Nmania.Push(new KeyboardLayoutSelect(this));
			break;
		case 1:
			TextBox box = new TextBox("Dim level", "" + Math.min(99, (int) (Settings.bgDim * 100)), 2,
					TextField.NUMERIC);
			box.addCommand(dimOk);
			box.setCommandListener(this);
			Display.getDisplay(Nmania.inst).setCurrent(box);
			break;
		case 2:
			Settings.hitSamples = !Settings.hitSamples;
			break;
		case 3:
			Settings.gameplaySamples = !Settings.gameplaySamples;
			break;
		case 4:
			Settings.keepMenu = !Settings.keepMenu;
			break;
		case 5:
			TextBox box1 = new TextBox("Speed", "" + Settings.speedDiv, 1, TextField.NUMERIC);
			box1.addCommand(scrollOk);
			box1.setCommandListener(this);
			Display.getDisplay(Nmania.inst).setCurrent(box1);
			break;
		case 6:
			Settings.drawCounters = !Settings.drawCounters;
			break;
		case 7:
			Settings.fullScreenFlush = !Settings.fullScreenFlush;
			break;
		case 8:
			TextBox box2 = new TextBox("Folder location", Settings.workingFolder, 100, TextField.ANY);
			box2.addCommand(dirOk);
			box2.setCommandListener(this);
			Nmania.Push(box2);
			break;
		case 9:
			Settings.Save();
			Nmania.Push(new MainScreen());
			break;
		default:
			break;
		}
	}

	int selected = 0;
	String[] items = new String[] { "Gameplay bindings", "Background dim", "Enable hitsounds", "Enable feedback sounds",
			"Keep UI during gameplay", "Scroll speed", "Draw counters", "Fullscreen flush", "Folder location",
			"<<< back" };

	public void commandAction(Command c, Displayable d) {
		if (d instanceof TextBox) {
			if (c == dimOk) {
				Settings.bgDim = Integer.parseInt(((TextBox) d).getString()) / 100f;
			} else if (c == scrollOk) {
				Settings.speedDiv = Integer.parseInt(((TextBox) d).getString());
			} else if (c == dirOk) {
				Settings.workingFolder = ((TextBox) d).getString();
			}
			Nmania.Push(this);
		}
	}

}
