package nmania.ui.ng;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import tube42.lib.imagelib.ColorUtils;

public class NumberBox extends Screen {
	private int uuid;
	private INumberBoxHandler handler;
	protected int value;
	private final boolean allowNegative;
	public boolean showPlusMinus;
	private String title;
	private Font num = Font.getFont(0, 0, 8);
	private boolean sign;
	private final String[][] pad = new String[][] { new String[] { "1", "2", "3" }, new String[] { "4", "5", "6" },
			new String[] { "7", "8", "9" }, new String[] { "-", "0", "<" } };

	public NumberBox(String title, int UUID, INumberBoxHandler handler, int value, boolean allowNegative) {
		this.title = title;
		uuid = UUID;
		this.handler = handler;
		this.value = value;
		sign = value >= 0;
		this.allowNegative = allowNegative;
		if (!allowNegative)
			pad[3][0] = null;
	}

	public String GetTitle() {
		return title;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		if (value == Integer.MAX_VALUE)
			return null;
		return "CONFIRM";
	}

	public void OnOptionActivate(IDisplay d) {
		if (value != Integer.MAX_VALUE)
			handler.OnNumberEntered(uuid, value, d);
	}

	public void Paint(Graphics g, int w, int h) {
		g.setFont(num);
		int fh = num.getHeight();
		int offs = fh;
		if (showPlusMinus) {
			offs += 40;
			int b = (int) (Math.abs(1f - NmaniaDisplay.beatProgress * 2f) * 32);
			g.setColor(ColorUtils.blend(-1, NmaniaDisplay.PINK_COLOR, b));
			g.fillRoundRect(fh, 10, 35, fh, fh, fh);
			g.fillRoundRect(w - fh - 35, 10, 35, fh, fh, fh);
			g.setColor(-1);
			g.drawString("-1", fh + 17, 10, Graphics.TOP | Graphics.HCENTER);
			g.drawString("+1", w - fh - 17, 10, Graphics.TOP | Graphics.HCENTER);
		}

		g.setColor(NmaniaDisplay.NEGATIVE_COLOR);
		g.fillRoundRect(offs, 10, w - offs - offs, fh, fh, fh);
		g.setColor(NmaniaDisplay.PINK_COLOR);
		g.drawRoundRect(offs, 10, w - offs - offs, fh, fh, fh);
		g.setColor(-1);
		if (value == Integer.MAX_VALUE)
			g.drawString(sign ? "_" : "-_", w - offs - (fh >> 1), 10, Graphics.TOP | Graphics.RIGHT);
		else if (value == 0)
			g.drawString(sign ? "0" : "-0", w - offs - (fh >> 1), 10, Graphics.TOP | Graphics.RIGHT);
		else
			g.drawString(String.valueOf(value), w - offs - (fh >> 1), 10, Graphics.TOP | Graphics.RIGHT);
		PaintPad(g, w, fh + 20);
	}

	private final void PaintPad(Graphics g, int w, int y) {
		int fh = num.getHeight();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (pad[i][j] == null)
					continue;
				int b = (int) (Math.abs(1f - NmaniaDisplay.beatProgress * 2f) * 32);
				g.setColor(ColorUtils.blend(-1, NmaniaDisplay.PINK_COLOR, b));
				g.fillRoundRect(j * w / 3 + 1, y, w / 3 - 2, fh, fh, fh);
				g.setColor(-1);
				g.drawString(pad[i][j], j * w / 3 + w / 6, y, Graphics.TOP | Graphics.HCENTER);
			}
			y += fh + 2;
		}
	}

	public void OnKey(IDisplay d, int k) {
		if (value == Integer.MAX_VALUE) {
			if (k != Canvas.KEY_STAR)
				value = 0;
		}
		value = Math.abs(value);
		if (k == Canvas.KEY_STAR || k == '-') {
			if (allowNegative)
				sign = !sign;
			else
				sign = true;
			ApplySign();
			return;
		}
		if (k == Canvas.KEY_POUND || k == 8) {
			if (value == 0) {
				sign = true;
				value = Integer.MAX_VALUE;
			} else
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
		if (value == Integer.MAX_VALUE)
			return;

		value = Math.abs(value);
		if (!sign)
			value = -value;
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		if (s == 1) {
			int fh = num.getHeight();
			y -= 10;
			if (y < 0)
				return;
			if (y < fh) {
				if (showPlusMinus && value != Integer.MAX_VALUE) {
					if (x < fh + 40) {
						if (allowNegative)
							value--;
						else if (value > 0)
							value--;
					} else if (x > w - fh - 40) {
						value++;
					}
				}
				return;
			}
			y -= fh + 10;
			if (y < fh + 2) {
				if (x < w)
					OnKey(d, '1' + (x * 3 / w));
				return;
			}
			y -= fh + 2;
			if (y < fh + 2) {
				if (x < w)
					OnKey(d, '4' + (x * 3 / w));
				return;
			}
			y -= fh + 2;
			if (y < fh + 2) {
				if (x < w)
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
				} else if (x < w) {
					OnKey(d, Canvas.KEY_POUND);
				}
				return;
			}
		}
	}
}
