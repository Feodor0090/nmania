package nmania.skin;

public abstract class Skin {

	public abstract int GetLeftOffset();
	public abstract int GetColumnWidth();
	public abstract int GetNoteHeight();
	public abstract int GetHoldWidth();
	public abstract int GetKeyboardHeight();

	/*
	 * Gets data to draw keyboard.
	 *
	 * @param columns Columns count.
	 * @return
	 * <ul>
	 * <li> Image[columns] if sprites supposed to be drawn
	 * <li> int[columns] if solid colors supposed to be drawn
	 * <li> int[columns][] if gradients supposed to be drawn, each array contains colors for whole area
	 * </ul>
	 */
	public abstract Object GetKeyboardLook(int columns);

	/*
	 * Gets data to draw notes.
	 *
	 * @param columns Columns count.
	 * @return
	 * <ul>
	 * <li> Image[columns] if sprites supposed to be drawn
	 * <li> int[columns] if solid colors supposed to be drawn
	 * <li> int[columns][] if gradients supposed to be drawn, each array contains colors for whole area
	 * </ul>
	 */
	public abstract Object GetNotesLook(int columns);
	
	public abstract void SaveData();
}
