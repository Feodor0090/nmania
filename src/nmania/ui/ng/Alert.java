package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import symnovel.SNUtils;

public class Alert extends Screen {

	protected String title;
	private String[] text;
	private String button = null;
	private Screen next;
	private int backsCount;

	public Alert(String title, String text) {
		this.title = title;
		if (text == null)
			this.text = new String[0];
		else
			this.text = SNUtils.splitFull(text, ' ');
	}

	/**
	 * Initializes alert.
	 * 
	 * @param title      Title of the screen.
	 * @param text       Text to show.
	 * @param button     Text for left button.
	 * @param next       Screen to open by left button.
	 * @param backsCount Pass 0 to stay on alert, 1 to close the alert on return,
	 *                   1+N to close alert and N underlaying screens.
	 */
	public Alert(String title, String text, String button, Screen next, int backsCount) {
		this(title, text);
		this.button = button;
		this.next = next;
		this.backsCount = backsCount;
	}

	public String GetTitle() {
		return title;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return button;
	}

	public void SetText(String str) {
		this.text = SNUtils.splitFull(str, ' ');
	}

	public void OnOptionActivate(IDisplay d) {
		if (next != null) {
			d.Push(next);
		}
	}

	int scroll = 0;
	int totalY;
	boolean allowScroll = false;

	public void Paint(Graphics g, int w, int h) {
		if (allowScroll) {
			if (scroll < 0)
				scroll = 0;
			if (totalY - scroll < h)
				scroll = totalY - h;
		} else
			scroll = 0;
		Font f = Font.getFont(0, 0, 8);
		g.setFont(f);
		int y = 0;
		int x = 10;
		int sp = f.charWidth(' ');

		boolean lineHasWords = false;
		for (int i = 0; i < text.length; i++) {
			String word = text[i];
			int ww = f.stringWidth(word);
			boolean nl = word.equals("\n");
			if (nl || (x + ww > w && lineHasWords)) {
				x = 10;
				y += f.getHeight();
			}
			if (nl)
				continue;
			NmaniaDisplay.print(g, word, x, y - scroll, -1, 0, 0);
			lineHasWords = true;
			x += ww + sp;
		}
		totalY = y + f.getHeight();
		allowScroll = totalY > h;
	}

	public void OnKey(IDisplay d, int k) {
		if (allowScroll) {
			if (IsUp(d, k))
				scroll -= 30;
			if (IsDown(d, k))
				scroll += 30;
		}
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		if (allowScroll)
			scroll -= dy;
	}

	public void OnResume(IDisplay d) {
		for (int i = 0; i < backsCount; i++)
			d.Back();
	}

}
