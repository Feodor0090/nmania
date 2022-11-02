package nmania.ui.ng;

import nmania.Settings;

public class VisualSettings extends ListScreen implements IListSelectHandler, INumberBoxHandler {

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
		case 0:
			display.Push(new NumberBox("Dim %", 0, this, Settings.dimLevel, false));
			break;
		case 1:
			display.Push(new NumberBox("Scroll speed (divider)", 1, this, Settings.speedDiv, false));
			break;
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

	public void OnNumberEntered(int UUID, int newNumber, IDisplay d) {
		if (UUID == 0) {
			if (newNumber < 0)
				newNumber = 0;
			if (newNumber > 100)
				newNumber = 100;
			Settings.dimLevel = newNumber;
			d.Back();
			((DataItem) GetSelected()).data = Settings.dimLevel + "%";
		} else if (UUID == 1) {
			if (newNumber < 1)
				newNumber = 1;
			if (newNumber > 50)
				newNumber = 50;
			Settings.speedDiv = newNumber;
			d.Back();
			((DataItem) GetSelected()).data = "1px/" + Settings.speedDiv + "ms";
		}
	}
}
