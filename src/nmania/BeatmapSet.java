package nmania;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONObject;

import nmania.beatmaps.IRawBeatmap;
import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.beatmaps.RawBeatmapConverter;
import nmania.replays.IExtendedReplay;
import nmania.replays.json.NmaniaReplay;
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
	public String audio;
	public String wdPath;
	public String folderName;
	public String[] files;
	public float[][] timings;

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

	public void AddFile(String localPath) {
		String[] n = new String[files.length + 1];
		for (int i = 0; i < files.length; i++) {
			n[i] = files[i];
		}
		n[files.length] = localPath;
		files = n;
	}

	public String ToGlobalPath(String name) {
		return wdPath + folderName + name;
	}

	String lastReplayName;

	public void AddLastReplay() {
		if (lastReplayName != null)
			AddFile(lastReplayName);
		lastReplayName = null;
	}

	public String GetFilenameForNewReplay(IExtendedReplay replay, PlayerBootstrapData data) throws IOException {
		FileConnection fc = null;
		final String name = replay.GetPlayerName() + " - " + GetDifficultyNameFast(data.mapFileName) + " at "
				+ ResultsScreen.formatDate(replay.PlayedAt(), "-");
		String path = ToGlobalPath(name);
		int sub = 0;
		String ext = (replay instanceof OsuReplay) ? "osr" : "nmr";
		try {
			while (true) {
				fc = (FileConnection) Connector.open(addNum(path, sub, ext), Connector.READ);
				if (fc.exists()) {
					sub++;
					fc.close();
				} else {
					break;
				}
			}
		} finally {
			if (fc != null)
				fc.close();
		}
		lastReplayName = addNum(name, sub, ext);
		return addNum(path, sub, ext);
	}

	private String addNum(String name, int sub, String ext) {
		if (sub == 0)
			return name + "." + ext;
		return name + " (" + sub + ")." + ext;
	}

	public String[] ListAllReplays() {
		Vector v = new Vector(files.length / 2, 8);
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".osr") || files[i].endsWith(".nmr"))
				v.addElement(files[i]);
		}
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}

	public String toString() {
		return artist + " - " + title + " (" + mapper + ")";
	}

	/**
	 * Reads OSR.
	 * 
	 * @param name OSR file name.
	 * @return Read replay. Always not null.
	 * @throws IOException If file reading failed.
	 */
	public IExtendedReplay ReadReplay(String name) throws IOException {
		if (!name.endsWith(".osr")) {
			String raw = BeatmapManager.getStringFromFS(ToGlobalPath(name));
			NmaniaReplay nmr = new NmaniaReplay();
			nmr.ReadFrom(new JSONObject(raw));
			return nmr;
		}
		OsuReplay replay = new OsuReplay();
		FileConnection fc = null;
		InputStream is = null;
		try {
			fc = (FileConnection) Connector.open(ToGlobalPath(name), Connector.READ);
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

	public static String GetDifficultyNameFast(String fileName) {
		return fileName.substring(fileName.indexOf('[') + 1, fileName.lastIndexOf(']'));
	}

	/**
	 * Reads beatmap in this folder.
	 * 
	 * @param fileName Name of difficulty file.
	 * @return Raw beatmap.
	 * @throws InvalidBeatmapTypeException If could not read.
	 */
	public final IRawBeatmap ReadBeatmap(String fileName) throws InvalidBeatmapTypeException {
		String raw = BeatmapManager.getStringFromFS(ToGlobalPath(fileName));
		IRawBeatmap rb = RawBeatmapConverter.FromText(raw);
		return rb;
	}
}
