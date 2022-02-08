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
				return;
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
			verticalGradientOnNotes = j.getBoolean("verticalgradients");
			holdsHaveOwnColors = j.getBoolean("holdsdiffcolors");

			noteColors = SNUtils.json2intArray(j.getJSONArray("notecolors"));
			holdColors = SNUtils.json2intArray(j.getJSONArray("holdcolors"));
			keyColors = SNUtils.json2intArray(j.getJSONArray("keycolors"));
			holdKeyColors = SNUtils.json2intArray(j.getJSONArray("holdkeycolors"));
		} catch (Exception e) {
		}
	}

	public boolean rich = false;
	public int keyboardHeight = 50;
	public int leftOffset = 30;
	public int columnWidth = 30;
	public int holdWidth = 20;
	public int noteHeight = 20;
	public int[] noteColors = new int[] { SNUtils.toARGB("0xF00"), SNUtils.toARGB("0x200"), SNUtils.toARGB("0x0F0"),
			SNUtils.toARGB("0x020"), SNUtils.toARGB("0xFF0"), SNUtils.toARGB("0x022") };
	public int[] holdColors = new int[] { SNUtils.toARGB("0xF00"), SNUtils.toARGB("0x700"), SNUtils.toARGB("0x0F0"),
			SNUtils.toARGB("0x070"), SNUtils.toARGB("0xFF0"), SNUtils.toARGB("0x770") };
	public int[] keyColors = new int[] { SNUtils.toARGB("0x777"), SNUtils.toARGB("0x69D"), SNUtils.toARGB("0x777"),
			SNUtils.toARGB("0x69D"), SNUtils.toARGB("0x777"), SNUtils.toARGB("0x69D") };
	public int[] holdKeyColors = new int[] { SNUtils.toARGB("0x0FF"), SNUtils.toARGB("0x69D"), SNUtils.toARGB("0x0FF"),
			SNUtils.toARGB("0x69D"), SNUtils.toARGB("0x0FF"), SNUtils.toARGB("0x69D") };
	public boolean verticalGradientOnNotes = true;
	public boolean holdsHaveOwnColors = true;

	public RichSkin richSkin = null;

	public int GetColumnWidth() {
		return columnWidth;
	}

	public int GetNoteHeight() {
		return noteHeight;
	}

	public int GetKeyboardHeight() {
		return keyboardHeight;
	}

	public int[] GetNoteColors(int columns) {
		return composeColorsFor(noteColors, columns);
	}

	public int[] GetHoldColors(int columns) {
		return composeColorsFor(holdColors, columns);
	}

	public int[] GetKeyColors(int columns) {
		return composeColorsFor(keyColors, columns);
	}

	public int[] GetHoldKeyColors(int columns) {
		return composeColorsFor(holdKeyColors, columns);
	}

	private static final int[] composeColorsFor(final int[] s, final int cols) {
		int[] c = new int[cols * 2];
		// odd odd nonodd nonodd sp sp
		if (cols == 1) {
			return new int[] { s[4], s[5] };
		}
		for (int i = 0; i < cols / 2; i++) {
			final int c1 = i % 2 == 0 ? s[0] : s[2];
			final int c2 = i % 2 == 0 ? s[1] : s[3];
			c[i * 2] = c1;
			c[i * 2 + 1] = c2;
			c[(cols - i - 1) * 2] = c1;
			c[(cols - i - 1) * 2 + 1] = c2;
		}
		if (cols % 2 == 1) {
			c[(cols / 2) * 2] = s[4];
			c[(cols / 2) * 2 + 1] = s[5];
		}
		return c;
	}

	public void LoadRich(boolean force) throws IllegalStateException {
		rich = true;
		if (force || richSkin == null) {
			richSkin = new RichSkin(Settings.workingFolder + "_skin/");
			try {
				richSkin.Check();
			} catch (IllegalStateException e) {
				richSkin = null;
				throw e;
			}
		}
	}

	public void DisableRich() {
		rich = false;
		richSkin = null;
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
			j.accumulate("verticalgradients", new Boolean(verticalGradientOnNotes));
			j.accumulate("holdsdiffcolors", new Boolean(holdsHaveOwnColors));

			{
				Vector v = new Vector();
				for (int i = 0; i < noteColors.length; i++) {
					v.addElement(new Integer(noteColors[i]));
				}
				j.accumulate("notecolors", new JSONArray(v));
			}
			{
				Vector v = new Vector();
				for (int i = 0; i < holdColors.length; i++) {
					v.addElement(new Integer(holdColors[i]));
				}
				j.accumulate("holdcolors", new JSONArray(v));
			}
			{
				Vector v = new Vector();
				for (int i = 0; i < keyColors.length; i++) {
					v.addElement(new Integer(keyColors[i]));
				}
				j.accumulate("keycolors", new JSONArray(v));
			}
			{
				Vector v = new Vector();
				for (int i = 0; i < holdKeyColors.length; i++) {
					v.addElement(new Integer(holdKeyColors[i]));
				}
				j.accumulate("holdkeycolors", new JSONArray(v));
			}

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
