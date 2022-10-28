package nmania.ui.ng;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import nmania.Settings;

public class FolderSelectScreen extends ListScreen implements IListSelectHandler {

	private String root;
	boolean failed = false;

	public FolderSelectScreen(String root) {
		this.root = root;
		Refetch();
	}

	public Vector path = new Vector();

	public String GetTitle() {
		if (failed)
			return "Could not read FS here.";
		return CombinePath();
	}

	public String CombinePath() {
		StringBuffer sb = new StringBuffer();
		sb.append(root);
		for (int i = 0; i < path.size(); i++) {
			sb.append(path.elementAt(i).toString());
		}
		return sb.toString();
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		if (failed)
			return null;
		return "USE THIS";
	}

	public void OnOptionActivate(IDisplay d) {
		if (!failed) {
			Settings.workingFolder = CombinePath();
			Settings.Save();
			path.removeAllElements();
			d.Back();
			d.Back();
		}
	}

	public void Refetch() {
		try {
			failed = false;
			FileConnection fc = null;
			try {
				fc = (FileConnection) Connector.open("file:///" + CombinePath(), Connector.READ);
				Vector v = new Vector();
				Enumeration e = fc.list();
				while (e.hasMoreElements()) {
					String root = (String) e.nextElement();
					if (root.charAt(root.length() - 1) == '/')
						v.addElement(new ListItem(root, this));
				}
				SetItems(v);
			} finally {
				if (fc != null)
					fc.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			SetItems(new Vector());
			failed = true;
		}
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		path.addElement(item.text);
		Refetch();
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public boolean OnExit(IDisplay d) {
		if (path.size() == 0)
			return false;
		path.removeElementAt(path.size() - 1);
		Refetch();
		return true;
	}

}
