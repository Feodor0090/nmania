package nmania;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import symnovel.SNUtils;

public final class Settings {

	public static final String defaultWF = "file:///C:/Data/Sounds/nmania/";

	public static float bgDim = 0.75f;
	public static int[][] keyLayout = new int[10][];

	public static int speedDiv = 3;

	public static boolean gameplaySamples = true;
	public static boolean hitSamples = false;
	public static boolean useBmsSamples = true;
	public static boolean keepMenu = true;
	public static boolean drawCounters = true;
	public static boolean fullScreenFlush = false;
	public static boolean profiler = false;
	public static String workingFolder = defaultWF;

	/**
	 * Audio/gameplay clocks offset. <br>
	 * <br>
	 * Positive - gameplay is faster than music. <br>
	 * Negative - gameplay is behind the music.
	 */
	public static int gameplayOffset = 0;

	public static final void Save() {
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
			j.accumulate("drawcounters", new Boolean(drawCounters));
			j.accumulate("fullscreenflush", new Boolean(fullScreenFlush));
			j.accumulate("dir", workingFolder);
			j.accumulate("gameplayoffset", new Integer(gameplayOffset));
			j.accumulate("usebmssamples", new Boolean(useBmsSamples));
			j.accumulate("profiler", new Boolean(profiler));

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
			bgDim = Float.parseFloat(j.optString("bgdim", "0.90"));
			speedDiv = j.optInt("speed", 3);
			JSONArray keys = j.optJSONArray("keys");
			if (keys != null) {
				for (int i = 0; (i < keys.length() && i < keyLayout.length); i++) {
					if (keys.isNull(i))
						continue;
					keyLayout[i] = SNUtils.json2intArray(keys.getJSONArray(i));
				}
			}
			gameplaySamples = j.optBoolean("samples", true);
			hitSamples = j.optBoolean("hitsounds", false);
			keepMenu = j.optBoolean("keepmenu", false);
			drawCounters = j.optBoolean("drawcounters", true);
			final String device = Nmania.GetDevice();
			fullScreenFlush = j.optBoolean("fullscreenflush",
					device.indexOf("platform=S60") != -1 && device.indexOf("platform_version=5") != -1);
			workingFolder = j.optString("dir", defaultWF);
			gameplayOffset = j.optInt("gameplayoffset", 0);
			useBmsSamples = j.optBoolean("usebmssamples", true);
			profiler = j.optBoolean("profiler", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
