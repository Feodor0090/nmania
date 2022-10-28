package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public class SwitchItem extends ListItem implements ICustomListItem {

	public boolean state;

	public SwitchItem(int uUID, String text, IListSelectHandler handler, boolean s) {
		super(uUID, text, handler);
		state = s;
	}
	
	public void Paint(Graphics g, int y, int w, int h) {
		NmaniaDisplay.print(g, state ? "ON" : "OFF", w - h, y, state ? 0x00ff00 : 0xff0000, 0,
				Graphics.RIGHT | Graphics.TOP);
	}

	public final void Toggle() {
		state = !state;
	}
}
