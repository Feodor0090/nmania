package nmania.ui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import nmania.Nmania;
import nmania.Skin;

public class VectorSkinSetup extends Form implements CommandListener, ItemCommandListener {

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
		noteColors.setItemCommandListener(this);
		noteColors.setDefaultCommand(edit);
		append(noteColors);
		holdColors.setItemCommandListener(this);
		holdColors.setDefaultCommand(edit);
		append(holdColors);
		append(noteFillOptions);
		append("No more settings can be changed for now. Check this menu later.");
	}

	private final Command back = new Command("Back", Command.BACK, 1);
	private final Command edit = new Command("Edit", Command.ITEM, 1);
	final SkinSelect prev;
	final Skin skin = Nmania.skin;

	Gauge colW = new Gauge("Column width", true, 150, skin.columnWidth);
	Gauge keysH = new Gauge("Keyboard height", true, 200, skin.keyboardHeight);
	Gauge leftO = new Gauge("Left offset", true, 800, skin.leftOffset);
	Gauge holdW = new Gauge("Hold trail width", true, 150, skin.holdWidth);
	Gauge noteH = new Gauge("Note height", true, 100, skin.noteHeight);
	StringItem noteColors = new StringItem(null, "Notes pallete", StringItem.BUTTON);
	StringItem holdColors = new StringItem(null, "Holds pallete", StringItem.BUTTON);
	ChoiceGroup noteFillOptions = new ChoiceGroup("Note fill options", Choice.MULTIPLE,
			new String[] { "Use vertical gradient", "Use different palletes for notes and hold heads" }, null);

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

	public void commandAction(Command c, Item i) {
		if (c == edit) {
			int[] pallete;
			if (i == noteColors) {
				pallete = skin.noteColors;
			} else if (i == holdColors) {
				pallete = skin.holdColors;
			} else {
				return;
			}
			Nmania.Push(new ColorPalleteEditor(pallete, this));
		}
	}
}
