package nmania.ui;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import nmania.IInputOverrider;
import nmania.Nmania;
import nmania.PlayerBootstrapData;
import nmania.replays.ReplayChunk;
import nmania.replays.ReplayPlayer;
import nmania.replays.osu.OsuReplay;

public final class ReplaySelector extends List implements CommandListener {

	private Command back = new Command(Nmania.commonText[0], Command.BACK, 1);
	private Displayable prev;
	private PlayerBootstrapData data;

	public ReplaySelector(PlayerBootstrapData data, Displayable prev) {
		super("Select a replay", Choice.IMPLICIT);
		this.prev = prev;
		setCommandListener(this);
		this.data = data;
		String[] list = data.set.ListAllReplays();
		for (int i = 0; i < list.length; i++) {
			append(list[i], null);
		}
		addCommand(back);
	}

	public void commandAction(Command c, Displayable arg1) {
		if (c == List.SELECT_COMMAND && size() > 0) {
			String name = getString(getSelectedIndex());
			try {
				OsuReplay r = data.set.ReadReplay(name);
				ReplayChunk chunk = r.GetReplay();
				if(chunk == null)
				{
					Nmania.Push(new Alert("nmania", "Could not read replay.", null, AlertType.ERROR));
					return;
				}
				
				data.mods = r.GetMods();
				IInputOverrider input = new ReplayPlayer(chunk, r);
				Nmania.Push(new ResultsScreen(data, r, input, null, null, null, null, this));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (c == back) {
			Nmania.Push(prev);
		}
	}
}
