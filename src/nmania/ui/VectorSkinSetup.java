package nmania.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;

import nmania.Nmania;
import nmania.Skin;

public class VectorSkinSetup extends Form implements CommandListener {

	public VectorSkinSetup(SkinSelect ss) {
		super("Vector skin setup");
		prev = ss;
		addCommand(back);
		setCommandListener(this);
		append(colW);
		append(keysH);
		append(leftO);
		append(holdW);
		append(noteH);
		append("No more settings can be changed for now. Check this menu later.");
	}

	private final Command back = new Command("Back", Command.BACK, 1);
	final SkinSelect prev;
	final Skin skin = Nmania.skin;
	
	Gauge colW = new Gauge("Column width", true, 150, skin.columnWidth);
	Gauge keysH = new Gauge("Keyboard height", true, 200, skin.keyboardHeight);
	Gauge leftO = new Gauge("Left offset", true, 800, skin.leftOffset);
	Gauge holdW = new Gauge("Hold trail width", true, 150, skin.holdWidth);
	Gauge noteH = new Gauge("Note height", true, 100, skin.noteHeight);

	void Apply() {
		skin.columnWidth = colW.getValue();
		skin.keyboardHeight = keysH.getValue();
		skin.leftOffset = leftO.getValue();
		skin.holdWidth = holdW.getValue();
		skin.noteHeight = noteH.getValue();
	}
	public void commandAction(Command c, Displayable arg1) {
		if (c == back) {
			Apply();
			skin.Save();
			Nmania.Push(prev);
			return;
		}
	}
}
