package nmania.ui.ng;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;

import tube42.lib.imagelib.ColorUtils;

public abstract class ListScreen implements IScreen {

	private ListItem[] items;
	protected boolean loadingState = false;
	private int selected;
	private int targetY;
	private int realY;
	protected Font font = Font.getFont(0, 0, 8);
	private int fontH = font.getHeight();
	protected String horSelecrorTitle = null;
	private String selectedText = null;
	int textScroll = 0;

	public final void SetItems(ListItem[] list) {
		items = list;
		if (selected >= list.length)
			selected = list.length - 1;
	}

	public final ListItem[] GetAllItems() {
		return items;
	}

	public final void SetItems(Vector v) {
		ListItem[] arr = new ListItem[v.size()];
		v.copyInto(arr);
		SetItems(arr);
	}

	public final ListItem GetSelected() {
		if (items == null)
			return null;
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
		int bb = h * 3 / 2;
		for (int i = 0; i < items.length; i++) {
			if (y > -fontH * 2) {
				if (selected == i) {
					g.setColor(NmaniaDisplay.PINK_COLOR);
					g.fillArc(fontH / 2, y, fontH, fontH, 0, 360);
					g.fillRect(fontH, y, w - fontH, fontH);
					g.setColor(
							ColorUtils.blend(NmaniaDisplay.PINK_COLOR, -1, (int) (255 * NmaniaDisplay.beatProgress)));
					int x = w - (int) (w * NmaniaDisplay.beatProgress);
					g.fillArc(x + fontH / 2, y, fontH, fontH, 0, 360);
					g.fillRect(x + fontH, y, w - fontH, fontH);
				}
				ListItem item = items[i];
				int x = fontH;
				if (item.icon != null) {
					g.drawImage(item.icon, fontH, y + fontH / 2, Graphics.VCENTER | Graphics.LEFT);
					x += item.icon.getWidth();
				}
				if (item.text != null) {
					if (selected == i) {
						if (selectedText != item.text) {
							selectedText = item.text;
							textScroll = 0;
						} else {
							int tw = font.stringWidth(selectedText);
							int atw = w - fontH;
							if (tw > atw) {
								if (tw - textScroll > 0)
									textScroll += 3;
								else
									textScroll = -w;
							} else {
								textScroll = 0;
							}
						}
						g.setClip(fontH >> 1, y, w - fontH, fontH);
						NmaniaDisplay.print(g, selectedText, x - textScroll, y, -1, 0, 0);
						g.setClip(-1000, -1000, 9999, 9999);
					} else
						NmaniaDisplay.print(g, item.text, x, y, -1, 0, 0);
				}
				if (item instanceof ICustomListItem) {
					((ICustomListItem) item).Paint(g, y, w, fontH);
				}

			}
			y += fontH;
			if (y > bb)
				break;
		}
	}

	public void OnKey(IDisplay d, int k) {
		if (items == null)
			return;
		if (k == -1 || k == '2') {
			selected--;
			if (selected < 0)
				selected = items.length - 1;
			OnItemChange();
			return;
		}
		if (k == -2 || k == '8') {
			selected++;
			if (selected >= items.length)
				selected = 0;
			OnItemChange();
			return;
		}
		if (items.length == 0)
			return;
		ListItem selected = GetSelected();
		if (k == -5 || k == 10 || k == '5' || k == 32 || ((Canvas) d).getGameAction(k) == Canvas.FIRE) {
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

	protected void OnItemChange() {
	}

}
