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

	public SettingsScreen() {
		setFullScreenMode(true);
	}

	private final Command dimOk = new Command("OK", Command.OK, 1);
	private final Command scrollOk = new Command("OK", Command.OK, 1);

	protected void paint(Graphics g) {
		int h = getHeight();
		Font f = Font.getFont(0, 0, 8);
		int th = f.getHeight();
		g.setColor(0);
		g.fillRect(0, 0, getWidth(), h);
		int y = (h - th * items.length) / 2;
		g.setColor(MainScreen.bgColor);
		g.fillRect(5, y + th * selected, getWidth() - 10, th);
		g.setColor(-1);
		g.drawString("nmania settings", getWidth() / 2, 0, Graphics.HCENTER | Graphics.TOP);
		for (int i = 0; i < items.length; i++) {
			g.drawString(items[i], 10, y + th * i, 0);
		}
		g.drawString(((int) (Settings.bgDim * 100)) + "%", getWidth() - 10, y + th, Graphics.TOP | Graphics.RIGHT);
		drawCheckbox(g, Settings.hitSamples, y + th * 2, th);
		drawCheckbox(g, Settings.gameplaySamples, y + th * 3, th);
		g.drawString("x" + Settings.speedDiv, getWidth() - 10, y + th * 4, Graphics.TOP | Graphics.RIGHT);
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
				selected = 0;
		} else if (k == -2 || k == '8') {
			// down
			selected++;
			if (selected >= items.length)
				selected = items.length - 1;
		} else if (k == -5 || k == -6 || k == 32 || k == '5') {
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
				TextBox box1 = new TextBox("Speed", "" + Settings.speedDiv, 1, TextField.NUMERIC);
				box1.addCommand(scrollOk);
				box1.setCommandListener(this);
				Display.getDisplay(Nmania.inst).setCurrent(box1);
				break;
			case 5:
				Settings.Save();
				Nmania.Push(new MainScreen());
				break;
			default:
				break;
			}
		}
		repaint();
	}

	int selected = 0;
	String[] items = new String[] { "Gameplay bindings", "Background dim", "Enable hitsounds", "Enable feedback sounds",
			"Scroll speed", "<<< back" };

	public void commandAction(Command c, Displayable d) {
		if (d instanceof TextBox) {
			if (c == dimOk) {
				Settings.bgDim = Integer.parseInt(((TextBox) d).getString()) / 100f;
			} else if (c == scrollOk) {
				Settings.speedDiv = Integer.parseInt(((TextBox) d).getString());
			}
			Nmania.Push(this);
		}
	}

}
