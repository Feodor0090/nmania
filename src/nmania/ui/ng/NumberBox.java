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
	private Font buttons = Font.getFont(0, 0, 0);
	private boolean sign;

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

	public void OnTouch(IDisplay d, int s, int x, int y, int w, int h) {
	}
}
