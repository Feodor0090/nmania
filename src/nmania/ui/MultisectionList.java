package nmania.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.Nmania;
import nmania.Settings;

public abstract class MultisectionList extends Canvas {

	public MultisectionList(boolean touch) {
		setFullScreenMode(true);
		selected = touch ? -1 : 0;
		repaint();
		this.touch = touch;
	}

	int iy;
	int th;

	protected ListSection curr = null;
	protected ListSection prev;
	boolean switching;
	int switchOffset = 0;
	boolean touch;
	int selected = 0;

	public final void Switch(final ListSection ss) {
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

	protected void paint(Graphics g) {
		Font f = Font.getFont(0, 0, 8);
		th = f.getHeight();
		g.setFont(f);
		if (switching) {
			int x = (getWidth() * (40 - switchOffset)) / 20;
			g.setColor(0);
			g.fillRect(0, 0, getWidth() - x, getHeight());
			g.setColor(MainScreen.bgColor);
			g.fillRect(getWidth() - x, 0, Math.min(x, getWidth()), getHeight());
			g.translate(-x, 0);
			if (prev != null && x < getWidth()) {
				paintSection(g, prev);
			}
			g.translate(getWidth() * 2, 0);
			g.setColor(0);
			g.fillRect(0, 0, getWidth(), getHeight());
			if (curr != null && x > getWidth()) {
				paintSection(g, curr);
			}
			g.translate(-g.getTranslateX(), 0);
		} else {
			g.setColor(0);
			g.fillRect(0, 0, getWidth(), getHeight());
			paintSection(g, curr);
		}
	}

	private void paintSection(Graphics g, ListSection s) {
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

	public abstract class ListSection {
		public abstract String GetTitle();

		public abstract String[] GetItems();

		public abstract void OnSelect(int i);

		public void paint(Graphics g, int y, int sw) {
		}
	}
}
