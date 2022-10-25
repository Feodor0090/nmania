package nmania.ui.ng;

import javax.microedition.lcdui.Displayable;

import nmania.Nmania;
import nmania.Settings;
import nmania.ui.KeyboardSetup;

public class InputSettings extends ListScreen implements IListSelectHandler {

	public InputSettings() {

	}

	public String GetTitle() {
		return "INPUT SETTINGS";
	}

	public String GetOption() {
		return "RESET ALL";
	}

	public void OnOptionActivate(IDisplay d) {
		Settings.keyLayout = new int[10][];
		OnEnter(d);
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

	public void OnEnter(IDisplay d) {
		ListItem[] items = new ListItem[10];
		for (int i = 0; i < items.length; i++) {
			items[i] = new DataItem(i + 1, (i + 1) + "K keyboard layout", this,
					Settings.keyLayout[i] == null ? "not set" : "");
		}
		SetItems(items);
	}

	public void OnResume(IDisplay d) {
		OnEnter(d);
	}

}
