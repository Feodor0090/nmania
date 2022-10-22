package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import symnovel.SNUtils;

public class Alert implements IScreen {

	private String title;
	private String[] text;

	public Alert(String title, String text) {
		this.title = title;
		this.text = SNUtils.splitFull(text, ' ');
	}

	public String GetTitle() {
		return title;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void Paint(Graphics g, int w, int h) {
		Font f = Font.getFont(0, 0, 8);
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
	}

}
