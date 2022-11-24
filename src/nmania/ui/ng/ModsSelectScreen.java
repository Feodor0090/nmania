package nmania.ui.ng;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import nmania.GL;
import nmania.ModsState;
import nmania.Settings;

public class ModsSelectScreen extends Screen {

	Font f = Font.getFont(0, 0, 8);
	int fontH;
	private ModsState mods;
	private String[] da = new String[] { "Easy", "Normal", "Hard" };
	private String[] fa = new String[] { "No fail", "Normal", "Sudden death" };
	private String[] lines = new String[] { "Difficulty adjustment", "Failing adjustment", "Save as default" };
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

	public void Paint(Graphics g, int w, int h) {
		g.setFont(f);
		int y = drawTripleSelection(g, 0, w, selected == 0, mods.GetDA(), lines[0], da);
		y = drawTripleSelection(g, y, w, selected == 1, mods.GetFA(), lines[1], fa);
		if (selected == 2) {
			g.setColor(NmaniaDisplay.PINK_COLOR);
			g.fillArc(fontH / 2, y, fontH, fontH, 0, 360);
			g.fillRect(fontH, y, w - fontH, fontH);
		}
		NmaniaDisplay.print(g, lines[2], fontH, y, -1, 0, 0);
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
		if (selected == 2 && IsOk(d, k)) {
			Settings.defaultMods = mods.GetMask();
			Settings.Save();
			GL.Log("(ui) Mods preset was saved.");
			return;
		}
		if (IsUp(d, k)) {
			selected--;
			if (selected < 0)
				selected = 2;
			GL.Log("(ui) Selecting " + lines[selected] + " on mods screen");
			return;
		}
		if (IsDown(d, k)) {
			selected++;
			if (selected > 2)
				selected = 0;
			GL.Log("(ui) Selecting " + lines[selected] + " on mods screen");
			return;
		}

		int dir = 0;
		// left
		if (IsLeft(d, k)) {
			dir = -1;
		}
		// right
		if (IsRight(d, k)) {
			dir = 1;
		}

		if (dir == 0) {
			return;
		}

		switch (selected) {
		case 0:
			mods.ToggleDA(dir);
			break;
		case 1:
			mods.ToggleFA(dir);
			break;
		}
		GL.Log("(ui) Gameplay mods switched to " + mods.toString());
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {
		if (s != 1)
			return;

		int dir = x * 3 / w;
		dir--;
		if (y < fontH * 2) {
			selected = 0;
			mods.SetDA(dir);
		} else if (y < fontH * 4) {
			selected = 1;
			mods.SetFA(dir);
		} else if (y < fontH * 5) {
			selected = 2;
			Settings.defaultMods = mods.GetMask();
			Settings.Save();
		}
	}

}
