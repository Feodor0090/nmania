package nmania.ui.ng;

import nmania.BeatmapSet;
import nmania.ModsState;
import nmania.Nmania;
import nmania.PlayerBootstrapData;
import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.replays.AutoplayRunner;

public abstract class SkinSettings extends ListScreen {

	public String GetTitle() {
		return "SKIN SETUP";
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return "PREVIEW";
	}

	public void OnOptionActivate(IDisplay d) {
		try {
			BeatmapSet testBms = new BeatmapSet("/beatmaps/", "wwwwww/", new String[] { "bm.osu", "audio.mp3" });
			testBms.Fill(testBms.ReadBeatmap("bm [skin test].osu"));
			PlayerBootstrapData pbd = new PlayerBootstrapData();
			pbd.mapFileName = "bm [skin test].osu";
			pbd.mods = new ModsState();
			pbd.recordReplay = false;
			pbd.keepBackScreen = true;
			pbd.set = testBms;
			pbd.input = new AutoplayRunner();
			PlayerLoaderScreen pls = new PlayerLoaderScreen(pbd);
			d.Push(pls);
		} catch (InvalidBeatmapTypeException e) {
			e.printStackTrace();
		}
	}

	public boolean OnExit(IDisplay d) {
		Nmania.SaveSkin();
		return false;
	}
}
