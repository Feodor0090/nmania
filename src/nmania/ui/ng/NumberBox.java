package nmania.ui.ng;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class NumberBox extends Screen {
	private int uuid;
	private INumberBoxHandler handler;
	private int value;
	private boolean allowNegative;
	private String title;
	private Font num = Font.getFont(0, 0, 8);
	private boolean sign;
	private final char[][] pad = new char[][] { new char[] { '1', '2', '3' }, new char[] { '4', '5', '6' },
			new char[] { '7', '8', '9' }, new char[] { '-', '0', '<' }, };

	public NumberBox(String title, int UUID, INumberBoxHandler handler, int value, boolean allowNegative) {
		this.title = title;
		uuid = UUID;
		this.handler = handler;
		this.value = value;
		sign = value >= 0;
		this.allowNegative = allowNegative;
	}

	public String GetTitle() {
		return title;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "CONFIRM";
	}

	public void OnOptionActivate(IDisplay d) {
		handler.OnNumberEntered(uuid, value, d);
	}

	public void Paint(Graphics g, int w, int h) {
		g.setColor(NmaniaDisplay.NEGATIVE_COLOR);
		g.setFont(num);
		int fh = num.getHeight();
		g.fillRoundRect(fh, 10, w - fh - fh, fh, fh, fh);
		g.setColor(NmaniaDisplay.PINK_COLOR);
		g.drawRoundRect(fh, 10, w - fh - fh, fh, fh, fh);
		g.setColor(-1);
		if (value == 0)
			g.drawString(sign ? "0" : "-0", w - fh - (fh >> 1), 10, Graphics.TOP | Graphics.RIGHT);
		else
			g.drawString(String.valueOf(value), w - fh - (fh >> 1), 10, Graphics.TOP | Graphics.RIGHT);
		PaintPad(g, w, fh + 20);
	}

	private final void PaintPad(Graphics g, int w, int y) {
		int fh = num.getHeight();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 3 && j == 0 && !allowNegative)
					continue;
				g.setColor(NmaniaDisplay.PINK_COLOR);
				g.fillRoundRect(j * w / 3 + 1, y, w / 3 - 2, fh, fh, fh);
				g.setColor(-1);
				g.drawChar(pad[i][j], j * w / 3 + w / 6, y, Graphics.TOP | Graphics.HCENTER);
			}
			y += fh + 2;
		}
	}

	public void OnKey(IDisplay d, int k) {
		value = Math.abs(value);
		if (k == Canvas.KEY_STAR || k == '-') {
			if (!allowNegative) {
				sign = true;
				ApplySign();
				return;
			}
			sign = !sign;
			ApplySign();
			return;
		}
		if (k == Canvas.KEY_POUND || k == 8) {
			value /= 10;
			ApplySign();
			return;
		}
		if (k >= '0' && k <= '9') {
			int a = k - '0';
			value = value * 10 + a;
			ApplySign();
			return;
		}
	}

	private void ApplySign() {
		value = Math.abs(value);
		if (!sign)
			value = -value;
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		if (s == 1) {
			int fh = num.getHeight();
			y -= fh + 20;
			if (y < 0)
				return;
			if (y < fh + 2) {
				OnKey(d, '1' + (x * 3 / w));
				return;
			}
			y -= fh + 2;
			if (y < fh + 2) {
				OnKey(d, '4' + (x * 3 / w));
				return;
			}
			y -= fh + 2;
			if (y < fh + 2) {
				OnKey(d, '7' + (x * 3 / w));
				return;
			}
			y -= fh + 2;
			if (y < fh + 2) {
				if (x < w / 3) {
					if (allowNegative)
						OnKey(d, Canvas.KEY_STAR);
				} else if (x < w * 2 / 3) {
					OnKey(d, '0');
				} else {
					OnKey(d, Canvas.KEY_POUND);
				}
				return;
			}
		}
	}
}
