package nmania.ui.ng;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import nmania.Nmania;
import nmania.Settings;

public class SettingsScreen extends ListScreen implements IListSelectHandler, CommandListener {
	public SettingsScreen() {
	}

	public String GetTitle() {
		return "SETTINGS";
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	IDisplay disp;

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		switch (item.UUID) {
		case 0:
			display.Push(new InputSettings());
			break;
		case 1:
			display.Push(new VisualSettings());
			break;
		case 2:
			display.Push(new AudioSettings());
			break;
		case 3:
			display.Push(new SystemSettings());
			break;
		case 5:
			display.Push(new DiskSelectScreen());
			break;
		case 6:
			display.PauseRendering();
			disp = display;
			final TextBox box = new TextBox("What's your name?", Settings.name, 50, 0);
			box.addCommand(new Command("Next", Command.OK, 0));
			box.setCommandListener(this);
			Nmania.Push(box);
			break;
		case 7:
			Settings.Import(display);
			break;
		case 8:
			Settings.Save();
			Settings.Export(display);
			break;
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean OnExit(IDisplay d) {
		Settings.Save();
		return false;
	}

	public void OnResume(IDisplay d) {
		SetItems(new ListItem[] { new ListItem(0, "Input settings", this), new ListItem(1, "Visual settings", this),
				new ListItem(2, "Audio settings", this), new ListItem(3, "System settings", this),
				new DataItem(5, "Working folder", this, Settings.workingFolder),
				new DataItem(6, "Player's name", this, Settings.name), new ListItem(7, "Import...", this),
				new ListItem(8, "Export...", this) });
	}

	public void OnEnter(IDisplay d) {
		OnResume(d);
	}

	public boolean ShowLogo() {
		return true;
	}

	public void commandAction(Command arg0, Displayable d) {
		if (d instanceof TextBox) {
			String name = ((TextBox) d).getString().trim();
			if (name.length() == 0)
				return;
			name = name.replace('\n', ' ');
			Settings.name = name;
			Nmania.Push(disp.GetDisplayable());
		}
	}
}
