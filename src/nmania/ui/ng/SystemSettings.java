package nmania.ui.ng;

import nmania.Settings;

public class SystemSettings extends ListScreen implements IListSelectHandler {

	public SystemSettings() {
		SetItems(new ListItem[] { new SwitchItem(2, "Fullscreen flush", this, Settings.fullScreenFlush),
				new SwitchItem(3, "Record replays", this, Settings.recordReplay),
				new SwitchItem(4, "Keep UI during gameplay", this, Settings.keepMenu),
				new SwitchItem(5, "Throttle gameplay clock", this, Settings.throttleGameplay),
				new SwitchItem(6, "High priority", this, Settings.maxPriority), new SwitchItem(7, "Switch threads", this, Settings.forceThreadSwitch),
				new SwitchItem(8, "Analyze beatmaps", this, false), });
	}

	public String GetTitle() {
		return "SYSTEM SETTINGS";
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 2:
			Settings.fullScreenFlush = !Settings.fullScreenFlush;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 3:
			Settings.recordReplay = !Settings.recordReplay;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 4:
			Settings.keepMenu = !Settings.keepMenu;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 5:
			Settings.throttleGameplay = !Settings.throttleGameplay;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 6:
			Settings.maxPriority = !Settings.maxPriority;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 7:
			((SwitchItem) item).state = !((SwitchItem) item).state;
			Settings.forceThreadSwitch = !Settings.forceThreadSwitch;
			break;
		case 8:
			break;
		default:
			break;
		}

	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean ShowLogo() {
		return false;
	}
}
