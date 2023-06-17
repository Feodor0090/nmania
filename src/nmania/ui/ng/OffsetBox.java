package nmania.ui.ng;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nmania.GL;
import tube42.lib.imagelib.ColorUtils;

public class OffsetBox extends NumberBox {

	public OffsetBox(int UUID, INumberBoxHandler handler, int value, boolean allowNegative) {
		super("Clock offset", UUID, handler, value, allowNegative);
		showPlusMinus = true;
		try {
			wf = Image.createImage("/ui/exwf.png");
		} catch (IOException e) {
			GL.Log("Failed to load waveform image, screen will fail!");
		}
	}

	private Image wf;

	public void Paint(Graphics g, int w, int h) {
		g.setColor(0);
		g.fillRect(w - 46, 5, 42, h - 10);
		g.setColor(-1);
		g.drawRect(w - 46, 5, 41, h - 11);
		int time = (int) (System.currentTimeMillis() % 723);
		int pprogress = (723 - time) * 112 / 723;
		// waveform
		{
			g.drawRegion(wf, 112 - pprogress, 0, pprogress, 52, Sprite.TRANS_MIRROR_ROT90, w - 101, h - 5 - pprogress,
					0);
			int cy = h - 5 - pprogress - 112;
			while (cy > 5) {
				g.drawRegion(wf, 0, 0, 112, 52, Sprite.TRANS_MIRROR_ROT90, w - 101, cy, 0);
				cy -= 112;
			}
			int ty = -(cy - 5);
			g.drawRegion(wf, 0, 0, 112 - ty, 52, Sprite.TRANS_MIRROR_ROT90, w - 101, 5, 0);
		}
		// notes
		for (int i = -2; i < 30; i++) {
			int ny = h - 6 - pprogress - (56 * i) - (value * 112 / 723);
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
		super.Paint(g, w - 105, h);
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		super.OnTouch(d, s, x, y, dx, dy, w - 105, h);
	}
}
