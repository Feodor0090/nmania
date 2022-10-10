package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public class DataItem extends ListItem implements ICustomListItem {

	public String data;

	public void Paint(Graphics g, int y, int w, int h) {
		if (data != null)
			NmaniaDisplay.print(g, data, w - h, y, -1, 0, Graphics.RIGHT | Graphics.TOP);
	}

	public DataItem(int uUID, String text, IListSelectHandler handler, String data) {
		super(uUID, text, handler);
		this.data = data;
	}
	
	
}
