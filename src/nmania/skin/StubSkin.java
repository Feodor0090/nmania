package nmania.skin;

import javax.microedition.lcdui.Image;

public class StubSkin extends Skin {

	public int GetLeftOffset() {
		return 20;
	}

	public int GetColumnWidth() {
		return 40;
	}

	public int GetNoteHeight() {
		return 20;
	}

	public int GetHoldWidth() {
		return 30;
	}

	public int GetKeyboardHeight() {
		return 50;
	}

	public Object GetKeyboardLook(int columns) {
		int[][] a = new int[columns][];
		for (int i = 0; i < columns; i++) {
			a[i] = new int[] { 0x00ff00 };
		}
		return a;
	}

	public Object GetHoldKeyboardLook(int columns) {
		int[][] a = new int[columns][];
		for (int i = 0; i < columns; i++) {
			a[i] = new int[] { 0x007700 };
		}
		return a;
	}

	public Object GetNotesLook(int columns) {
		int[][] a = new int[columns][];
		for (int i = 0; i < columns; i++) {
			a[i] = new int[] { 0xff0000 };
		}
		return a;
	}

	public Object GetHoldHeadsLook(int columns) {
		int[][] a = new int[columns][];
		for (int i = 0; i < columns; i++) {
			a[i] = new int[] { 0x0000ff };
		}
		return a;
	}

	public int[] GetHoldBodiesLook(int columns) {
		int[] a = new int[columns];
		for (int i = 0; i < columns; i++) {
			a[i] = 0x00ffff;
		}
		return a;
	}

	public Object GetNumericHUD() {
		return new Integer(0x00ff00);
	}

	public Image[] GetJudgments() {
		return null;
	}

	public int[] GetColumnsBackground(int columns) {
		return new int[columns];
	}

}
