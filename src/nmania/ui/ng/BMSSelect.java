package nmania.ui.ng;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import nmania.Nmania;
import nmania.Settings;
import nmania.Skin;

public class BMSSelect extends ListScreen implements Runnable, IListSelectHandler {

	public String GetTitle() {
		return "SELECT A BEATMAPSET";
	}

	public boolean ShowLogo() {
		return true;
	}

	public String GetOption() {
		return "HELP";
	}

	public void OnOptionActivate(IDisplay d) {
		// TODO Auto-generated method stub

	}

	public void OnEnter(IDisplay d) {
		loadingState = true;
		(new Thread(this)).start();
	}

	public void run() {
		try {
			Nmania.LoadManager(Settings.workingFolder);
			if (Nmania.skin == null) {
				Nmania.skin = new Skin();
			}
			if (Nmania.skin.rich) {
				try {
					Nmania.skin.LoadRich(false);
				} catch (IllegalStateException e) {
					Thread.yield();
					Alert a = new Alert("nmania",
							"Failed to load your rich skin. A vector one will be used. Visit skinning menu to learn what went wrong.",
							null, AlertType.ERROR);
					a.setTimeout(Alert.FOREVER);
					Nmania.Push(a);
				}
			}
			Enumeration e = Nmania.bm.list();
			Vector v = new Vector();

			while (e.hasMoreElements()) {
				String s = e.nextElement().toString();
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
		// TODO Auto-generated method stub

	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		// TODO Auto-generated method stub

	}

}
