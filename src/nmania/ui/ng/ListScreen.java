package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public abstract class ListScreen implements IScreen {

	private ListItem[] items;
	private boolean loadingState = true;
	private int selected;
	private int targetY;
	private int realY;
	private Font font = Font.getFont(0, 0, 8);
	private int fontH = font.getHeight();
	protected String horSelecrorTitle = null;

	public void SetItems(ListItem[] list) {
		items = list;
		if (selected >= list.length)
			selected = list.length - 1;
	}

	public ListItem GetSelected() {
		if (selected < 0)
			selected = 0;
		if (selected >= items.length)
			return null;
		return items[selected];
	}

	public void Paint(Graphics g, int w, int h) {
		g.setFont(font);
		int center = h / 2;
		int selectedY = selected * fontH + fontH / 2;
		if (selectedY <= center) {
			targetY = 0;
		} else if (items.length * fontH - center <= selectedY) {
			targetY = -(items.length * fontH - h);
		} else {
			targetY = -(selectedY - center);
		}
		if (realY != targetY) {
			int diff = targetY - realY;
			int add = (diff < 0) ? -1 : 1;
			diff /= 10;
			diff += add;
			realY += diff;
		}
		int y = realY;
		for (int i = 0; i < items.length; i++) {
			if (y > -fontH * 2) {
				if (selected == i) {
					g.setColor(NmaniaDisplay.PINK_COLOR);
					g.fillArc(fontH / 2, y, fontH, fontH, 0, 360);
					g.fillArc(w - fontH * 3 / 2, y, fontH, fontH, 0, 360);
					g.fillRect(fontH, y, w - fontH * 2, fontH);
				}
				ListItem item = items[i];
				int x = fontH;
				if (item.icon != null) {
					g.drawImage(item.icon, fontH, y + fontH / 2, Graphics.VCENTER | Graphics.LEFT);
					x += item.icon.getWidth();
				}
				if (item.text != null) {
					NmaniaDisplay.print(g, item.text, x, y, -1, 0, 0);
				}
				if (item instanceof ICustomListItem) {
					((ICustomListItem) item).Paint(g, y, w, fontH);
				}

			}
			y += fontH;

		}
	}

	public void OnKey(IDisplay d, int k) {
		if (k == -1 || k == '2') {
			selected--;
			if (selected < 0)
				selected = items.length - 1;
			return;
		}
		if (k == -2 || k == '8') {
			selected++;
			if (selected >= items.length)
				selected = 0;
			return;
		}
		if (items.length == 0)
			return;
		ListItem selected = GetSelected();
		if (k == -5 || k == 10 || k == '5' || k == 32) {
			selected.handler.OnSelect(selected, this, d);
		}
		if (k == -3 || k == '4') {
			selected.handler.OnSide(-1, selected, this, d);
			return;
		}
		if (k == -4 || k == '6') {
			selected.handler.OnSide(1, selected, this, d);
			return;
		}
	}

	public void OnEnter(IDisplay d) {
	}

	public void OnExit(IDisplay d) {
	}

	public void OnPause(IDisplay d) {
	}

	public void OnResume(IDisplay d) {
	}

}
