package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.ModsState;

public class ModsSelectScreen implements IScreen {

	Font f = Font.getFont(0, 0, 8);
	int fontH;
	private ModsState mods;
	private String[] da = new String[] { "Easy", "Normal", "Hard" };
	private String[] fa = new String[] { "No fail", "Normal", "Sudden death" };
	private int selected = 0;

	public ModsSelectScreen(ModsState mods) {
		this.mods = mods;
		fontH = f.getHeight();
	}

	public String GetTitle() {
		return "GAMEPLAY MODS";
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
		g.setFont(f);
		int y = drawTripleSelection(g, 0, w, selected == 0, mods.GetDA(), "Difficulty adjustment", da);
		y = drawTripleSelection(g, y, w, selected == 1, mods.GetFA(), "Failing adjustment", fa);
		
	}

	private int drawTripleSelection(Graphics g, int y, int w, boolean selected, int item, String title,
			String[] items) {
		if (selected) {
			g.setColor(NmaniaDisplay.PINK_COLOR);
			g.fillArc(fontH / 2, y, fontH, fontH, 0, 360);
			g.fillArc(w - fontH * 3 / 2, y, fontH, fontH, 0, 360);
			g.fillRect(fontH, y, w - fontH * 2, fontH);
		}
		NmaniaDisplay.print(g, title, w >> 1, y, -1, 0, Graphics.TOP | Graphics.HCENTER);
		y += fontH;
		int ow = (w - 40) / 3;

		g.setColor(NmaniaDisplay.PINK_COLOR);
		int trt = y + 2;
		int trb = y + fontH - 2;
		int trc = y + fontH / 2;
		if (item < 0) {
			g.fillRect(10, y, ow, fontH);
			if (selected)
				g.fillTriangle(ow + 12, trt, ow + 12, trb, ow + 20, trc);
		} else if (item == 0) {
			g.fillRect(20 + ow, y, ow, fontH);
			if (selected) {
				g.fillTriangle(ow + 10, trc, ow + 18, trt, ow + 18, trb);
				g.fillTriangle(ow * 2 + 22, trt, ow * 2 + 22, trb, ow * 2 + 30, trc);
			}
		} else {
			g.fillRect(30 + ow * 2, y, ow, fontH);
			if (selected)
				g.fillTriangle(ow * 2 + 20, trc, ow * 2 + 28, trt, ow * 2 + 28, trb);
		}
		NmaniaDisplay.print(g, items[0], 10 + ow / 2, y, -1, 0, Graphics.TOP | Graphics.HCENTER);
		NmaniaDisplay.print(g, items[1], w / 2, y, -1, 0, Graphics.TOP | Graphics.HCENTER);
		NmaniaDisplay.print(g, items[2], 30 + ow * 2 + ow / 2, y, -1, 0, Graphics.TOP | Graphics.HCENTER);

		return y + fontH + 2;
	}

	public void OnKey(IDisplay d, int k) {
		if (k == -1 || k == '2') {
			selected--;
			if (selected < 0)
				selected = 1;
			return;
		}
		if (k == -2 || k == '8') {
			selected++;
			if (selected >= 2)
				selected = 0;
			return;
		}

		int dir = 0;
		// left
		if (k == -3 || k == '4') {
			dir = -1;
		}
		// right
		if (k == -4 || k == '6') {
			dir = 1;
		}

		switch (selected) {
		case 0:
			mods.ToggleDA(dir);
			break;
		case 1:
			mods.ToggleFA(dir);
			break;
		}
	}

	public void OnEnter(IDisplay d) {
	}

	public void OnExit(IDisplay d) {
	}

	public void OnPause(IDisplay d) {
	}

	public void OnResume(IDisplay d) {
	}

}
