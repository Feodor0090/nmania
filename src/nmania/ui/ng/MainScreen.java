package nmania.ui.ng;

public class MainScreen extends ListScreen implements IListSelectHandler {

	public MainScreen() {
		SetItems(new ListItem[] { new ListItem(0, "Play solo", this), new ListItem(1, "Play online", this),
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
		// TODO Auto-generated method stub

	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		// TODO Auto-generated method stub
		
	}

	public boolean ShowLogo() {
		return true;
	}

}
