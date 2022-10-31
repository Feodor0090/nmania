package nmania.ui.ng;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;

import nmania.Nmania;
import nmania.Skin;
import nmania.ui.VectorSkinSetup;
import tube42.lib.imagelib.ColorUtils;

public class SkinSelectScreen extends Screen implements CommandListener {

	private final Command back = new Command("Back", Command.BACK, 2);
	private IDisplay d;

	public SkinSelectScreen(IDisplay d) {
		this.d = d;
		if (Nmania.skin == null)
			Nmania.skin = new Skin();
		if (Nmania.skin.rich) {
			try {
				Nmania.skin.LoadRich(false);
			} catch (IllegalStateException e) {
				Alert a = new Alert("Rich skin is invalid", e.getMessage(), null, AlertType.ERROR);
				a.setTimeout(Alert.FOREVER);
				d.PauseRendering();
				Nmania.Push(a);
			}
		}
	}

	public String GetTitle() {
		return "SKIN TYPE SELECT";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "SETTINGS";
	}

	public void OnOptionActivate(IDisplay d) {
		d.PauseRendering();
		if (Nmania.skin.rich) {
			DisplayRichInfo();
		} else {
			Nmania.Push(new VectorSkinSetup(d.GetDisplayable()));
		}
	}

	public void Paint(Graphics g, int w, int h) {
		g.setColor(NmaniaDisplay.PINK_COLOR);
		if (Nmania.skin.rich) {
			g.fillRoundRect(w / 2 + 10, 0, w / 2 - 20, h, 40, 40);
		} else {
			g.fillRoundRect(10, 0, w / 2 - 20, h, 40, 40);
		}

		drawVectorSkinIcon(g, w / 4, 55);
		drawRichSkinIcon(g, w * 3 / 4, 55);
		g.setFont(Font.getFont(0, 0, 8));
		NmaniaDisplay.print(g, "Vector", w / 4, h - 5, -1, 0, Graphics.BOTTOM | Graphics.HCENTER);
		NmaniaDisplay.print(g, "Rich", w * 3 / 4, h - 5, -1, 0, Graphics.BOTTOM | Graphics.HCENTER);
	}

	static void drawVectorSkinIcon(Graphics g, int x, int y) {
		g.setColor(NmaniaDisplay.NMANIA_COLOR);
		g.fillRect(x - 50, y - 45, 50, 20);
		g.fillRect(x, y - 15, 50, 20);
		g.fillRect(x - 50, y + 15, 50, 20);
		g.setColor(-1);
		g.drawLine(x - 50, y - 50, x - 50, y + 50);
		g.drawLine(x, y - 50, x, y + 50);
		g.drawLine(x + 50, y - 50, x + 50, y + 50);
		g.drawLine(x - 50, y + 50, x + 50, y + 50);

	}

	static void drawRichSkinIcon(Graphics g, int x, int y) {
		for (int i = x - 50; i < x + 49; i++) {
			int bl = Math.abs(i - x);
			g.setColor(ColorUtils.blend(0, NmaniaDisplay.NMANIA_COLOR, bl * 255 / 50));
			g.drawLine(i, y - 50, i, y + 50);
		}
	}

	public void OnKey(IDisplay d, int k) {
		if (IsLeft(d, k) || IsRight(d, k)) {
			Nmania.skin.rich = !Nmania.skin.rich;
		}
	}

	public void OnTouch(IDisplay d, int s, int x, int y, int dx, int dy, int w, int h) {

	}

	private final void DisplayRichInfo() {
		Form f = new Form("Rich skin setup");
		f.setCommandListener(this);
		f.addCommand(back);
		f.append("Rich skin setup is being done via file manager.\n");
		f.append("In your working folder, create a subfolder \"_skin\".\n");
		f.append("File naming structure: \"(type)(index).png\". Only png is supported.\n");
		f.append(
				"There should be 3 (1,2,3) \"key\", 3 \"hkey\", 3 \"note\", 6 (0-5) \"judgment\" and 12 (0-11) \"digit\" images.\n");
		f.append("The 1st images will be used in non-odd columns, the 2nds in odd and the 3rds in central.\n");
		f.append(
				"0-9 digit images will be used for 0-9 digits. The 10th image should contain comma, the 11th \"%\" symbol.\n");
		f.append("0th-5th judgment images should contain splashes for 0, 50, 100, 200, 300, 305.\n");
		f.append(
				"All digits must have the same height. All keys and hkeys the same size. All notes the same size. Keys, hkeys and notes the same width.");
		Nmania.Push(f);
	}

	public void commandAction(Command c, Displayable arg1) {
		if (c == back)
			Nmania.Push(d.GetDisplayable());
	}

}
