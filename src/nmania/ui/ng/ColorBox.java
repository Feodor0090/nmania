package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public class ColorBox extends ListScreen implements IListSelectHandler, INumberBoxHandler {

	private String title;
	private int uuid;
	private INumberBoxHandler handler;
	private int value;
	private final DataItem red = new DataItem(0, "Red", this, null);
	private final DataItem green = new DataItem(1, "Green", this, null);
	private final DataItem blue = new DataItem(2, "Blue", this, null);

	public ColorBox(String title, int UUID, INumberBoxHandler handler, int value) {
		this.title = title;
		uuid = UUID;
		this.handler = handler;
		this.value = value;
		SetItems(new ListItem[] { red, green, blue });
		UpdateItems();
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
		super.Paint(g, w, h);
		g.setColor(value);
		int y = realY + 10 + 3 * fontH;
		g.fillRect(10, y, w - 20, 40);
		g.setColor(-1);
		g.drawRect(10, y, w - 21, 39);
	}

	private void UpdateItems() {
		red.data = String.valueOf((value >> 16) & 255);
		green.data = String.valueOf((value >> 8) & 255);
		blue.data = String.valueOf(value & 255);
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		if (item == red) {
			display.Push(new NumberBox("Red value", 0, this, (value >> 16) & 255, false));
		} else if (item == green) {
			display.Push(new NumberBox("Green value", 1, this, (value >> 8) & 255, false));
		} else if (item == blue) {
			display.Push(new NumberBox("Blue value", 2, this, value & 255, false));
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		int r = ((value >> 16) & 255);
		int g = ((value >> 8) & 255);
		int b = (value & 255);
		if (item == red) {
			r += direction * 4;
		} else if (item == green) {
			g += direction * 4;
		} else if (item == blue) {
			b += direction * 4;
		}
		value = (clampC(r) << 16) | (clampC(g) << 8) | (clampC(b));
		UpdateItems();
	}

	public void OnNumberEntered(int UUID, int newNumber, IDisplay d) {
		int r = ((value >> 16) & 255);
		int g = ((value >> 8) & 255);
		int b = (value & 255);
		if (UUID == 0) {
			r = newNumber;
		} else if (UUID == 1) {
			g = newNumber;
		} else if (UUID == 2) {
			b = newNumber;
		}
		value = (clampC(r) << 16) | (clampC(g) << 8) | (clampC(b));
		UpdateItems();
	}

	private static int clampC(int c) {
		if (c < 0)
			return 0;
		if (c > 255)
			return 255;
		return c;
	}

}
