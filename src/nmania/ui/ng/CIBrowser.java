package nmania.ui.ng;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import nmania.Nmania;

// ??sidonly

public class CIBrowser extends ListScreen implements IListSelectHandler, CommandListener {

	public String branch = null;
	public boolean page = false;
	public DataItem branchField = new DataItem(1, "Branch", this, "(enter name)");
	private IDisplay display;
	final String folder = "http://nnp.nnchan.ru/nm/dev/";

	public String GetTitle() {
		return page ? branch : "CI";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return (branch != null && !page) ? "CHECK" : null;
	}

	public void OnOptionActivate(IDisplay d) {
		if (branch != null && !page) {
			page = true;
			try {
				String commit = Nmania.getUtf(folder + branch + ".txt");
				SetItems(new ListItem[] { new ListItem("Commit: " + commit, this),
						new ListItem(2, "Download debug", this), new ListItem(3, "Download normal", this) });
			} catch (Exception e) {
				SetItems(new ListItem[] { new ListItem(e.toString(), this) });
			}
		}
	}

	public void OnEnter(IDisplay d) {
		this.display = d;
		SetItems(new ListItem[] { branchField });
	}

	public void commandAction(Command arg0, Displayable d) {
		if (d instanceof TextBox) {
			branch = ((TextBox) d).getString().trim().toLowerCase().replace(' ', '-');
			branchField.data = branch;
			Nmania.Push(display.GetDisplayable());
		}
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		if (item.UUID == 1) {
			final TextBox box = new TextBox("Enter keywords", branch, 70, 0);
			box.addCommand(new Command("Close", Command.BACK, 0));
			box.setCommandListener(this);
			Nmania.Push(box);
			return;
		}
		if (item.UUID == 2) {
			Nmania.open(folder + "debug-" + branch + ".jar");
			return;
		}
		if (item.UUID == 3) {
			Nmania.open(folder + "normal-" + branch + ".jar");
			return;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

}
