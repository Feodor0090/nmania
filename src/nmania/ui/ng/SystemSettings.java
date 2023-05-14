package nmania.ui.ng;

import nmania.Settings;

public class SystemSettings extends ListScreen implements IListSelectHandler {

	public SystemSettings() {
		SetItems(new ListItem[] { new SwitchItem(0, "Fullscreen flush", this, Settings.fullScreenFlush),
				new SwitchItem(2, "Record replays", this, Settings.recordReplay),
				new SwitchItem(3, "Encode replays into OSR", this, Settings.encodeOsr),
				new SwitchItem(4, "Keep UI during gameplay", this, Settings.keepMenu), // ?full
				new SwitchItem(5, "Throttle gameplay clock", this, Settings.throttleGameplay),
				new SwitchItem(6, "High priority", this, Settings.maxPriority),
				new SwitchItem(7, "Switch threads", this, Settings.forceThreadSwitch),
				new SwitchItem(8, "Analyze beatmaps", this, Settings.analyzeMaps), // ?full
				new SwitchItem(9, "Redraw column borders", this, Settings.redrawBorders), });
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
		((SwitchItem) item).Toggle();
		switch (item.UUID) {
		case 0:
			Settings.fullScreenFlush = !Settings.fullScreenFlush;
			break;
		case 2:
			Settings.recordReplay = !Settings.recordReplay;
			break;
		case 3:
			Settings.encodeOsr = !Settings.encodeOsr;
			break;
		case 4:
			Settings.keepMenu = !Settings.keepMenu;
			break;
		case 5:
			Settings.throttleGameplay = !Settings.throttleGameplay;
			break;
		case 6:
			Settings.maxPriority = !Settings.maxPriority;
			break;
		case 7:
			Settings.forceThreadSwitch = !Settings.forceThreadSwitch;
			break;
		case 8:
			Settings.analyzeMaps = !Settings.analyzeMaps;
			break;
		case 9:
			Settings.redrawBorders = !Settings.redrawBorders;
			break;
		}

	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean ShowLogo() {
		return false;
	}
}
