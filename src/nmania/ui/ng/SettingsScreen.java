package nmania.ui.ng;

import nmania.Settings;

public class SettingsScreen extends ListScreen implements IListSelectHandler {
	public SettingsScreen() {
		SetItems(new ListItem[] { new ListItem(0, "Input settings", this), new ListItem(1, "Visual settings", this),
				new ListItem(2, "Audio settings", this), new ListItem(3, "System settings", this),
				new DataItem(4, "Language", this, Settings.locale), new ListItem(5, "Working folder", this),
				new DataItem(6, "Player's name", this, Settings.name), });
	}

	public String GetTitle() {
		return "SETTINGS";
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			display.Push(new InputSettings());
			break;
		case 1:
			display.Push(new VisualSettings());
			break;
		case 2:
			display.Push(new AudioSettings());
			break;
		case 3:
			display.Push(new SystemSettings());
			break;
		default:
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public void OnExit(IDisplay d) {
		Settings.Save();
	}

	public boolean ShowLogo() {
		return true;
	}
}
