package nmania;

import javax.microedition.lcdui.Image;

public class RichSkin {
	public RichSkin(String folder) {

	}

	public Image key1;
	public Image key2;
	public Image key3;
	public Image note1;
	public Image note2;
	public Image note3;
	public Image shadow1;
	public Image shadow2;
	public Image shadow3;
	public final Image[] digits = new Image[12]; // 0123456789,%
	public final Image[] judgments = new Image[6];

	public final void Check() throws IllegalStateException {

	}

	public int GetColumnWidth() {
		return key1.getWidth();
	}

	public int GetNoteHeight() {
		return note1.getHeight();
	}

	public int GetKeyboardHeight() {
		return key1.getHeight();
	}

	public int GetCounterHeight() {
		return digits[0].getHeight();
	}

	public int GetScoreWidth() {
		int max = 0;
		for (int i = 0; i < 10; i++) {
			if (digits[i].getWidth() > max) {
				max = digits[i].getWidth();
			}
		}
		return max * 9;
	}

	public int GetAccWidth() {
		int max = 0;
		for (int i = 0; i < 10; i++) {
			if (digits[i].getWidth() > max) {
				max = digits[i].getWidth();
			}
		}
		return (max * 5) + digits[10].getWidth() + digits[11].getWidth();
	}

	/**
	 * Returns a collection of sprites to be drawn.<br>
	 * <br>
	 * Format:<br>
	 * <ul>
	 * <li>6 sprites for judgements from miss to perfect.
	 * <li>12 sprites for counters drawing (0123456789,%)
	 * <li>[columns] sprites for keys
	 * <li>[columns] sprites for notes
	 * <li>[columns] sprites for shadows
	 * </ul>
	 * 
	 * @param columns
	 * @return
	 */
	public Image[] toPlayerCache(int columns) {
		Image[] a = new Image[18 + columns * 3];
		System.arraycopy(judgments, 0, a, 0, 6);
		System.arraycopy(digits, 0, a, 6, 12);
		System.arraycopy(composeSpritesFor(key1, key2, key3, columns), 0, a, 18, columns);
		System.arraycopy(composeSpritesFor(note1, note2, note3, columns), 0, a, 18 + columns, columns);
		System.arraycopy(composeSpritesFor(shadow1, shadow2, shadow3, columns), 0, a, 18 + columns + columns, columns);
		return a;
	}

	private static final Image[] composeSpritesFor(Image i1, Image i2, Image i3, final int cols) {
		// odd odd nonodd nonodd sp sp
		if (cols == 1) {
			return new Image[] { i1 };
		}
		Image[] c = new Image[cols];
		for (int i = 0; i < cols / 2; i++) {
			final Image it = i % 2 == 0 ? i1 : i2;
			c[i] = it;
			c[(cols - i - 1)] = it;
		}
		if (cols % 2 == 1) {
			c[(cols / 2)] = i3;
		}
		return c;
	}
}
