package nmania.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import nmania.Nmania;

public class VectorSkinSetup extends Form implements CommandListener {

	public VectorSkinSetup(SkinSelect ss) {
		super("Vector skin setup");
		prev = ss;
		addCommand(back);
		setCommandListener(this);
		append("No settings can be changed for now. Check this menu later.");
	}

	private final Command back = new Command("Back", Command.BACK, 1);
	final SkinSelect prev;

	public void commandAction(Command c, Displayable arg1) {
		if (c == back) {
			Nmania.Push(prev);
			return;
		}
	}
}
