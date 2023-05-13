package nmania.skin;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import tube42.lib.imagelib.ColorUtils;

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

	public abstract Skin Read(JSONObject j);

	public abstract JSONObject Write();

	public abstract String RMSName();

	protected final static int[] Interpolate(int c1, int c2, int l) {
		if (c1 == c2)
			return new int[] { c1 };

		int[] a = new int[l];
		int lm1 = l - 1;
		for (int i = 0; i < l; i++) {
			a[i] = ColorUtils.blend(c2, c1, (i * 255) / lm1);
		}
		return a;
	}

	protected final static int[][] Pack(int[] odd, int[] nonOdd, int[] center, int count) {
		int[][] a = new int[count][];
		if (count == 1) {
			a[0] = center;
		} else if (count == 2) {
			a[0] = odd;
			a[1] = nonOdd;
		} else if (count == 3) {
			a[0] = odd;
			a[1] = center;
			a[2] = nonOdd;
		} else {
			if (count % 2 == 1) {
				// center note
				a[count / 2] = center;
			}
			for (int i = 0; i < count / 2; i++) {
				int[] t = i % 2 == 0 ? odd : nonOdd;
				a[i] = t;
				a[count - i - 1] = t;
			}
		}
		return a;
	}

	protected final static int[][] Pack(int[] pallete6, int columns, int size) {
		int[] odd = Interpolate(pallete6[0], pallete6[1], size);
		int[] nonOdd = Interpolate(pallete6[2], pallete6[3], size);
		int[] center = Interpolate(pallete6[4], pallete6[5], size);
		return Pack(odd, nonOdd, center, columns);
	}

	protected final static int[] Pack(int[] pallete3, int count) {
		int[] a = new int[count];
		if (count == 1) {
			a[0] = pallete3[2];
		} else if (count == 2) {
			a[0] = pallete3[0];
			a[1] = pallete3[1];
		} else if (count == 3) {
			a[0] = pallete3[0];
			a[1] = pallete3[2];
			a[2] = pallete3[1];
		} else {
			if (count % 2 == 1) {
				// center note
				a[count / 2] = pallete3[2];
			}
			for (int i = 0; i < count / 2; i++) {
				int t = pallete3[i % 2];
				a[i] = t;
				a[count - i - 1] = t;
			}
		}
		return a;
	}

	protected final static Image[] Pack(Image[] pallete3, int count) {
		Image[] a = new Image[count];
		if (count == 1) {
			a[0] = pallete3[2];
		} else if (count == 2) {
			a[0] = pallete3[0];
			a[1] = pallete3[1];
		} else if (count == 3) {
			a[0] = pallete3[0];
			a[1] = pallete3[2];
			a[2] = pallete3[1];
		} else {
			if (count % 2 == 1) {
				// center note
				a[count / 2] = pallete3[2];
			}
			for (int i = 0; i < count / 2; i++) {
				Image t = pallete3[i % 2];
				a[i] = t;
				a[count - i - 1] = t;
			}
		}
		return a;
	}

	public static Vector ToVector(int[] arr) {
		Vector v = new Vector(arr.length);
		for (int i = 0; i < arr.length; i++) {
			v.addElement(new Integer(arr[i]));
		}
		return v;
	}
}
