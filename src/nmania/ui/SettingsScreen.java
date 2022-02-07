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
		_this = this;
		Switch(main);
		repaint();
		this.touch = touch;
	}

	final SettingsScreen _this;

	final Command dimOk = new Command("OK", Command.OK, 1);
	final Command scrollOk = new Command("OK", Command.OK, 1);
	final Command dirOk = new Command("OK", Command.OK, 1);
	final Command offsetOk = new Command("OK", Command.OK, 1);
	final Command localeOk = new Command("OK", Command.OK, 1);

	int iy;
	int th;

	SettingsSection curr = null;
	SettingsSection prev;
	boolean switching;
	int switchOffset = 0;
	boolean touch;

	protected void paint(Graphics g) {
		g.setColor(0);
		g.fillRect(0, 0, getWidth(), getHeight());
		Font f = Font.getFont(0, 0, 8);
		th = f.getHeight();
		g.setFont(f);
		if (switching) {
			int x = (getWidth() * (40 - switchOffset)) / 20;
			g.translate(-x, 0);
			if (prev != null)
				paintSection(g, prev);
			g.translate(getWidth(), 0);
			g.setColor(MainScreen.bgColor);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.translate(getWidth(), 0);
			if (curr != null)
				paintSection(g, curr);
			g.translate(-g.getTranslateX(), 0);
		} else {
			paintSection(g, curr);
		}
	}

	private void paintSection(Graphics g, SettingsSection s) {
		int h = getHeight();
		String[] items = s.GetItems();
		iy = (h - th * items.length) / 2;
		g.setColor(MainScreen.bgColor);
		if (selected >= 0 && !switching)
			g.fillRect(5, iy + th * selected, getWidth() - 10, th);
		g.setColor(-1);
		g.drawString(s.GetTitle(), getWidth() / 2, 0, Graphics.HCENTER | Graphics.TOP);
		for (int i = 0; i < items.length; i++) {
			g.drawString(items[i], 10, iy + th * i, 0);
		}
		s.paint(g, iy, getWidth());
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
		touch = false;
		if (curr == null || switching)
			return;
		if (k == -1 || k == '2') {
			// up
			selected--;
			if (selected < 0)
				selected = curr.GetItems().length - 1;
		} else if (k == -2 || k == '8') {
			// down
			selected++;
			if (selected >= curr.GetItems().length)
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
		touch = true;
		if (curr == null || switching)
			return;
		y -= iy;
		if (y < 0)
			return;
		for (int i = 0; i < curr.GetItems().length; i++) {
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
		if (!switching)
			curr.OnSelect(selected);
	}

	int selected = 0;

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

	public void Switch(final SettingsSection ss) {
		switching = true;
		prev = curr;
		curr = ss;
		switchOffset = 40;
		(new Thread() {
			public void run() {
				try {
					while (switchOffset > 0) {
						repaint();
						Thread.sleep(10);
						switchOffset--;
					}
					selected = touch ? -1 : 0;
					if (ss == null)
						Nmania.Push(new MainScreen());
					else {
						switching = false;
						repaint();
					}

				} catch (Exception e) {
					return;
				}
			}
		}).start();
	}

	final SettingsSection main = new SettingsSection() {

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
			g.drawString("x" + Settings.speedDiv, getWidth() - 10, y + th * 4, Graphics.TOP | Graphics.RIGHT);
		}
	};
	final SettingsSection audio = new SettingsSection() {

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
	final SettingsSection system = new SettingsSection() {

		public void OnSelect(int i) {
			switch (i) {
			case 0:
				Settings.keepMenu = !Settings.keepMenu;
				break;
			case 1:
				Settings.drawCounters = !Settings.drawCounters;
				break;
			case 2:
				Settings.fullScreenFlush = !Settings.fullScreenFlush;
				break;
			case 3:
				Settings.profiler = !Settings.profiler;
				break;
			case 4:
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
			drawCheckbox(g, Settings.drawCounters, y + th, th);
			drawCheckbox(g, Settings.fullScreenFlush, iy + th * 2, th);
			drawCheckbox(g, Settings.profiler, iy + th * 3, th);
		}
	};
	final SettingsSection binds = new SettingsSection() {

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

	public abstract class SettingsSection {
		public abstract String GetTitle();

		public abstract String[] GetItems();

		public abstract void OnSelect(int i);

		public void paint(Graphics g, int y, int sw) {
		}
	}

}
