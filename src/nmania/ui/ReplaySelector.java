package nmania.ui;

import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import nmania.BeatmapSet;
import nmania.Nmania;

public final class ReplaySelector extends List implements CommandListener {

	private BeatmapSet set;
	private Command back = new Command(Nmania.commonText[0], Command.BACK, 1);
	private Displayable prev;

	public ReplaySelector(BeatmapSet set, Displayable prev) {
		super("Select a replay", Choice.IMPLICIT);
		this.prev = prev;
		setCommandListener(this);
		this.set = set;
		String[] list = set.ListAllReplays();
		for (int i = 0; i < list.length; i++) {
			append(list[i], null);
		}
		addCommand(back);
	}

	public void commandAction(Command c, Displayable arg1) {
		if (c == List.SELECT_COMMAND && size() > 0) {
			String name = getString(getSelectedIndex());
			try {
				set.ReadReplay(name).DecodeData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (c == back) {
			Nmania.Push(prev);
		}
	}
}
