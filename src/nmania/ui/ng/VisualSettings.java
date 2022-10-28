package nmania.ui.ng;

import nmania.Settings;

public class VisualSettings extends ListScreen implements IListSelectHandler {

	public VisualSettings() {
		SetItems(new ListItem[] { //
				new DataItem(0, "Dim level", this, Settings.dimLevel + "%"), // ?full
				new DataItem(1, "Scroll speed", this, "1px/" + Settings.speedDiv + "ms"),
				new SwitchItem(2, "Show HUD", this, Settings.drawHUD),
				new SwitchItem(3, "Show fps", this, Settings.profiler), });
	}

	public String GetTitle() {
		return "VISUAL SETTINGS";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 2:
			Settings.drawHUD = !Settings.drawHUD;
			((SwitchItem) item).Toggle();
			break;
		case 3:
			Settings.profiler = !Settings.profiler;
			((SwitchItem) item).Toggle();
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			Settings.dimLevel += direction * 5;
			if (Settings.dimLevel < 0)
				Settings.dimLevel = 0;
			if (Settings.dimLevel > 100)
				Settings.dimLevel = 100;
			((DataItem) item).data = Settings.dimLevel + "%";
			break;
		case 1:
			Settings.speedDiv += direction;
			if (Settings.speedDiv < 1)
				Settings.speedDiv = 1;
			((DataItem) item).data = "1px/" + Settings.speedDiv + "ms";
			break;
		}
	}

}
