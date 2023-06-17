package nmania.ui.ng;

import nmania.Settings;

public class AudioSettings extends ListScreen implements IListSelectHandler, INumberBoxHandler {
	public AudioSettings() {
		SetItems(new ListItem[] { //
				new SwitchItem(0, "Play music in menu", this, Settings.musicInMenu), // ?full
				new SwitchItem(1, "Enable hitsounds", this, Settings.hitSamples), // ?full
				new SwitchItem(2, "Enable feedback samples", this, Settings.gameplaySamples), // ?full
				new SwitchItem(3, "Use BMS's sounds", this, Settings.useBmsSamples), // ?full
				new DataItem(4, "Clock offset", this, Settings.gameplayOffset + "ms"),
				new DataItem(5, "Audio volume", this, Settings.volume + "%"), });
	}

	public String GetTitle() {
		return "AUDIO SETTINGS";
	}

	public String GetOption() {
		return "HELP";
	}

	public void OnOptionActivate(IDisplay d) {
		d.Push(new Alert("AUDIO SETTINGS",
				"Feedback samples - sounds like restart, fail, pass, etc. \n BMS's sounds - usage of effects provided by loaded beatmap, not default ones."));
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
			display.Push(new OffsetBox(0, this, Settings.gameplayOffset, true));
			break;
		case 5:
			NumberBox nb = new NumberBox("Audio volume (%)", 1, this, Settings.volume, false);
			nb.showPlusMinus = true;
			display.Push(nb);
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
		if (UUID == 0) {
			if (newNumber < -1000)
				newNumber = -1000;
			if (newNumber > 1000)
				newNumber = 1000;
			Settings.gameplayOffset = newNumber;
			((DataItem) GetSelected()).data = Settings.gameplayOffset + "ms";
		} else if (UUID == 1) {
			if (newNumber < 1)
				newNumber = 1;
			if (newNumber > 100)
				newNumber = 100;
			Settings.volume = newNumber;
			((DataItem) GetSelected()).data = Settings.volume + "%";
		}
		d.Back();
	}
}
