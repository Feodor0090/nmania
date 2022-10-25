package nmania.ui;

import javax.microedition.lcdui.Alert;
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

	public VectorSkinSetup(Displayable ss) {
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
		noteFillOptions.setSelectedIndex(0, skin.verticalGradientOnNotes);
		noteFillOptions.setSelectedIndex(1, skin.holdsHaveOwnColors);
		append(noteFillOptions);
		keyColors.setItemCommandListener(this);
		keyColors.setDefaultCommand(edit);
		append(keyColors);
		holdKeyColors.setItemCommandListener(this);
		holdKeyColors.setDefaultCommand(edit);
		append(holdKeyColors);
	}

	private final Command back = new Command("Back", Command.BACK, 2);
	private final Command edit = new Command("Edit", Command.ITEM, 1);
	final Displayable prev;
	final Skin skin = Nmania.skin;

	Gauge colW = new Gauge("Column width", true, 150, skin.columnWidth);
	Gauge keysH = new Gauge("Keyboard height", true, 200, skin.keyboardHeight);
	Gauge leftO = new Gauge("Left offset", true, 800, skin.leftOffset);
	Gauge holdW = new Gauge("Hold trail width", true, 150, skin.holdWidth);
	Gauge noteH = new Gauge("Note height", true, 100, skin.noteHeight);
	StringItem noteColors = new StringItem(null, "Notes pallete", Item.BUTTON);
	StringItem holdColors = new StringItem(null, "Holds pallete", Item.BUTTON);
	ChoiceGroup noteFillOptions = new ChoiceGroup("Note fill options", Choice.MULTIPLE,
			new String[] { "Use vertical gradient", "Use different palletes for notes and hold heads" }, null);
	StringItem keyColors = new StringItem(null, "Keys pallete", Item.BUTTON);
	StringItem holdKeyColors = new StringItem(null, "Holded keys pallete", Item.BUTTON);

	void Apply() {
		skin.columnWidth = colW.getValue();
		skin.keyboardHeight = keysH.getValue();
		skin.leftOffset = leftO.getValue();
		skin.holdWidth = holdW.getValue();
		skin.noteHeight = noteH.getValue();
		skin.verticalGradientOnNotes = noteFillOptions.isSelected(0);
		skin.holdsHaveOwnColors = noteFillOptions.isSelected(1);
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
			if (i == noteColors) {
				Nmania.Push(new ColorPalleteEditor(skin.noteColors, this));
			} else if (i == holdColors) {
				Nmania.Push(new ColorPalleteEditor(skin.holdColors, this));
			} else if (i == keyColors) {
				Nmania.Push(new ColorPalleteEditor(skin.keyColors, this));
			} else if (i == holdKeyColors) {
				Nmania.Push(new ColorPalleteEditor(skin.holdKeyColors, this));
			} else {
				Nmania.Push(new Alert(i.toString()));
			}
		}
	}
}
