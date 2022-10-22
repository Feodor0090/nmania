package nmania.ui.ng;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public abstract class ListScreen implements IScreen {

	private ListItem[] items;
	protected boolean loadingState = false;
	private int selected;
	private int targetY;
	private int realY;
	protected Font font = Font.getFont(0, 0, 8);
	private int fontH = font.getHeight();
	protected String horSelecrorTitle = null;

	public void SetItems(ListItem[] list) {
		items = list;
		if (selected >= list.length)
			selected = list.length - 1;
	}

	public void SetItems(Vector v) {
		ListItem[] arr = new ListItem[v.size()];
		v.copyInto(arr);
		SetItems(arr);
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
		if (loadingState) {
			int cw = w / 2;
			g.setColor(NmaniaDisplay.PINK_COLOR);
			g.fillRect(cw - 102, center - 12, 204, 24);
			g.setClip(cw - 100, center - 10, 200, 20);
			int y1 = center - 10;
			int y2 = center + 10;
			g.setColor(NmaniaDisplay.NMANIA_COLOR);
			int shift = (int) (System.currentTimeMillis() / 500);
			for (int x = cw - 140 - (shift % 40); x < w; x += 40) {
				g.fillTriangle(x, y2, x + 20, y2, x + 20, y1);
				g.fillTriangle(x + 20, y1, x + 20, y2, x + 40, y1);
			}
			g.setClip(-1000, -1000, 9999, 9999);
			return;
		}
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
		if (items == null)
			return;
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
		if (items == null)
			return;
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

	public boolean OnExit(IDisplay d) {
		return false;
	}

	public void OnPause(IDisplay d) {
	}

	public void OnResume(IDisplay d) {
	}

}
