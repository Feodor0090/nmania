package nmania.ui.ng;

import javax.microedition.lcdui.Image;

public class ListItem {
	public int UUID;
	public String text;
	public Image icon;
	public IListSelectHandler handler;

	public ListItem(int uUID, String text, Image icon, IListSelectHandler handler) {
		super();
		UUID = uUID;
		this.text = text;
		this.icon = icon;
		this.handler = handler;
	}

	public ListItem(int uUID, String text, IListSelectHandler handler) {
		super();
		UUID = uUID;
		this.text = text;
		this.handler = handler;
	}

	public ListItem(String text, IListSelectHandler handler) {
		super();
		this.text = text;
		this.handler = handler;
	}

	public ListItem() {

	}
}
