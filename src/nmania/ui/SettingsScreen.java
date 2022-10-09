package nmania.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import nmania.Nmania;
import nmania.Settings;

public class SettingsScreen extends MultisectionList implements CommandListener {

	public SettingsScreen(boolean touch) {
		super(touch);
		_this = this;
		Switch(main);
	}

	final SettingsScreen _this;

	final Command dimOk = new Command("OK", Command.OK, 1);
	final Command scrollOk = new Command("OK", Command.OK, 1);
	final Command dirOk = new Command("OK", Command.OK, 1);
	final Command offsetOk = new Command("OK", Command.OK, 1);
	final Command localeOk = new Command("OK", Command.OK, 1);

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

	public void commandAction(Command c, Displayable d) {
		if (d instanceof TextBox) {
			if (c == dimOk) {
				Settings.bgDim = Integer.parseInt(((TextBox) d).getString()) / 100f;
			} else if (c == scrollOk) {
				Settings.speedDiv = Integer.parseInt(((TextBox) d).getString());
			} else if (c == dirOk) {
				Settings.workingFolder = ((TextBox) d).getString();
			} else if (c == offsetOk) {
				Settings.gameplayOffset = Integer.parseInt(((TextBox) d).getString());
			} else if (c == localeOk) {
				Settings.locale = ((TextBox) d).getString();
			}
			Nmania.Push(this);
		}
	}

	final ListSection main = new ListSection() {

		public void OnSelect(int i) {
			switch (i) {
			case 0:
				Switch(binds);
				break;
			case 1:
				Switch(audio);
				break;
			case 2:
				Switch(system);
				break;
			case 3:
				TextBox box = new TextBox(items[3], "" + Math.min(99, (int) (Settings.bgDim * 100)), 2,
						TextField.NUMERIC);
				box.addCommand(dimOk);
				box.setCommandListener(_this);
				Display.getDisplay(Nmania.inst).setCurrent(box);
				break;
			case 4:
				TextBox box1 = new TextBox(items[4], "" + Settings.speedDiv, 1, TextField.NUMERIC);
				box1.addCommand(scrollOk);
				box1.setCommandListener(_this);
				Display.getDisplay(Nmania.inst).setCurrent(box1);
				break;
			case 5:
				TextBox box2 = new TextBox(items[5], Settings.workingFolder, 100, TextField.ANY);
				box2.addCommand(dirOk);
				box2.setCommandListener(_this);
				Nmania.Push(box2);
				break;
			case 6:
				TextBox box3 = new TextBox(items[6], Settings.locale, 6, TextField.ANY);
				box3.addCommand(localeOk);
				box3.setCommandListener(_this);
				Nmania.Push(box3);
				break;
			case 7:
				Settings.Save();
				Switch(null);
				break;
			}
		}

		final String[] items = Nmania.getStrings("sets_main");

		public String[] GetItems() {
			return items;
		}

		public String GetTitle() {
			return Nmania.commonText[20];
		}

		public void paint(Graphics g, int y, int sw) {
			g.setColor(-1);
			g.drawString(((int) (Settings.bgDim * 100)) + "%", getWidth() - 10, y + th * 3,
					Graphics.TOP | Graphics.RIGHT);
			g.drawString("1px/" + Settings.speedDiv + "ms", getWidth() - 10, y + th * 4, Graphics.TOP | Graphics.RIGHT);
		}
	};
	final ListSection audio = new ListSection() {

		public void OnSelect(int i) {
			switch (i) {
			case 0:
				Settings.hitSamples = !Settings.hitSamples;
				break;
			case 1:
				Settings.gameplaySamples = !Settings.gameplaySamples;
				break;
			case 2:
				Settings.useBmsSamples = !Settings.useBmsSamples;
				break;
			case 3:
				TextBox box = new TextBox(items[3], String.valueOf(Settings.gameplayOffset), 4, TextField.NUMERIC);
				box.addCommand(offsetOk);
				box.setCommandListener(_this);
				Nmania.Push(box);
				break;
			case 4:
				Switch(main);
				break;
			}
		}

		public String GetTitle() {
			return Nmania.commonText[21];
		}

		final String[] items = Nmania.getStrings("sets_audio");

		public String[] GetItems() {
			return items;
		}

		public void paint(Graphics g, int y, int sw) {
			drawCheckbox(g, Settings.hitSamples, y, th);
			drawCheckbox(g, Settings.gameplaySamples, y + th, th);
			drawCheckbox(g, Settings.useBmsSamples, y + th * 2, th);
			g.setColor(-1);
			g.drawString(Settings.gameplayOffset + "ms", getWidth() - 10, y + th * 3, 24);
		}
	};
	final ListSection system = new ListSection() {

		public void OnSelect(int i) {
			switch (i) {
			case 0:
				Settings.keepMenu = !Settings.keepMenu;
				break;
			case 1:
				Settings.drawHUD = !Settings.drawHUD;
				break;
			case 2:
				Settings.fullScreenFlush = !Settings.fullScreenFlush;
				break;
			case 3:
				Settings.profiler = !Settings.profiler;
				break;
			case 4:
				Settings.recordReplay = !Settings.recordReplay;
			case 5:
				Switch(main);
				break;
			}
		}

		public String GetTitle() {
			return Nmania.commonText[22];
		}

		final String[] items = Nmania.getStrings("sets_system");

		public String[] GetItems() {
			return items;
		}

		public void paint(Graphics g, int y, int sw) {
			drawCheckbox(g, Settings.keepMenu, y, th);
			drawCheckbox(g, Settings.drawHUD, y + th, th);
			drawCheckbox(g, Settings.fullScreenFlush, iy + th * 2, th);
			drawCheckbox(g, Settings.profiler, iy + th * 3, th);
		}
	};
	final ListSection binds = new ListSection() {

		final String[] items = new String[] { "1K", "2K", "3K", "4K", "5K", "6K", "7K", "8K", "9K", "10K", "<<< back" };

		public void OnSelect(int i) {
			if (i >= 0 && i <= 9) {
				Nmania.Push(new KeyboardSetup(i + 1, _this));
			} else if (i == 10) {
				Switch(main);
			}
		}

		public String GetTitle() {
			return "";
		}

		public String[] GetItems() {
			return items;
		}

		public void paint(Graphics g, int y, int sw) {
			for (int i = 0; i < 10; i++) {
				if (i > 0 && Settings.keyLayout[i] == null) {
					g.drawString(Nmania.commonText[23], getWidth() - 10, y + th * i, 24);
				}
			}
		}
	};

}
