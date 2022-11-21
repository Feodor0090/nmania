package nmania.ui.ng;

import nmania.Nmania;
import nmania.GL;

public class MainScreen extends ListScreen implements IListSelectHandler {

	public MainScreen() {
		SetItems(new ListItem[] { new ListItem(0, "Play solo", this), // new ListItem(1, "Play online", this),
				new ListItem(2, "Browse maps", this), new ListItem(3, "Skinning", this),
				new ListItem(4, "About (v" + Nmania.version + ")", this), //
				new ListItem(5, "CI builds", this) // ?sid
		});
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
			display.Push(new AboutScreen());
			break;
		case 5:
			display.Push(new CIBrowser()); // ?sid
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean ShowLogo() {
		return true;
	}

	protected void OnItemChange() {
		GL.Log("(ui) In main menu " + GetSelected().text + " is now selected."); // ?sid
	}

	public void OnResume(IDisplay d) {
		OnItemChange();
	}

}
