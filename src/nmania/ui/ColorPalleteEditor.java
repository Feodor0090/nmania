package nmania.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import nmania.Nmania;
import symnovel.SNUtils;

public class ColorPalleteEditor extends Form implements CommandListener {

	public ColorPalleteEditor(int[] colors, Displayable prev) {
		super("Pallete editor");
		this.prev = prev;
		fields = new TextField[colors.length];
		for (int i = 0; i < colors.length; i++) {
			String curr = Integer.toHexString(colors[i]);
			while (curr.length() < 6)
				curr = "0" + curr;
			fields[i] = new TextField("Color " + (i + 1) + " (HEX value):", curr, 6, 0);
		}
		setCommandListener(this);
		addCommand(back);
	}

	private final Command back = new Command("Back", Command.BACK, 1);
	final Displayable prev;
	int[] colors;
	TextField[] fields;

	public void commandAction(Command c, Displayable arg1) {
		int i = 0;
		try {
			for (i = 0; i < colors.length; i++) {
				colors[i] = SNUtils.toARGB(fields[i].getString());
			}
			Nmania.Push(prev);
		} catch (Exception e) {
			Nmania.Push(new Alert("Pallete editor", "Color " + (i + 1) + " is invalid. Check the field.", null,
					AlertType.ERROR));
		}

	}

}
