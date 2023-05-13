package nmania.ui.ng;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import nmania.Nmania;
import nmania.Settings;

public class BMSSelect extends ListScreen implements Runnable, IListSelectHandler {

	private IDisplay disp;

	public String GetTitle() {
		return "SELECT A CHART";
	}

	public boolean ShowLogo() {
		return true;
	}

	public String GetOption() {
		return "HELP";
	}

	public void OnOptionActivate(IDisplay d) {
		d.Push(new Alert("SELECT A CHART",
				"Your working folder is \"" + Settings.workingFolder
						+ "\". Place folders with charts there to see them ingame. Look at the structure: \n "
						+ Settings.GetLastDir()
						+ " \n > chart1/ \n > > map [easy].osu \n > > background.jpg \n > > track.mp3"));
	}

	public void OnEnter(IDisplay d) {
		loadingState = true;
		disp = d;
		(new Thread(this)).start();
	}

	public void run() {
		try {
			Enumeration en;
			try {
				Nmania.LoadManager(Settings.workingFolder);
				en = Nmania.bm.list();
			} catch (IOException e) {
				Thread.sleep(1001);
				loadingState = false;
				disp.Push(new Alert("Failed to load charts",
						"Current working folder " + Settings.workingFolder
								+ " can't be accessed. Visit settings section and choose an existing folder.",
						"CHANGE NOW", new DiskSelectScreen(), 2));
				return;
			}

			Vector v = new Vector();

			while (en.hasMoreElements()) {
				String s = en.nextElement().toString();
				if (s.charAt(0) == '_')
					continue;
				if (s.charAt(s.length() - 1) == '/') {
					v.addElement(new ListItem(1, s.substring(0, s.length() - 1), this));
				}
				if (s.endsWith(".osz")) {
					v.addElement(new ListItem(-1, s, this));
				}
			}
			SetItems(v);
			loadingState = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		if (item.UUID == 1)
			display.Push(new DifficultySelect(Nmania.bm, item.text));
		else if (item.UUID == -1) {
			display.Push(new Alert("Packed beatmapset",
					item.text + " is an OSZ (zipped beatmapset). Do you want to unpack it?", "UNPACK",
					new BeatmapUnpacker(item.text), 2));
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

}
