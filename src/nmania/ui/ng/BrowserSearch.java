package nmania.ui.ng;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import nmania.Nmania;

public class BrowserSearch extends ListScreen implements IListSelectHandler, CommandListener {

	DataItem request = new DataItem(0, "Request", this, "(enter keywords)");
	SwitchItem notRanked = new SwitchItem(1, "Show not approved", this, false);

	public String GetTitle() {
		return "SEARCH FOR BEATMAPS";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "SEARCH";
	}

	public void OnOptionActivate(IDisplay d) {
		d.Push(new BrowserList(request.data, notRanked.state));
	}

	public void OnEnter(IDisplay d) {
		SetItems(new ListItem[] { request, notRanked });
	}

	IDisplay disp;

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			display.PauseRendering();
			disp = display;
			final TextBox box = new TextBox("Enter keywords", "", 50, 0);
			box.addCommand(new Command("Next", Command.OK, 0));
			box.setCommandListener(this);
			Nmania.Push(box);
			break;
		case 1:
			notRanked.state = !notRanked.state;
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public void commandAction(Command arg0, Displayable d) {
		if (d instanceof TextBox) {
			String name = ((TextBox) d).getString().trim();
			name = name.replace('\n', ' ');
			Nmania.Push(disp.GetDisplayable());
			request.data = name;
		}
	}
}
