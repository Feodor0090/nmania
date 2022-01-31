package nmania.ui;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;

import nmania.NmaniaApp;
import symnovel.SNUtils;

public class MainScreen extends GameCanvas implements Runnable {

	public MainScreen() {
		super(false);
		setFullScreenMode(true);
		(new Thread(this)).start();
	}

	private Graphics g;
	private boolean needThread;
	public static int bgColor = SNUtils.toARGB("0xffbd55");
	private Image logo, menu;
	int state = -2;

	protected void keyPressed(int k) {
		if (state == 0) {
			state = 1;
			return;
		}
		if (state == 2) {
			if (k == -5) {
				state = 4;
				action = 1;
				return;
			}
			if (k == -6) {
				state = 4;
				action = 2;
				return;
			}
			if (k == -7) {
				state = 5;
				return;
			}
		}
	}

	protected void pointerReleased(int arg0, int arg1) {
		if (state == 0) {
			state = 1;
			return;
		}
	}

	int action = 0;

	protected void pointerPressed(int x, int y) {
		if (state == 2) {
			x -= (getWidth() - menu.getWidth());
			y -= (getHeight() - menu.getHeight());
			x *= mul;
			y *= mul;
			if (x < 210) {
				if (y > 180) {
					// skin
					action = 3;
				} else {
					// sets
					action = 2;
				}
				state = 4;
				return;
			} else if (x > 640 - 210) {
				if (y > 180) {
					// exit
					state = 5;
					return;
				} else {
					// инфа
				}
			} else {
				state = 4;
				action = 1;
				return;
			}
		}
	}

	private void Open() {
		switch (action) {
		case 1:
			Play();
			break;
		case 2:
			NmaniaApp.Push(new SettingsScreen());
			break;
		default:
			break;
		}
	}

	/**
	 * Launches song select.
	 */
	private void Play() {
		TextBox box = new TextBox("Number of keys (2-10):", "4", 2, TextField.NUMERIC);
		Command ok = new Command("Start", Command.OK, 1);
		box.addCommand(ok);
		box.setCommandListener(NmaniaApp.inst);
		NmaniaApp.inst.box = box;
		Display.getDisplay(NmaniaApp.inst).setCurrent(box);
	}

	float mul;

	public void run() {
		state = -1;
		needThread = true;
		g = getGraphics();
		int w = getWidth();
		int h = getHeight();
		int l = (w > h) ? w : h;
		g.setColor(0);
		g.fillRect(0, 0, w, h);

		String suffix;
		if (w >= 1280 && h >= 720) {
			suffix = "1x";
			mul = 0.5f;
		} else if (w >= 640 && h >= 360) {
			suffix = "0.5x";
			mul = 1f;
		} else if (w >= 320 && h >= 180) {
			suffix = "0.25x";
			mul = 2f;
		} else {
			suffix = "0.12x";
			mul = 4f;
		}
		try {
			logo = Image.createImage("/ui/nmania-logo-" + suffix + ".png");
			menu = Image.createImage("/ui/menu-" + suffix + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		long startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			int arcSize = (int) ((now - startTime) * l / 500);
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(-1);
			int arcSize = (int) ((now - startTime) * Math.min(w, h) / 500) / 2;
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(logo, w / 2, h / 2, 3);
			g.setColor(-1);
			int arcSize = (int) ((500 - (now - startTime)) * Math.min(w, h) / 500) / 2;
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		state = 0;
		while (needThread && state == 0) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(logo, w / 2, h / 2, 3);
			flushGraphics();
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(-1);
			int arcSize = (int) ((now - startTime) * l / 500);
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		state = 2;
		startTime = System.currentTimeMillis();
		while (needThread && state == 2) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(menu, w / 2, h / 2, 3);
			if (now - startTime < 1000) {
				g.setColor(-1);
				g.fillRect(0, 0, w, (int) (h * (1000 - (now - startTime)) / 1000));
			}
			flushGraphics();
		}
		if (state == 4) {
			startTime = System.currentTimeMillis();
			// play
			while (true) {
				long now = System.currentTimeMillis();
				g.setColor(0);
				g.fillRect(0, 0, w, (int) (h * (now - startTime) / 500));
				flushGraphics();
				if (now - startTime > 500) {
					Open();
					return;
				}
			}
		}
		if (state == 5) {
			startTime = System.currentTimeMillis();
			// exit
			while (true) {
				long now = System.currentTimeMillis();
				g.setColor(-1);
				g.fillRect(0, 0, (int) (w * (now - startTime) / 1000), h);
				flushGraphics();
				if (now - startTime > 1000) {
					NmaniaApp.exit();
					return;
				}
			}
		}
	}
}
