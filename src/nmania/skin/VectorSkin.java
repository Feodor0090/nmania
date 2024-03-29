package nmania.skin;

import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import nmania.ui.ng.NmaniaDisplay;
import symnovel.SNUtils;

public class VectorSkin extends Skin {

	public VectorSkin() {
	}

	public int leftOffset = 30;
	public int columnWidth = 30;
	public int noteHeight = 20;
	public int holdWidth = 20;
	public int keyboardHeight = 50;

	public int GetLeftOffset() {
		return leftOffset;
	}

	public int GetColumnWidth() {
		return columnWidth;
	}

	public int GetNoteHeight() {
		return noteHeight;
	}

	public int GetHoldWidth() {
		return holdWidth;
	}

	public int GetKeyboardHeight() {
		return keyboardHeight;
	}

	public int[] background = new int[3];
	public int[] keyboard = new int[6];
	public int[] keyboardHold = new int[6];
	public int[] notes = new int[6];
	public int[] holds = new int[6];
	public int[] holdBodies = new int[3];
	public int hudColor;
	public int bordersColor;

	public Object GetKeyboardLook(int columns) {
		return Pack(keyboard, columns, GetKeyboardHeight());
	}

	public Object GetHoldKeyboardLook(int columns) {
		return Pack(keyboardHold, columns, GetKeyboardHeight());
	}

	public Object GetNotesLook(int columns) {
		return Pack(notes, columns, GetNoteHeight());
	}

	public Object GetHoldHeadsLook(int columns) {
		return Pack(holds, columns, GetNoteHeight());
	}

	public int[] GetHoldBodiesLook(int columns) {
		return Pack(holdBodies, columns);
	}

	public Object GetNumericHUD() {
		return new Integer(hudColor);
	}

	public Image[] GetJudgments() {
		return null;
	}

	public int[] GetColumnsBackground(int columns) {
		return Pack(background, columns);
	}

	public int GetBordersColor() {
		return bordersColor;
	}

	public Skin Read(JSONObject j) {
		if (j == null)
			j = new JSONObject();
		// numbers
		leftOffset = j.optInt("left_offset", 30);
		columnWidth = j.optInt("column_width", 30);
		noteHeight = j.optInt("note_height", 20);
		holdWidth = j.optInt("hold_width", 20);
		keyboardHeight = j.optInt("keyboard_height", keyboardHeight);
		hudColor = j.optInt("hud_color", -1);
		bordersColor = j.optInt("borders_color", -1);

		// palletes
		background = SNUtils.json2intArray(j.optJSONArray("background"));
		if (background == null || background.length < 3)
			background = new int[] { 0x002200, 0x220000, 0x000022 };

		keyboard = SNUtils.json2intArray(j.optJSONArray("keyboard"));
		if (keyboard == null || keyboard.length < 6)
			keyboard = new int[] { NmaniaDisplay.NMANIA_COLOR, NmaniaDisplay.DARKER_COLOR, NmaniaDisplay.NMANIA_COLOR,
					NmaniaDisplay.DARKER_COLOR, NmaniaDisplay.NMANIA_COLOR, NmaniaDisplay.DARKER_COLOR };

		keyboardHold = SNUtils.json2intArray(j.optJSONArray("keyboard_hold"));
		if (keyboardHold == null || keyboardHold.length < 6)
			keyboardHold = new int[] { 0x00ff00, NmaniaDisplay.DARKER_COLOR, 0xff0000, NmaniaDisplay.DARKER_COLOR,
					0x0000ff, NmaniaDisplay.DARKER_COLOR };

		notes = SNUtils.json2intArray(j.optJSONArray("notes"));
		if (notes == null || notes.length < 6)
			notes = new int[] { 0x002200, 0x00ff00, 0x220000, 0xff0000, 0x000022, 0x0000ff };

		holds = SNUtils.json2intArray(j.optJSONArray("holds"));
		if (holds == null || holds.length < 6)
			holds = new int[] { 0x007700, 0x00ff00, 0x770000, 0xff0000, 0x000077, 0x0000ff };

		holdBodies = SNUtils.json2intArray(j.optJSONArray("hold_bodies"));
		if (holdBodies == null || holdBodies.length < 3)
			holdBodies = new int[] { 0x007700, 0x770000, 0x000077 };

		return this;
	}

	public JSONObject Write() {
		JSONObject j = new JSONObject();

		// numbers
		j.put("left_offset", leftOffset);
		j.put("column_width", columnWidth);
		j.put("note_height", noteHeight);
		j.put("hold_width", holdWidth);
		j.put("keyboard_height", keyboardHeight);
		j.put("hud_color", hudColor);
		j.put("borders_color", bordersColor);

		// palletes
		j.put("background", ToVector(background));
		j.put("keyboard", ToVector(keyboard));
		j.put("keyboard_hold", ToVector(keyboardHold));
		j.put("notes", ToVector(notes));
		j.put("holds", ToVector(holds));
		j.put("hold_bodies", ToVector(holdBodies));

		return j;
	}

	public String RMSName() {
		return "nmania_skin_vector";
	}

}
