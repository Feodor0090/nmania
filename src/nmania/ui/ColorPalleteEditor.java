package nmania.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;

import nmania.Nmania;
import symnovel.SNUtils;

public class ColorPalleteEditor extends Form implements CommandListener {

	public ColorPalleteEditor(int[] colors, Displayable prev) {
		super(Nmania.commonText[2]);
		this.prev = prev;
		this.colors = colors;
		fields = new TextField[colors.length];
		for (int i = 0; i < colors.length; i++) {
			String curr = Integer.toHexString(colors[i]);
			while (curr.length() < 8)
				curr = "0" + curr;
			fields[i] = new TextField("Color " + (i + 1) + " (HEX value):", curr, 8, 0);
			append(fields[i]);
		}
		setCommandListener(this);
		addCommand(back);
		addCommand(view);
	}

	private final Command back = new Command(Nmania.commonText[0], Command.BACK, 1);
	private final Command view = new Command(Nmania.commonText[3], Command.SCREEN, 2);
	final Displayable prev;
	public final int[] colors;
	TextField[] fields;

	public void commandAction(Command c, Displayable arg1) {
		int i = 0;
		try {
			for (i = 0; i < colors.length; i++) {
				colors[i] = SNUtils.toARGB("0x"+fields[i].getString());
			}
			if (c == back)
				Nmania.Push(prev);
			if (c == view)
				Nmania.Push(new ColorPalletePreview(this));
		} catch (Exception e) {
			e.printStackTrace();
			Nmania.Push(new Alert(Nmania.commonText[2], "Color " + (i + 1) + " is invalid. Check the field.", null,
					AlertType.ERROR));
		}

	}

	public static class ColorPalletePreview extends Canvas {
		public ColorPalletePreview(ColorPalleteEditor editor) {
			setFullScreenMode(true);
			this.editor = editor;
		}

		private final ColorPalleteEditor editor;

		protected void paint(Graphics g) {
			int l = editor.colors.length;
			final int w = getWidth();
			final int h = getHeight();
			for (int i = 0; i < l; i++) {
				g.setColor(editor.colors[i]);
				g.fillRect(i * w / l, 0, w / l, h);
			}
		}

		protected void keyPressed(int arg0) {
			Nmania.Push(editor);
		}

		protected void pointerPressed(int arg0, int arg1) {
			Nmania.Push(editor);
		}
	}

}
