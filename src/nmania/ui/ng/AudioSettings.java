package nmania.ui.ng;

import nmania.Settings;

public class AudioSettings extends ListScreen implements IListSelectHandler, INumberBoxHandler {
	public AudioSettings() {
		SetItems(new ListItem[] { //
				new SwitchItem(0, "Play music in menu", this, Settings.musicInMenu), // ?full
				new SwitchItem(1, "Enable hitsounds", this, Settings.hitSamples), // ?full
				new SwitchItem(2, "Enable feedback samples", this, Settings.gameplaySamples), // ?full
				new SwitchItem(3, "Use BMS's sounds", this, Settings.useBmsSamples), // ?full
				new DataItem(4, "Clock offset", this, Settings.gameplayOffset + "ms"), });
	}

	public String GetTitle() {
		return "AUDIO SETTINGS";
	}

	public String GetOption() {
		return "HELP";
	}

	public void OnOptionActivate(IDisplay d) {
		d.Push(new Alert("AUDIO SETTINGS", "Feedback samples - sounds like restart, fail, pass, etc. \n BMS's sounds - usage of effects provided by loaded beatmap, not default ones. \n Clock offset - set to positive to make notes appear earlier than music, set to negative to make notes appear before than music. Value is in milliseconds (1000 is 1 second)."));
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			Settings.musicInMenu = !Settings.musicInMenu;
			((SwitchItem) item).Toggle();
			if (!Settings.musicInMenu) {
				display.SetAudio(null);
			}
			break;
		case 1:
			Settings.hitSamples = !Settings.hitSamples;
			((SwitchItem) item).Toggle();
			break;
		case 2:
			Settings.gameplaySamples = !Settings.gameplaySamples;
			((SwitchItem) item).Toggle();
			break;
		case 3:
			Settings.useBmsSamples = !Settings.useBmsSamples;
			((SwitchItem) item).Toggle();
			break;
		case 4:
			display.Push(new NumberBox("Clock offset", 0, this, Settings.gameplayOffset, true));
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		if (item.UUID != 4) {
			OnSelect(item, screen, display);
			return;
		}
		int newNumber = Settings.gameplayOffset + direction * 5;
		if (newNumber < -1000)
			newNumber = -1000;
		if (newNumber > 1000)
			newNumber = 1000;
		Settings.gameplayOffset = newNumber;
		((DataItem) item).data = Settings.gameplayOffset + "ms";
	}

	public boolean ShowLogo() {
		return false;
	}

	public void OnNumberEntered(int UUID, int newNumber, IDisplay d) {
		if (newNumber < -1000)
			newNumber = -1000;
		if (newNumber > 1000)
			newNumber = 1000;
		Settings.gameplayOffset = newNumber;
		d.Back();
		((DataItem) GetSelected()).data = Settings.gameplayOffset + "ms";
	}
}
