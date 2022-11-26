package nmania.skin;

public class VectorSkin extends Skin {

	public static final String RMS = "nmania_vector_skin";
	public final int[] leftOffset = new int[1];
	public final int[] columnWidth = new int[1];
	public final int[] noteHeight = new int[1];
	public final int[] holdWidth = new int[1];
	public final int[] keyboardHeight = new int[1];


	public int GetLeftOffset() {
		return leftOffset[0];
	}

	public int GetColumnWidth() {
		return columnWidth[0];
	}

	public int GetNoteHeight() {
		return noteHeight[0];
	}

	public int GetHoldWidth() {
		return holdWidth[0];
	}

	public int GetKeyboardHeight() {
		return keyboardHeight[0];
	}

	public Object GetKeyboardLook(int columns) {
		return new int[columns];
	}

	public Object GetNotesLook(int columns) {
		return new int[columns];
	}

	public void SaveData() { }
}
