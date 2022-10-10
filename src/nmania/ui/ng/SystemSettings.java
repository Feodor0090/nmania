package nmania.ui.ng;

import nmania.Settings;

public class SystemSettings extends ListScreen implements IListSelectHandler {

	public SystemSettings() {
		SetItems(new ListItem[] { new SwitchItem(0, "Draw backgrounds", this, false),
				new SwitchItem(1, "Draw profiler", this, false), new SwitchItem(2, "Fullscreen flush", this, false),
				new SwitchItem(3, "Record replays", this, false),
				new SwitchItem(4, "Keep UI during gameplay", this, false),
				new SwitchItem(5, "Throttle gameplay clock", this, false),
				new SwitchItem(6, "High priority", this, false), new SwitchItem(7, "Switch threads", this, false),
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
		case 0:

			break;
		case 1:
			Settings.profiler = !Settings.profiler;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 2:
			Settings.fullScreenFlush = !Settings.fullScreenFlush;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 3:
			Settings.recordReplay = !Settings.recordReplay;
			((SwitchItem) item).state = !((SwitchItem) item).state;
		case 4:
			Settings.keepMenu = !Settings.keepMenu;
			((SwitchItem) item).state = !((SwitchItem) item).state;
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
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
