package nmania.ui.ng;

import javax.microedition.lcdui.Displayable;

import nmania.Nmania;
import nmania.Settings;
import nmania.ui.KeyboardSetup;

public class InputSettings extends ListScreen implements IListSelectHandler {

	public InputSettings() {
		ListItem[] items = new ListItem[10];
		for (int i = 0; i < items.length; i++) {
			items[i] = new ListItem(i + 1, (i + 1) + "K keyboard layout", this);
		}
		SetItems(items);
	}

	public String GetTitle() {
		return "INPUT SETTINGS";
	}

	public String GetOption() {
		return "RESET ALL";
	}

	public void OnOptionActivate(IDisplay d) {
		Settings.keyLayout = new int[10][];
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		display.PauseRendering();
		Nmania.Push(new KeyboardSetup(item.UUID, (Displayable) display));
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean ShowLogo() {
		return false;
	}

}
