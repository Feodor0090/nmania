package nmania.ui.ng;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import nmania.IInputOverrider;
import nmania.Nmania;
import nmania.PlayerBootstrapData;
import nmania.replays.ReplayChunk;
import nmania.replays.ReplayPlayer;
import nmania.replays.osu.OsuReplay;
import nmania.ui.ResultsScreen;

public class ReplaySelect extends ListScreen implements IListSelectHandler, Runnable {

	private PlayerBootstrapData data;

	public ReplaySelect(PlayerBootstrapData data) {
		this.data = data;
	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		String name = item.text;
		try {
			OsuReplay r = data.set.ReadReplay(name);
			ReplayChunk chunk = r.GetReplay();
			if (chunk == null) {
				display.PauseRendering();
				Nmania.Push(new Alert("nmania", "Could not read replay.", null, AlertType.ERROR));
				return;
			}

			data.mods = r.GetMods();
			IInputOverrider input = new ReplayPlayer(chunk, r);
			display.PauseRendering();
			Nmania.Push(new ResultsScreen(data, r, input, null, null, null, null, display.GetDisplayable()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

	public String GetTitle() {
		return "SELECT A REPLAY";
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
		loadingState = true;
		(new Thread(this)).start();
	}

	public void run() {
		String[] list = data.set.ListAllReplays();
		ListItem[] items = new ListItem[list.length];
		for (int i = 0; i < list.length; i++) {
			items[i] = new ListItem(list[i], this);
		}
		SetItems(items);
		loadingState = false;
	}
}
