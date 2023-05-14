package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

import tube42.lib.imagelib.ColorUtils;

public class ScrollSpeedBox extends NumberBox {

	public ScrollSpeedBox(int UUID, INumberBoxHandler handler, int value, boolean allowNegative) {
		super("Scroll speed (higher = faster)", UUID, handler, value, allowNegative);
		showPlusMinus = true;
	}

	public void Paint(Graphics g, int w, int h) {
		g.setColor(0);
		g.fillRect(w - 46, 5, 42, h - 10);
		g.setColor(-1);
		g.drawRect(w - 46, 5, 41, h - 11);
		int time = (int) (System.currentTimeMillis() % 300);
		for (int i = 0; i < 16; i++) {
			int ny = posToY(time, i * 300, h);
			for (int j = 0; j < 25; j++) {
				int ly = ny - j;
				if (ly <= 5)
					break;
				if (ly >= h - 6)
					continue;
				g.setColor(ColorUtils.blend(0, 0x00ff00, j * 255 / 24));
				g.drawLine(w - 45, ly, w - 6, ly);
			}
		}
		super.Paint(g, w - 50, h);
	}

	private int posToY(int currTime, int noteTime, int h) {
		if (value == Integer.MAX_VALUE)
			return 0;
		if (value == 0)
			return 0;
		final int notesY = h - 11 + ((currTime * value) >> 5);
		return notesY - ((noteTime * value) >> 5);
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		super.OnTouch(d, s, x, y, dx, dy, w - 50, h);
	}

}
