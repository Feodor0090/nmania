package nmania;

import java.util.Vector;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
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
		try {
			JSONObject j = new JSONObject();

			j.accumulate("rich", new Boolean(rich));
			j.accumulate("keyboardheight", new Integer(keyboardHeight));
			j.accumulate("leftoffset", new Integer(leftOffset));
			j.accumulate("columnwidth", new Integer(columnWidth));
			j.accumulate("holdwidth", new Integer(holdWidth));
			j.accumulate("noteheight", new Integer(noteHeight));

			{Vector v = new Vector();for(int i=0;i<noteColors.length;i++) {v.addElement(new Integer(noteColors[i]));}j.accumulate("notecolors", new JSONArray(v));}
			{Vector v = new Vector();for(int i=0;i<holdColors.length;i++) {v.addElement(new Integer(holdColors[i]));}j.accumulate("holdcolors", new JSONArray(v));}
			{Vector v = new Vector();for(int i=0;i<keyColors.length;i++) {v.addElement(new Integer(keyColors[i]));}j.accumulate("keycolors", new JSONArray(v));}
			{Vector v = new Vector();for(int i=0;i<holdKeyColors.length;i++) {v.addElement(new Integer(holdKeyColors[i]));}j.accumulate("holdkeycolors", new JSONArray(v));}
			
			// writing
			byte[] d = j.toString().getBytes();
			RecordStore r = RecordStore.openRecordStore("nmania_skin", true);

			if (r.getNumRecords() == 0) {
				r.addRecord(new byte[1], 0, 1);
			}
			r.setRecord(1, d, 0, d.length);
			r.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
