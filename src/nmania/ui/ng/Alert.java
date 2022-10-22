package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import symnovel.SNUtils;

public class Alert implements IScreen {

	private String title;
	private String[] text;
	private String button = null;
	private IScreen next;
	private int backsCount;

	public Alert(String title, String text) {
		this.title = title;
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
	public Alert(String title, String text, String button, IScreen next, int backsCount) {
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

	public void OnOptionActivate(IDisplay d) {
		if (next != null) {
			d.Push(next);
		}
	}

	public void Paint(Graphics g, int w, int h) {
		Font f = Font.getFont(0, 0, 8);
		g.setFont(f);
		int y = 0;
		int x = 10;
		int sp = f.charWidth(' ');
		boolean lineHasWords = false;
		for (int i = 0; i < text.length; i++) {
			String word = text[i];
			int ww = f.stringWidth(word);
			if (x + ww > w && lineHasWords) {
				x = 10;
				y += f.getHeight();
			}
			NmaniaDisplay.print(g, word, x, y, -1, 0, 0);
			lineHasWords = true;
			x += ww + sp;
		}
	}

	public void OnKey(IDisplay d, int k) {
	}

	public void OnEnter(IDisplay d) {
	}

	public boolean OnExit(IDisplay d) {
		return false;
	}

	public void OnPause(IDisplay d) {
	}

	public void OnResume(IDisplay d) {
		for (int i = 0; i < backsCount; i++)
			d.Back();
	}

}
