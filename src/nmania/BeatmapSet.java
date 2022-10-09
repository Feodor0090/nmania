package nmania;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import nmania.replays.IReplayProvider;
import nmania.replays.osu.OsuReplay;
import nmania.ui.ResultsScreen;

/**
 * Model for folder with several beatmaps. All the data will be read from a
 * random one. Factory method is
 * {@link BeatmapManager#FromBMSDirectory(String)}.
 * 
 * @author Feodor0090
 *
 */
public final class BeatmapSet {
	public int id;
	public String title;
	public String artist;
	public String mapper;
	public String image;
	public String wdPath;
	public String folderName;
	public String[] files;

	public boolean hasFile(String eqName) {
		for (int i = 0; i < files.length; i++) {
			if (eqName.endsWith(files[i]))
				return true;
		}
		return false;
	}

	/**
	 * Will find the first file in BMS folder, that begins with the given
	 * prefix.<br>
	 * <br>
	 * For example, there are files "hit1.mp3", "fail-section.vaw", "hit1.wav" and
	 * "fail.wav".<br>
	 * "hit1" will find "hit1.mp3", "fail" "fail-section.vaw", "fail."
	 * "fail.wav".<br>
	 * Can be used to load hitsounds.
	 * 
	 * @param prefix Beginning of the file's name.
	 * @return Full file name, null if nothing is found.
	 */
	public String findFile(String prefix) {
		for (int i = 0; i < files.length; i++) {
			if (files[i].startsWith(prefix))
				return files[i];
		}
		return null;
	}

	public String GetFullBeatmapPath(String diffFileName) {
		return wdPath + folderName + diffFileName;
	}

	public String GetFilenameForNewReplay(OsuReplay replay, PlayerBootstrapData data) throws IOException {
		FileConnection fc = null;
		String name = wdPath + folderName + replay.playerName + " playing "
				+ data.mapFileName.substring(0, data.mapFileName.length() - 4) + " at "
				+ ResultsScreen.formatDate(replay.PlayedAt(), "-");
		int sub = 0;
		try {
			while (true) {
				fc = (FileConnection) Connector.open(addOsrNum(name, sub), Connector.READ);
				if(fc.exists()) {
					sub++;
					fc.close();
				}
				else {
					break;
				}
			}
		} finally {
			if (fc != null)
				fc.close();
		}
		return addOsrNum(name, sub);
	}

	private String addOsrNum(String name, int sub) {
		if (sub == 0)
			return name + ".osr";
		return name + " (" + sub + ").osr";
	}

	public String[] ListAllReplays() {
		Vector v = new Vector(files.length / 2, 8);
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".osr"))
				v.addElement(files[i]);
		}
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}

	/**
	 * Reads OSR.
	 * 
	 * @param name OSR file name.
	 * @return Read replay. Always not null.
	 * @throws IOException If file reading failed.
	 */
	public OsuReplay ReadReplay(String name) throws IOException {
		OsuReplay replay = new OsuReplay();
		FileConnection fc = null;
		InputStream is = null;
		try {
			fc = (FileConnection) Connector.open(wdPath + folderName + name, Connector.READ);
			is = fc.openInputStream();
			replay.read(is);
		} finally {
			if (is != null)
				is.close();
			if (fc != null)
				fc.close();
		}
		return replay;
	}
}
