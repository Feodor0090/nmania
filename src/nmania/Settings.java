package nmania;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import symnovel.SNUtils;

public final class Settings {

	public static final String GetDefaultFolder() {
		String dir = System.getProperty("fileconn.dir.music");
		if (dir == null)
			dir = "C:/Data/Sounds/";
		if (dir.startsWith("file:///"))
			dir = dir.substring(8);
		return dir + "nmania/";
	}

	/**
	 * % of background dimming during gameplay. 0f - not touched, 1f - completely
	 * black.
	 */
	public static float bgDim = 1f;
	/**
	 * Array with keyboard layouts. Zero element is layout for 1K, the 9th is for
	 * 10K. Each layout has keys for all columns and for pause key.
	 */
	public static int[][] keyLayout = new int[10][];

	/**
	 * Divider for scrolling speed.
	 */
	public static int speedDiv = 3;

	/**
	 * Are exit/fail/pass/restart samples enabled?
	 */
	public static boolean gameplaySamples = false;
	/**
	 * Are hit samples enabled?
	 */
	public static boolean hitSamples = false;
	/**
	 * If hit samples are enabled, do we want to load them from beatmap?
	 */
	public static boolean useBmsSamples = false;
	/**
	 * If false, menu's displayables will be lost when loading player.
	 */
	public static boolean keepMenu = false;
	/**
	 * Are we drawing HUD?
	 */
	public static boolean drawHUD = true;
	/**
	 * Do we want to use single full flush? If false, three partial will be
	 * performed.
	 */
	public static boolean fullScreenFlush = false;
	/**
	 * Draw fps?
	 */
	public static boolean profiler = false;
	/**
	 * Folder from which we read all the data. Must contain trailing slash. Must not
	 * contain file:///.
	 */
	public static String workingFolder = GetDefaultFolder();
	/**
	 * Player's nickname.
	 */
	public static String name = null;
	/**
	 * Should replays be recorded by default?
	 */
	public static boolean recordReplay = false;

	public static boolean musicInMenu = false;

	public static boolean throttleGameplay = false;

	public static boolean maxPriority = true;

	public static boolean forceThreadSwitch = false;

	/**
	 * Audio/gameplay clocks offset. <br>
	 * <br>
	 * Positive - gameplay is faster than music. <br>
	 * Negative - gameplay is behind the music.
	 */
	public static int gameplayOffset = 0;
	public static boolean analyzeMaps;

	public static String GetLastDir() {
		int i = workingFolder.lastIndexOf('/', workingFolder.length() - 2);
		if (i == -1)
			return workingFolder;
		return workingFolder.substring(i, workingFolder.length());
	}

	public static final void Save() {
		if (!workingFolder.endsWith("/"))
			workingFolder = workingFolder + "/";
		try {
			JSONObject j = new JSONObject();
			j.accumulate("bgdim", String.valueOf(bgDim));
			j.accumulate("speed", new Integer(speedDiv));
			JSONArray keys = new JSONArray();
			for (int i = 0; i < keyLayout.length; i++) {
				JSONArray layout = new JSONArray();
				if (keyLayout[i] == null) {
					keys.put(JSONObject.NULL);
					continue;
				}
				for (int k = 0; k < keyLayout[i].length; k++) {
					layout.put(keyLayout[i][k]);
				}
				keys.put(layout);
			}
			j.accumulate("keys", keys);
			j.accumulate("samples", new Boolean(gameplaySamples));
			j.accumulate("hitsounds", new Boolean(hitSamples));
			j.accumulate("keepmenu", new Boolean(keepMenu));
			j.accumulate("drawcounters", new Boolean(drawHUD));
			j.accumulate("fullscreenflush", new Boolean(fullScreenFlush));
			j.accumulate("dir", workingFolder);
			j.accumulate("gameplayoffset", new Integer(gameplayOffset));
			j.accumulate("usebmssamples", new Boolean(useBmsSamples));
			j.accumulate("profiler", new Boolean(profiler));
			j.accumulate("name", name);
			j.accumulate("record", new Boolean(recordReplay));
			j.accumulate("musicinmenu", new Boolean(musicInMenu));
			j.accumulate("throttle", new Boolean(throttleGameplay));
			j.accumulate("maxpr", new Boolean(maxPriority));
			j.accumulate("threadswitch", new Boolean(forceThreadSwitch));
			j.accumulate("analyze", new Boolean(analyzeMaps));

			// writing
			byte[] d = j.toString().getBytes();
			RecordStore r = RecordStore.openRecordStore("nmania_prefs", true);

			if (r.getNumRecords() == 0) {
				r.addRecord(new byte[1], 0, 1);
			}
			r.setRecord(1, d, 0, d.length);
			r.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void Load() {
		try {
			RecordStore r = RecordStore.openRecordStore("nmania_prefs", true);
			byte[] d = null;
			if (r.getNumRecords() > 0) {
				d = r.getRecord(1);
			}
			r.closeRecordStore();

			// parse
			JSONObject j;
			if (d == null) {
				j = new JSONObject();
			} else {
				j = new JSONObject(new String(d));
			}
			bgDim = Float.parseFloat(j.optString("bgdim", "0.60")); // ?full
			speedDiv = j.optInt("speed", 3);
			JSONArray keys = j.optJSONArray("keys");
			if (keys != null) {
				for (int i = 0; (i < keys.length() && i < keyLayout.length); i++) {
					if (keys.isNull(i))
						continue;
					keyLayout[i] = SNUtils.json2intArray(keys.getJSONArray(i));
				}
			}
			gameplaySamples = j.optBoolean("samples", true); // ?full
			hitSamples = j.optBoolean("hitsounds", false); // ?full
			keepMenu = j.optBoolean("keepmenu", false); // ?full
			drawHUD = j.optBoolean("drawcounters", true);
			final String device = Nmania.GetDevice();
			fullScreenFlush = j.optBoolean("fullscreenflush",
					device.indexOf("platform=S60") != -1 && device.indexOf("platform_version=5") != -1);
			workingFolder = j.optString("dir", GetDefaultFolder());
			gameplayOffset = j.optInt("gameplayoffset", 0);
			useBmsSamples = j.optBoolean("usebmssamples", true);
			profiler = j.optBoolean("profiler", false);
			name = j.optString("name", null);
			recordReplay = j.optBoolean("record");
			musicInMenu = j.optBoolean("musicinmenu", true); // ?full
			throttleGameplay = j.optBoolean("throttle");
			maxPriority = j.optBoolean("maxpr");
			forceThreadSwitch = j.optBoolean("threadswitch");
			analyzeMaps = j.optBoolean("analyze"); // ?full
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
