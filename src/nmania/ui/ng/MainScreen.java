package nmania.ui.ng;

import nmania.Nmania;
import nmania.ui.InfoScreen;

public class MainScreen extends ListScreen implements IListSelectHandler {

	public MainScreen() {
		SetItems(new ListItem[] { new ListItem(0, "Play solo", this), // new ListItem(1, "Play online", this),
				new ListItem(2, "Browse maps", this), new ListItem(3, "Skinning", this),
				new ListItem(4, "Information", this) });
	}

	public String GetTitle() {
		return "MAIN MENU";
	}

	public String GetOption() {
		return "SETTINGS";
	}

	public void OnOptionActivate(IDisplay d) {
		d.Push(new SettingsScreen());
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			display.Push(new BMSSelect());
			break;
		case 2:
			display.Push(new BrowserSearch());
			break;
		case 3:
			display.Push(new SkinSelectScreen(display));
			break;
		case 4:
			display.PauseRendering();
			Nmania.Push(new InfoScreen(display.GetDisplayable()));
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean ShowLogo() {
		return true;
	}

}
