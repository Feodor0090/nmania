package nmania.ui.ng;

import nmania.Settings;

public class AudioSettings extends ListScreen implements IListSelectHandler {
	public AudioSettings() {
		SetItems(new ListItem[] { new SwitchItem(0, "Play music in menu", this, false),
				new SwitchItem(1, "Enable hitsounds", this, Settings.hitSamples),
				new SwitchItem(2, "Enable feedback samples", this, Settings.gameplaySamples),
				new SwitchItem(3, "Use BMS's sounds", this, Settings.useBmsSamples),
				new DataItem(4, "Clock offset", this, Settings.gameplayOffset + "ms"), });
	}

	public String GetTitle() {
		return "AUDIO SETTINGS";
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			Settings.musicInMenu = !Settings.musicInMenu;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 1:
			Settings.hitSamples = !Settings.hitSamples;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 2:
			Settings.gameplaySamples = !Settings.gameplaySamples;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 3:
			Settings.useBmsSamples = !Settings.useBmsSamples;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 4:

			break;

		default:
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		if (item.UUID != 4) {
			OnSelect(item, screen, display);
			return;
		}
		Settings.gameplayOffset += direction * 5;
		((DataItem) item).data = Settings.gameplayOffset + "ms";
	}

	public boolean ShowLogo() {
		return false;
	}
}