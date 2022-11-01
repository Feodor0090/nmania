package nmania.ui.ng;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import tube42.lib.imagelib.ColorUtils;

public abstract class ListScreen extends Screen {

	private ListItem[] items;
	/**
	 * Is this list not ready?
	 */
	protected boolean loadingState = false;
	/**
	 * Index of currently focused item.
	 */
	private int selected;
	/**
	 * Shift of screen's content which must be approached.
	 * In keyboard mode, calculated by Y of selected item.
	 * Always less than zero.
	 */
	private int targetY;
	/**
	 * Shift of screen's content right now.
	 * In keyboard mode, this approaches targetY.
	 * In touch mode controlled by used.
	 * Always less than zero.
	 */
	private int realY;
	/**
	 * <ul>
	 * <li> 0 = keyboard mode, selected item is approached.
	 * <li> 1 = touch mode, finger is holded.
	 * <li> 2 = touch mode, finger is released.
	 * </ul>
	 */
	private int scrollMode = 0;
	protected Font font = Font.getFont(0, 0, 8);
	private int fontH = font.getHeight();
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
		if (loadingState) {
			DrawLoadingBar(g, w, h);
		} else {
			UpdateScroll(w, h);
			DrawScreen(g, w, h);
		}
	}

	protected void UpdateScroll(int w, int h) {
		if (items == null) {
			selected = 0;
			realY = 0;
			targetY = 0;
			return;
		}
		if (selected < 0)
			selected = 0;
		if (selected >= items.length)
			selected = items.length - 1;
		
		int center = h / 2;
		int selectedY = selected * fontH + fontH / 2;
		int totalH = items.length * fontH;
		if (scrollMode == 0) {
			if (selectedY <= center) {
				targetY = 0;
			} else if (totalH - center <= selectedY) {
				targetY = -(totalH - h);
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
		} else if (scrollMode == 1) {
			// dragged
			if (targetY > 0) {
				// to bottom
				realY = targetY >> 1;
			} else if(targetY < -totalH + h) {
				int diff = -targetY + totalH - h;
				realY = -totalH + h - (diff >> 1);
			} else {
				realY = targetY;
			}
		} else {
			if (targetY > 0) {
				targetY = 0;
			} else if(targetY < -totalH + h) {
				realY = -totalH + h;
			}
			if(targetY != realY) {
				int diff = targetY - realY;
				int add = (diff < 0) ? -1 : 1;
				diff /= 10;
				diff += add;
				realY += diff;
			}
		}
	}

	protected void DrawScreen(Graphics g, int w, int h) {
		if (items == null)
			return;
		g.setFont(font);
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

	private final void DrawLoadingBar(Graphics g, int w, int h) {
		int center = h >> 1;
		int cw = w >> 1;
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
	}

	public void OnKey(IDisplay d, int k) {
		if (items == null)
			return;
		if (IsUp(d, k)) {
			if (selected == 0)
				selected = items.length - 1;
			else
				selected--;
			scrollMode = 0;
			OnItemChange();
			return;
		}
		if (IsDown(d, k)) {
			if (selected == items.length - 1)
				selected = 0;
			else
				selected++;
			scrollMode = 0;
			OnItemChange();
			return;
		}
		if (items.length == 0)
			return;
		ListItem selected = GetSelected();
		if (IsOk(d, k)) {
			selected.handler.OnSelect(selected, this, d);
		}
		if (IsLeft(d, k)) {
			selected.handler.OnSide(-1, selected, this, d);
			return;
		}
		if (IsRight(d, k)) {
			selected.handler.OnSide(1, selected, this, d);
			return;
		}
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		targetY += dy;
		if (s == 3) {
			scrollMode = 2;
		} else {
			scrollMode = 1;
		}
	}

	protected void OnItemChange() {
	}

}
