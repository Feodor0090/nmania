package nmania.skin;

import javax.microedition.lcdui.Image;

public abstract class Skin {

	public abstract int GetLeftOffset();

	public abstract int GetColumnWidth();

	public abstract int GetNoteHeight();

	public abstract int GetHoldWidth();

	public abstract int GetKeyboardHeight();

	/**
	 * Gets data to draw keyboard.
	 *
	 * @param columns Columns count.
	 *
	 * @return
	 *         <ul>
	 *         <li>Image[columns] if sprites supposed to be drawn
	 *         <li>int[columns][1 or GetKeyboardHeight()] if color supposed to be
	 *         drawn, each array contains colors for whole area if gradient must be
	 *         drawn, or 1 element if solid color.
	 *         </ul>
	 * 
	 */
	public abstract Object GetKeyboardLook(int columns);

	/**
	 * Gets data to draw pressed keyboard.
	 *
	 * @param columns Columns count.
	 *
	 * @return
	 *         <ul>
	 *         <li>Image[columns] if sprites supposed to be drawn
	 *         <li>int[columns][1 or GetKeyboardHeight()] if color supposed to be
	 *         drawn, each array contains colors for whole area if gradient must be
	 *         drawn, or 1 element if solid color.
	 *         </ul>
	 */
	public abstract Object GetHoldKeyboardLook(int columns);

	/**
	 * Gets data to draw notes.
	 *
	 * @param columns Columns count.
	 *
	 * @return
	 *         <ul>
	 *         <li>Image[columns] if sprites supposed to be drawn</li>
	 *         <li>int[columns][1 or GetKeyboardHeight()] if color supposed to be
	 *         drawn, each array contains colors for whole area if gradient must be
	 *         drawn, or 1 element if solid color.</li>
	 *         </ul>
	 */
	public abstract Object GetNotesLook(int columns);

	/**
	 * Gets data to draw notes with non-zero duration.
	 *
	 * @param columns Columns count.
	 *
	 * @return
	 *         <ul>
	 *         <li>Image[columns] if sprites supposed to be drawn</li>
	 *         <li>int[columns][1 or GetKeyboardHeight()] if color supposed to be
	 *         drawn, each array contains colors for whole area if gradient must be
	 *         drawn, or 1 element if solid color.</li>
	 *         </ul>
	 */
	public abstract Object GetHoldHeadsLook(int columns);

	/**
	 * Gets data to draw holds.
	 *
	 * @param columns Columns count.
	 *
	 * @return Array with colors for each column.
	 */
	public abstract int[] GetHoldBodiesLook(int columns);

	/**
	 * Gets data to draw numeric HUD.
	 * 
	 * @return
	 *         <ul>
	 *         <li>Integer if regular font must be used. Specifies color.</li>
	 *         <li>Image[12] - sprites for 0123456789,%</li>
	 *         </ul>
	 */
	public abstract Object GetNumericHUD();

	/**
	 * Gets images to draw judgments.
	 * 
	 * @return Image[6] for miss, meh, ok, good, great, perfect; or null if font
	 *         must be used.
	 */
	public abstract Image[] GetJudgments();

	/**
	 * Gets data to fill columns.
	 *
	 * @param columns Columns count.
	 *
	 * @return Array with colors for each column.
	 */
	public abstract int[] GetColumnsBackground(int columns);
}
