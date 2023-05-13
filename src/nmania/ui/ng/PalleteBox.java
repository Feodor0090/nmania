package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public class PalleteBox extends ListScreen {

	private final String title;
	private final int[] original;

	public PalleteBox(String title, int[] pallete, String[] names) {
		this.title = title;
		original = pallete;
		ListItem[] items = new ListItem[pallete.length];
		for (int i = 0; i < items.length; i++) {
			String text = null;
			if (names != null && names.length > i)
				text = names[i];
			if (text == null)
				text = "Color " + (i + 1);
			items[i] = new PalleteItem(text, pallete[i]);
		}
		SetItems(items);
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
		ListItem[] l = GetAllItems();
		for (int i = 0; i < l.length; i++)
			original[i] = ((PalleteItem) l[i]).value;
		d.Back();
	}

	private class PalleteItem extends ListItem implements ICustomListItem, INumberBoxHandler, IListSelectHandler {

		public PalleteItem(String text, int value) {
			this.value = value;
			this.text = text;
			this.handler = this;
		}

		public int value;

		public void Paint(Graphics g, int y, int w, int h) {
			g.setColor(value);
			g.fillRect(w - h - 5, y, h, h);
		}

		public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
			display.Push(new ColorBox(text, 0, this, value));
		}

		public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		}

		public void OnNumberEntered(int UUID, int newNumber, IDisplay d) {
			value = newNumber;
			d.Back();
		}

	}
}
