package nmania.skin;

import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import nmania.BeatmapManager;
import nmania.Settings;
import symnovel.SNUtils;

public class RasterSkin extends Skin {

	public RasterSkin() {
	}

	public int leftOffset;
	public int holdWidth;
	public int[] background = new int[3];
	public Image[] keyboard;
	public Image[] keyboardHold;
	public Image[] notes;
	public Image[] holds;
	public int[] holdBodies = new int[3];
	public Image[] hud;

	public int GetLeftOffset() {
		return leftOffset;
	}

	public int GetColumnWidth() {
		if (VerifyWidth() == null)
			return notes[0].getWidth();
		return 30;
	}

	public int GetNoteHeight() {
		if (VerifyNotes() == null)
			return notes[0].getHeight();
		return 20;
	}

	public int GetHoldWidth() {
		return Math.max(GetLeftOffset(), holdWidth);
	}

	public int GetKeyboardHeight() {
		if (VerifyKb() == null) {
			return keyboard[0].getHeight();
		}
		return 50;
	}

	public Object GetKeyboardLook(int columns) {
		if (VerifyKb() == null) {
			return Pack(keyboard, columns);
		}
		return Pack(new int[6], columns, 50);
	}

	public Object GetHoldKeyboardLook(int columns) {
		if (VerifyKb() == null) {
			return Pack(keyboard, columns);
		}
		return Pack(new int[] { -1, -1, -1, -1, -1, -1 }, columns, 50);
	}

	public Object GetNotesLook(int columns) {
		if (VerifyNotes() == null) {
			return Pack(notes, columns);
		}
		return Pack(new int[] { -1, -1, -1, -1, -1, -1 }, columns, 20);
	}

	public Object GetHoldHeadsLook(int columns) {
		if (VerifyNotes() == null) {
			return Pack(holds, columns);
		}
		return Pack(new int[] { -1, -1, -1, -1, -1, -1 }, columns, 20);
	}

	public int[] GetHoldBodiesLook(int columns) {
		return Pack(holdBodies, columns);
	}

	public Object GetNumericHUD() {
		if (VerifyHud() == null)
			return hud;
		return new Integer(-1);
	}

	public Image[] GetJudgments() {
		return null;
	}

	public int[] GetColumnsBackground(int columns) {
		return Pack(background, columns);
	}

	public Skin Read(JSONObject j) {
		final String base = "file:///" + Settings.workingFolder + "_skin/";
		final String png = ".png";
		keyboard = new Image[3];
		keyboardHold = new Image[3];
		notes = new Image[3];
		holds = new Image[3];
		hud = new Image[12];
		// files
		try {
			for (int i = 0; i < 3; i++) {
				int i1 = i + 1;
				keyboard[i] = BeatmapManager.getImgFromFS(base + "kb" + i1 + png);
				keyboardHold[i] = BeatmapManager.getImgFromFS(base + "kbh" + i1 + png);
				notes[i] = BeatmapManager.getImgFromFS(base + "note" + i1 + png);
				holds[i] = BeatmapManager.getImgFromFS(base + "hold" + i1 + png);
			}
			for (int i = 0; i < 10; i++) {
				hud[i] = BeatmapManager.getImgFromFS(base + "hud" + i + png);
			}
			hud[10] = BeatmapManager.getImgFromFS(base + "hud," + png);
			hud[10] = BeatmapManager.getImgFromFS(base + "hud%" + png);
		} catch (Exception e) {
		}
		if (j == null) {
			leftOffset = 30;
			holdWidth = 20;
			background = new int[] { 0x002200, 0x220000, 0x000022 };
			holdBodies = new int[] { 0x007700, 0x770000, 0x000077 };
			return this;
		}
		// json
		leftOffset = j.optInt("left_offset", 30);
		holdWidth = j.optInt("hold_width", 20);

		background = SNUtils.json2intArray(j.optJSONArray("background"));
		if (background == null || background.length < 3)
			background = new int[] { 0x002200, 0x220000, 0x000022 };

		holdBodies = SNUtils.json2intArray(j.optJSONArray("hold_bodies"));
		if (holdBodies == null || holdBodies.length < 3)
			holdBodies = new int[] { 0x007700, 0x770000, 0x000077 };

		return this;
	}

	public JSONObject Write() {
		JSONObject j = new JSONObject();

		j.put("left_offset", leftOffset);
		j.put("hold_width", holdWidth);
		j.put("background", ToVector(background));
		j.put("hold_bodies", ToVector(holdBodies));

		return j;
	}

	public String RMSName() {
		return "nmania_skin_raster";
	}

	public String VerifyKb() {
		for (int i = 0; i < 3; i++) {
			if (keyboard[i] == null)
				return "File \"kb" + (i + 1) + ".png\" is missing";
			if (keyboardHold[i] == null)
				return "File \"kbh" + (i + 1) + ".png\" is missing";
		}
		int w = keyboard[0].getWidth();
		int h = keyboard[0].getHeight();

		for (int i = 1; i < 3; i++) {
			if (keyboard[i].getWidth() != w || keyboard[i].getHeight() != h)
				return "All keyboard sprites must have identical width and height";
		}
		for (int i = 0; i < 3; i++) {
			if (keyboardHold[i].getWidth() != w || keyboardHold[i].getHeight() != h)
				return "All keyboard (hold) sprites must have identical width and height and have the same size as regular keyboard sprites.";
		}

		return null;
	}

	public String VerifyNotes() {
		for (int i = 0; i < 3; i++) {
			if (notes[i] == null)
				return "File \"note" + (i + 1) + ".png\" is missing";
			if (holds[i] == null)
				return "File \"hold" + (i + 1) + ".png\" is missing";
		}
		int w = notes[0].getWidth();
		int h = notes[0].getHeight();

		for (int i = 1; i < 3; i++) {
			if (notes[i].getWidth() != w || notes[i].getHeight() != h)
				return "All note sprites must have identical width and height";
		}
		for (int i = 0; i < 3; i++) {
			if (holds[i].getWidth() != w || holds[i].getHeight() != h)
				return "All hold sprites must have identical width and height and have the same size as regular note sprites.";
		}

		return null;
	}

	public String VerifyWidth() {
		String kb = VerifyKb();
		if (kb != null)
			return kb;
		String nt = VerifyNotes();
		if (nt != null)
			return nt;
		if (notes[0].getWidth() != keyboard[0].getWidth())
			return "Note sprites and keyboard sprites must have identical width";

		return null;
	}

	public String VerifyHud() {
		for (int i = 0; i < 10; i++) {
			if (hud[i] == null)
				return "File \"hud" + i + ".png\" is missing";
		}
		if (hud[10] == null)
			return "File \"hud,.png\" is missing";
		if (hud[11] == null)
			return "File \"hud%.png\" is missing";
		int h = hud[0].getHeight();
		for (int i = 1; i < 12; i++) {
			if (hud[i].getHeight() != h)
				return "HUD sprites must have identical height";
		}

		return null;
	}

}
