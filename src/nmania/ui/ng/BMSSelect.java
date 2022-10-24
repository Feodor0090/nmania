package nmania.ui.ng;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import nmania.GL;
import nmania.Nmania;
import nmania.Settings;
import nmania.Skin;

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
		d.Push(new Alert("SELECT A CHART", "Your working folder is \"" + Settings.workingFolder
				+ "\". Place folders with charts there to see them ingame. Check \"information\" section in main menu for more info."));
	}

	public void OnEnter(IDisplay d) {
		loadingState = true;
		disp = d;
		(new Thread(this)).start();
		GL.Create();// ?dbg
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
			if (Nmania.skin == null) {
				Nmania.skin = new Skin();
			}
			if (Nmania.skin.rich) {
				try {
					Nmania.skin.LoadRich(false);
				} catch (IllegalStateException e) {
					Thread.sleep(1001);
					disp.Push(new Alert("Failed to load rich skin",
							"A vector one will be used. Visit skinning menu to learn what went wrong."));
				}
			}
			Vector v = new Vector();

			while (en.hasMoreElements()) {
				String s = en.nextElement().toString();
				if (s.charAt(0) == '_')
					continue;
				if (s.charAt(s.length() - 1) != '/')
					continue;
				v.addElement(s.substring(0, s.length() - 1));
			}
			ListItem[] l = new ListItem[v.size()];
			for (int i = 0; i < v.size(); i++) {
				l[i] = new ListItem(i, v.elementAt(i).toString(), this);
			}
			SetItems(l);
			loadingState = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		display.Push(new DifficultySelect(Nmania.bm, item.text));
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

}
