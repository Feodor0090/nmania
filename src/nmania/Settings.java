package nmania;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import symnovel.SNUtils;

public final class Settings {
	public static float bgDim = 0.75f;
	public static int[][] keyLayout = new int[10][];
	/**
	 * @deprecated
	 */
	public static int leftOffset = 30;
	/**
	 * @deprecated
	 */
	public static int columnWidth = 30;
	/**
	 * @deprecated
	 */
	public static int holdWidth = 20;
	/**
	 * @deprecated
	 */
	public static int keyboardHeight = 50;
	/**
	 * @deprecated
	 */
	public static int noteHeight = 20;
	public static int speedDiv = 3;

	public static boolean gameplaySamples = true;
	public static boolean hitSamples = true;
	
	public static boolean keepMenu = false;

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

			if (r.getNumRecords() < 1) {
				r.closeRecordStore();
				throw new RuntimeException("No saved settings");
			}
			byte[] d = r.getRecord(1);
			r.closeRecordStore();

			// parse
			JSONObject j = new JSONObject(new String(d));
			bgDim = Float.parseFloat(j.getString("bgdim"));
			speedDiv = j.getInt("speed");
			JSONArray keys = j.getJSONArray("keys");
			for (int i = 0; (i < keys.length() && i < keyLayout.length); i++) {
				if (keys.isNull(i))
					continue;
				keyLayout[i] = SNUtils.json2intArray(keys.getJSONArray(i));
			}
			gameplaySamples = j.getBoolean("samples");
			hitSamples = j.getBoolean("hitsounds");
			keepMenu = j.optBoolean("keepmenu", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
