package nmania.ui.ng;

public class BrowserList extends ListScreen {

	private String search;
	private boolean notRanked;

	public BrowserList(String search, boolean notRanked) {
		this.search = search;
		this.notRanked = notRanked;
	}

	public String GetTitle() {
		return search;
	}

	public boolean ShowLogo() {
		// TODO Auto-generated method stub
		return false;
	}

	public String GetOption() {
		// TODO Auto-generated method stub
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
		// TODO Auto-generated method stub

	}

}
