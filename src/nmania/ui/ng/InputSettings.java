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
		Nmania.Push(new KeyboardSetup(item.UUID, (Displayable) display));
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean ShowLogo() {
		return false;
	}

	public void OnEnter(IDisplay d) {
		ListItem[] items = new ListItem[10];
		for (int i = 0; i < 10; i++) {
			String st;
			if (Settings.keyLayout[i] == null) {
				st = "not set";
			} else {
				boolean nonZero = false;
				for (int j = 0; j <= i; j++) {
					if (Settings.keyLayout[i][j] != 0) {
						nonZero = true;
						break;
					}
				}
				st = nonZero ? "" : "empty";
			}
			items[i] = new DataItem(i + 1, (i + 1) + "K keyboard layout", this, st);
		}
		SetItems(items);
	}

	public void OnResume(IDisplay d) {
		OnEnter(d);
	}

}
