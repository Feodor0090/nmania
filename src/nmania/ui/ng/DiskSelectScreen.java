package nmania.ui.ng;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;

public class DiskSelectScreen extends ListScreen implements IListSelectHandler {

	public String GetTitle() {
		return "SELECT DISK";
	}

	public boolean ShowLogo() {
		return true;
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnEnter(IDisplay d) {
		Vector v = new Vector();
		Enumeration e = FileSystemRegistry.listRoots();
		while (e.hasMoreElements()) {
			String root = (String) e.nextElement();
			v.addElement(new ListItem(root, this));
		}
		SetItems(v);
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		display.Push(new FolderSelectScreen(item.text));
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

}
