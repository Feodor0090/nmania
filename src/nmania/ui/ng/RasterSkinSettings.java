package nmania.ui.ng;

import nmania.Nmania;
import nmania.Settings;
import nmania.skin.RasterSkin;
import nmania.skin.Skin;

public class RasterSkinSettings extends SkinSettings implements IListSelectHandler, INumberBoxHandler {

	private final RasterSkin skin;
	private final DataItem left, holdW;

	private final String[] pal3 = new String[] { "Non-odd column", "Odd column", "Center column" };

	public RasterSkinSettings() {
		Skin s = Nmania.LoadSkin(true);
		if (s instanceof RasterSkin)
			skin = (RasterSkin) s;
		else {
			skin = null;
			left = null;
			holdW = null;
			SetItems(new ListItem[] { new ListItem("Failed to load skin", null) });
			return;
		}
		left = new DataItem(1, "Offset from left", this, skin.leftOffset + "px");
		holdW = new DataItem(2, "Holds width", this, skin.holdWidth + "px");
		SetItems(new ListItem[] { new ListItem(0, "General info", this), left, holdW,
				new ListItem(3, "Hold bodies color", this),
				new CheckItem(4, "Keyboard sprites", this, skin.VerifyKb() == null),
				new CheckItem(5, "Note sprites", this, skin.VerifyNotes() == null),
				new CheckItem(6, "HUD sprites", this, skin.VerifyHud() == null),
				new CheckItem(7, "Sizes consistency", this, skin.VerifyWidth() == null), });
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		String check = null;
		switch (item.UUID) {
		case 0:
			display.Push(new Alert(item.text, "Raster skin is skin which uses arbitary images to draw the game. "
					+ "Create \"_skin\" folder in your folder with songs (\"file:///" + Settings.workingFolder
					+ "_skin/\") and place your images there. "
					+ "Create 3 images (\"1\", \"2\", \"3\" for non-odd, odd and center columns) "
					+ "for keyboard (\"kbX.png\" and \"kbhX.png\" where X is number) and notes (\"noteX.png\" and \"holdX.png\") "
					+ "and 12 images for numbers (\"hudX.png\" where X is 0-9, \",\" and \"%\"). "
					+ "All images in one category must have equal sizes. Notes and keyboard must have equal width. "
					+ "Refer to checks below to learn what to fix."));
			break;
		case 1:
			display.Push(new NumberBox(item.text, 1, this, skin.leftOffset, false));
			break;
		case 2:
			display.Push(new NumberBox(item.text, 4, this, skin.holdWidth, false));
			break;
		case 3:
			display.Push(new PalleteBox(item.text, skin.holdBodies, pal3));
			break;
		case 4:
			check = skin.VerifyKb();
			display.Push(new Alert(item.text, check == null ? "Keyboard sprites are okay!" : check));
			break;
		case 5:
			check = skin.VerifyNotes();
			display.Push(new Alert(item.text, check == null ? "Note sprites are okay!" : check));
			break;
		case 6:
			check = skin.VerifyHud();
			display.Push(new Alert(item.text, check == null ? "HUD sprites are okay!" : check));
			break;
		case 7:
			check = skin.VerifyWidth();
			display.Push(new Alert(item.text, check == null ? "Sprite sizes are okay!" : check));
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
			left.data = String.valueOf(newNumber) + "px";
			break;
		case 4:
			if (newNumber < 1)
				newNumber = 1;
			if (newNumber > skin.GetColumnWidth())
				newNumber = skin.GetColumnWidth();
			skin.holdWidth = newNumber;
			holdW.data = String.valueOf(newNumber) + "px";
			break;
		}

		d.Back();
	}

}
