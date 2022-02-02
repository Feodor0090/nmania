package nmania;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONObject;

import symnovel.SNUtils;

public class Skin {

	public Skin() {
		try {
			RecordStore r = RecordStore.openRecordStore("nmania_skin", true);

			if (r.getNumRecords() < 1) {
				r.closeRecordStore();
				throw new RuntimeException("No saved settings");
			}
			byte[] d = r.getRecord(1);
			r.closeRecordStore();

			// parse
			JSONObject j = new JSONObject(new String(d));

			rich = j.getBoolean("rich");
			keyboardHeight = j.getInt("keyboardheight");
			leftOffset = j.getInt("leftoffset");
			columnWidth = j.getInt("columnwidth");
			holdWidth = j.getInt("holdwidth");
			noteHeight = j.getInt("noteheight");

			noteColors = SNUtils.json2intArray(j.getJSONArray("notecolors"));
			holdColors = SNUtils.json2intArray(j.getJSONArray("holdcolors"));
			keyColors = SNUtils.json2intArray(j.getJSONArray("keycolors"));
			holdKeyColors = SNUtils.json2intArray(j.getJSONArray("holdkeycolors"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean rich = false;
	public int keyboardHeight = 50;
	public int leftOffset = 30;
	public int columnWidth = 30;
	public int holdWidth = 20;
	public int noteHeight = 20;
	public int[] noteColors;
	public int[] holdColors;
	public int[] keyColors;
	public int[] holdKeyColors;

	public int GetColumnWidth() {
		return columnWidth;
	}

	public int GetNoteHeight() {
		return noteHeight;
	}

	public int GetKeyboardHeight() {
		return keyboardHeight;
	}

	public void Load() {

	}

	public void Save() {

	}
}
