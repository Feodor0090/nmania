package nmania.ui;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

import nmania.Nmania;
import nmania.Settings;
import nmania.Skin;
import symnovel.SNUtils;

public class MainScreen extends GameCanvas implements Runnable {

	public MainScreen() {
		super(false);
		setFullScreenMode(true);
		g = getGraphics();
		g.setColor(0);
		g.fillRect(0, 0, getWidth(), getHeight());
		(new Thread(this, "Main menu repainter")).start();
	}

	private Graphics g;
	boolean needThread;
	public static int bgColor = SNUtils.toARGB("0xffbd55");
	private Image logo, menu;
	int state = -2;
	int selected = 0;

	protected void keyPressed(int k) {
		lastInputIsTouch = false;
		if (state == 0) {
			state = 1;
			return;
		}
		if (state == 2) {
			if (k == -3) {
				selected--;
				if (selected < -2)
					selected = 2;
				return;
			}
			if (k == -4) {
				selected++;
				if (selected > 2)
					selected = -2;
				return;
			}
			if (k == -5 || k == 10 || k == 32) {
				if (selected == 2) {
					state = 5;
				} else {
					state = 4;
					action = (new int[] { 3, 2, 1, 4 })[selected + 2];
				}
				return;
			}
			if (k == '5') {
				state = 4;
				action = 1;
				return;
			}
			if (k == '1') {
				state = 4;
				action = 2;
				return;
			}
			if (k == '7') {
				state = 4;
				action = 3;
				return;
			}
			if (k == '3') {
				state = 4;
				action = 4;
				return;
			}
			if (k == '9') {
				state = 5;
				return;
			}
		}
	}

	protected void pointerReleased(int arg0, int arg1) {
		lastInputIsTouch = true;
		if (state == 0) {
			state = 1;
			return;
		}
	}

	int action = 0;
	boolean lastInputIsTouch;

	protected void pointerPressed(int x, int y) {
		lastInputIsTouch = true;
		if (state == 2) {
			// attempt to clamp the touch into 640x360
			x -= getWidth() / 2;
			y -= getHeight() / 2;
			x *= mul;
			y *= mul;
			x += 320;
			y += 180;
			// zones
			if (x < 210) {
				state = 4;
				if (y > 180) {
					// skin
					action = 3;
					return;
				}
				// sets
				action = 2;
				return;
			} else if (x > 640 - 210) {
				if (y > 180) {
					// exit
					state = 5;
					return;
				}
				// info
				state = 4;
				action = 4;
				return;
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
			return;
		case 2:
			Nmania.Push(new SettingsScreen(lastInputIsTouch));
			break;
		case 3:
			Nmania.Push(new SkinSelect());
			break;
		case 4:
			Nmania.Push(new InfoScreen(lastInputIsTouch));
			break;
		default:
			break;
		}
		needThread = false;
	}

	/**
	 * Launches song select.
	 */
	private void Play() {
		(new Thread(new Runnable() {
			public void run() {
				Thread.yield();
				try {
					Nmania.LoadManager(Settings.workingFolder);
					if (Nmania.skin == null) {
						Nmania.skin = new Skin();
					}
					Nmania.Push(new BeatmapSetsList(Nmania.bm));
					if (Nmania.skin.rich) {
						try {
							Nmania.skin.LoadRich(false);
						} catch (IllegalStateException e) {
							Thread.yield();
							Alert a = new Alert("nmania",
									"Failed to load your rich skin. A vector one will be used. Visit skinning menu to learn what went wrong.",
									null, AlertType.ERROR);
							a.setTimeout(Alert.FOREVER);
							Nmania.Push(a);
						}
					}
					needThread = false;
				} catch (Exception e) {
					needThread = false;
					e.printStackTrace();
					Nmania.Push(new InfoScreen(lastInputIsTouch));
					Thread.yield();
					Thread.yield();
					Alert a = new Alert("nmania",
							"Failed to start game. Check if you have working folder (default one is C:/Data/Sounds/nmania/). Refer to help sections to get more information.",
							null, AlertType.ERROR);
					a.setTimeout(Alert.FOREVER);
					Nmania.Push(a);
				}
			}
		}, "BMSL loader")).start();
	}

	float mul;

	/**
	 * Thread responsible for animations.
	 */
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

		// - > LOGO
		long startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			int arcSize = (int) ((now - startTime) * l / 400);
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 400)
				break;
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(-1);
			int rw = (int) (w * (now - startTime) / 400);
			g.fillRect(0, 0, rw, h);
			g.fillRect(w - rw, 0, rw, h);
			flushGraphics();
			if (now - startTime > 200)
				break;
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(logo, w / 2, h / 2, 3);
			g.setColor(-1);
			int arcSize = (int) ((400 - (now - startTime)) * Math.sqrt(w * w + h * h) / 400) / 2;
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 400)
				break;
		}
		state = 0;
		Font f = Font.getFont(0, 0, 8);
		g.setFont(f);
		// LOGO
		while (needThread && state == 0) {
			w = getWidth();
			h = getHeight();
			g = getGraphics();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(logo, w / 2, h / 2, 3);
			flushGraphics();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return;
			}
		}
		// LOGO > MENU
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(-1);
			int arcSize = (int) ((now - startTime) * l / 500);
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 400)
				break;
		}
		// MENU
		state = 2;
		startTime = System.currentTimeMillis();
		while (needThread && state == 2) {
			w = getWidth();
			h = getHeight();
			g = getGraphics();
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			if (!lastInputIsTouch) {
				g.setColor(255, 0, 0);
				if (selected == 0) {
					int s = (int) (236 / mul);
					int s2 = s / 2;
					g.fillArc(w / 2 - s2, h / 2 - s2, s, s, 0, 360);
				} else {
					int s = (int) (164 / mul);
					if (selected == -1) {
						// 53; 18
						g.fillArc((int) (w / 2 - (320 - 53) / mul), (int) (h / 2 - (180 - 18) / mul), s, s, 0, 360);
					} else if (selected == -2) {
						g.fillArc((int) (w / 2 - (320 - 53) / mul), (int) (h / 2 - (180 - 178) / mul), s, s, 0, 360);
					} else if (selected == 1) {
						g.fillArc((int) (w / 2 + 103 / mul), (int) (h / 2 - (180 - 18) / mul), s, s, 0, 360);
					} else if (selected == 2) {
						g.fillArc((int) (w / 2 + 103 / mul), (int) (h / 2 - (180 - 178) / mul), s, s, 0, 360);
					}
				}
			}
			g.drawImage(menu, w / 2, h / 2, 3);
			g.setColor(0);
			g.drawString("v" + Nmania.version(), w / 2, h, 33);
			if (now - startTime < 500) {
				g.setColor(-1);
				int h1 = (int) (h * (500 - (now - startTime)) / 500) / 2;
				g.fillRect(0, 0, w, h1);
				g.fillRect(0, h - h1, w, h1);
			} else {
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					return;
				}
			}
			flushGraphics();
		}
		// MENU > SUBMENU
		if (state == 4) {
			startTime = System.currentTimeMillis();
			// play
			while (true) {
				int length = 400;
				long now = System.currentTimeMillis();
				g.setColor(action == 3 ? -1 : 0);
				int h1 = (int) (h * (now - startTime) / length);
				g.fillRect(0, 0, w, h1);
				g.fillRect(0, h - h1, w, h1);
				flushGraphics();
				if (now - startTime > length) {
					Open();
					// loading animation
					startTime = System.currentTimeMillis();
					while (needThread) {
						now = System.currentTimeMillis();
						g.setColor(0);
						g.fillRect(0, 0, w, h);
						g.setColor(-1);
						g.fillArc(w / 2 - 30, h / 2 - 30, 60, 60, -((int) now / 2) % 360, 90);
						g.fillArc(w / 2 - 30, h / 2 - 30, 60, 60, -((int) now / 2) % 360 + 180, 90);
						flushGraphics();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							return;
						}
					}
					return;
				}
			}
		}
		// MENU > EXIT
		if (state == 5) {
			startTime = System.currentTimeMillis();
			// exit
			while (true) {
				long now = System.currentTimeMillis();
				g.setColor(-1);
				int rw = (int) (w * (now - startTime) / 1000) / 2;

				g.fillRect(0, 0, rw, h);
				g.fillRect(w - rw, 0, rw, h);
				int arcSize = (int) (h * (now - startTime) / 1000) / 2;
				g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
				flushGraphics();
				if (now - startTime > 1000) {
					Nmania.exit();
					return;
				}
			}
		}
	}
}
