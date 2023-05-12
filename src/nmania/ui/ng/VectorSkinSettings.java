package nmania.ui.ng;

import nmania.Nmania;
import nmania.skin.Skin;
import nmania.skin.VectorSkin;

public class VectorSkinSettings extends ListScreen implements IListSelectHandler, INumberBoxHandler {

	private final VectorSkin skin;
	private final DataItem left, colW, noteH, holdW, kbH;
	private final ListItem hudC, bgC, kbC, kbHC, noteC, holdC, holdBC;

	private final String[] pal3 = new String[] { "Non-odd column", "Odd column", "Center column" };
	private final String[] pal6 = new String[] { "Non-odd column (top)", "Non-odd column (bottom)", "Odd column (top)",
			"Odd column (bottom)", "Center column (top)", "Center column (bottom)" };

	public VectorSkinSettings() {
		Skin s = Nmania.LoadSkin(false);
		if (s instanceof VectorSkin)
			skin = (VectorSkin) s;
		else
			throw new IllegalArgumentException();

		left = new DataItem(1, "Offset from left", this, skin.leftOffset + "px");
		colW = new DataItem(2, "Columns & notes width", this, skin.columnWidth + "px");
		noteH = new DataItem(3, "Notes height", this, skin.noteHeight + "px");
		holdW = new DataItem(4, "Holds width", this, skin.holdWidth + "px");
		kbH = new DataItem(5, "Keyboard height", this, skin.keyboardHeight + "px");
		hudC = new ListItem(6, "Score & acc color", this);
		bgC = new ListItem(7, "Columns fill color", this);
		kbC = new ListItem(8, "Keyboard color", this);
		kbHC = new ListItem(9, "Keyboard color (pressed)", this);
		noteC = new ListItem(10, "Notes color", this);
		holdC = new ListItem(11, "Hold heads color", this);
		holdBC = new ListItem(12, "Hold bodies color", this);

		SetItems(new ListItem[] { left, colW, noteH, holdW, kbH, hudC, bgC, kbC, kbHC, noteC, holdC, holdBC });
	}

	public String GetTitle() {
		return "SKIN SETUP";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return null;
	}

	public boolean OnExit(IDisplay d) {
		Nmania.SaveSkin();
		return false;
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 1:
			display.Push(new NumberBox(item.text, 1, this, skin.leftOffset, false));
			break;
		case 2:
			display.Push(new NumberBox(item.text, 2, this, skin.columnWidth, false));
			break;
		case 3:
			display.Push(new NumberBox(item.text, 3, this, skin.noteHeight, false));
			break;
		case 4:
			display.Push(new NumberBox(item.text, 4, this, skin.holdWidth, false));
			break;
		case 5:
			display.Push(new NumberBox(item.text, 5, this, skin.keyboardHeight, false));
			break;
		case 6:
			display.Push(new ColorBox(item.text, 6, this, skin.hudColor));
			break;
		case 7:
			display.Push(new PalleteBox(item.text, skin.background, pal3));
			break;
		case 8:
			display.Push(new PalleteBox(item.text, skin.keyboard, pal6));
			break;
		case 9:
			display.Push(new PalleteBox(item.text, skin.keyboardHold, pal6));
			break;
		case 10:
			display.Push(new PalleteBox(item.text, skin.notes, pal6));
			break;
		case 11:
			display.Push(new PalleteBox(item.text, skin.holds, pal6));
			break;
		case 12:
			display.Push(new PalleteBox(item.text, skin.holdBodies, pal3));
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public void OnNumberEntered(int UUID, int newNumber, IDisplay d) {
		switch (UUID) {
		case 1:
			if (newNumber < 0)
				newNumber = 0;
			skin.leftOffset = newNumber;
			break;
		case 2:
			if (newNumber < 1)
				newNumber = 1;
			skin.columnWidth = newNumber;
			break;
		case 3:
			if (newNumber < 1)
				newNumber = 1;
			skin.noteHeight = newNumber;
			break;
		case 4:
			if (newNumber < 1)
				newNumber = 1;
			if (newNumber > skin.columnWidth)
				newNumber = skin.columnWidth;
			skin.holdWidth = newNumber;
			break;
		case 5:
			if (newNumber < 1)
				newNumber = 1;
			skin.keyboardHeight = newNumber;
			break;
		case 6:
			skin.hudColor = newNumber;
			break;
		}

		d.Back();
	}

}
