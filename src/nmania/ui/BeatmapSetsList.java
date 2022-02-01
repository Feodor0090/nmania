package nmania.ui;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import nmania.BeatmapManager;
import nmania.BeatmapSetPage;
import nmania.Nmania;

public class BeatmapSetsList extends List implements CommandListener {

	private BeatmapManager bm;

	public BeatmapSetsList(BeatmapManager bm) throws IOException {
		super("Beatmapset select", List.IMPLICIT);
		setCommandListener(this);
		this.bm = bm;
		Enumeration e = bm.list();
		while (e.hasMoreElements()) {
			String s = e.nextElement().toString();
			if (s.charAt(s.length() - 1) != '/')
				continue;
			append(s.substring(0, s.length() - 1), null);
		}
	}

	public void commandAction(Command c, Displayable arg1) {
		if (c == List.SELECT_COMMAND) {
			Nmania.Push(new BeatmapSetPage(bm, getString(getSelectedIndex())));
		}
	}

}