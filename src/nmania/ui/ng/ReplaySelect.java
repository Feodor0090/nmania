package nmania.ui.ng;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import nmania.IInputOverrider;
import nmania.Nmania;
import nmania.PlayerBootstrapData;
import nmania.replays.IExtendedReplay;
import nmania.replays.ReplayChunk;
import nmania.replays.ReplayPlayer;
import nmania.ui.ResultsScreen;

public class ReplaySelect extends ListScreen implements IListSelectHandler, Runnable {

	private PlayerBootstrapData data;

	public ReplaySelect(PlayerBootstrapData data) {
		this.data = data;
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		String name = item.text;
		try {
			IExtendedReplay r = data.set.ReadReplay(name);
			ReplayChunk chunk = r.GetReplay();
			if (chunk == null) {
				display.Push(new Alert("Could not read replay", null));
				return;
			}

			data.mods = r.GetMods();
			IInputOverrider input = new ReplayPlayer(chunk, r);
			display.PauseRendering();
			Nmania.Push(new ResultsScreen(data, r, input, null, display.GetAudio(), null, display.GetBg(),
					display.GetDisplayable()));
		} catch (IOException e) {
			e.printStackTrace();
			display.Push(new Alert("Could not read replay", e.toString()));
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public String GetTitle() {
		return "SELECT A REPLAY";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnEnter(IDisplay d) {
		loadingState = true;
		(new Thread(this)).start();
	}

	public void run() {
		Enumeration list = data.set.ListAllReplays().elements();
		Vector items = new Vector();
		while (list.hasMoreElements()) {
			String object = (String) list.nextElement();
			items.addElement(new ListItem(object, this));
		}
		SetItems(items);
		loadingState = false;
	}
}
