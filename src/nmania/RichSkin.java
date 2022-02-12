package nmania;

import javax.microedition.lcdui.Image;

public class RichSkin {
	public RichSkin(String folder) {
		key1 = BeatmapManager.getImgFromFS(folder + "key1.png");
		key2 = BeatmapManager.getImgFromFS(folder + "key2.png");
		key3 = BeatmapManager.getImgFromFS(folder + "key3.png");

		hkey1 = BeatmapManager.getImgFromFS(folder + "hkey1.png");
		hkey2 = BeatmapManager.getImgFromFS(folder + "hkey2.png");
		hkey3 = BeatmapManager.getImgFromFS(folder + "hkey3.png");

		note1 = BeatmapManager.getImgFromFS(folder + "note1.png");
		note2 = BeatmapManager.getImgFromFS(folder + "note2.png");
		note3 = BeatmapManager.getImgFromFS(folder + "note3.png");

		for (int i = 0; i < 6; i++) {
			judgments[i] = BeatmapManager.getImgFromFS(folder + "judgment" + i + ".png");
		}
		for (int i = 0; i < 12; i++) {
			digits[i] = BeatmapManager.getImgFromFS(folder + "digit" + i + ".png");
		}
	}

	public Image key1;
	public Image key2;
	public Image key3;
	public Image note1;
	public Image note2;
	public Image note3;
	public Image hkey1;
	public Image hkey2;
	public Image hkey3;
	public final Image[] digits = new Image[12]; // 0123456789,%
	public final Image[] judgments = new Image[6];

	public final void Check() throws IllegalStateException {
		int i1 = 0;
		try {
			for (i1 = 1; i1 < digits.length; i1++) {
				if (digits[i1].getHeight() != digits[i1 - 1].getHeight())
					throw new IllegalStateException("Heights of digits are not equal to each other.");
			}
		} catch (NullPointerException e) {
			throw new IllegalStateException(
					"Digits were not correctly loaded. Check " + (i1 - 1) + "-" + i1 + " digit sprites.");
		}
		try {
			for (int i = 1; i < judgments.length; i++) {
				judgments[i].getHeight();
			}
		} catch (NullPointerException e) {
			throw new IllegalStateException("Judgments were not correctly loaded.");
		}
		try {
			if (!Check3Match(note1, note2, note3))
				throw new IllegalStateException("Sizes of notes are not equal to each other.");
		} catch (NullPointerException e) {
			throw new IllegalStateException("Notes were not correctly loaded.");
		}
		try {
			if (!Check3Match(key1, key2, key3))
				throw new IllegalStateException("Sizes of keys are not equal to each other.");
			if (!Check3Match(hkey1, hkey2, hkey3))
				throw new IllegalStateException("Sizes of held keys are not equal to each other.");
		} catch (NullPointerException e) {
			throw new IllegalStateException("Keys were not correctly loaded.");
		}
		if (key1.getWidth() != note1.getWidth())
			throw new IllegalStateException("Widths of notes and keys are not equal to each other.");
		if (hkey1.getWidth() != key1.getWidth() || hkey1.getHeight() != key1.getHeight())
			throw new IllegalStateException("Sizes of keys and held keys are not equal to each other.");

	}

	private final static boolean Check3Match(Image i1, Image i2, Image i3) {
		return i1.getWidth() == i2.getWidth() && i2.getWidth() == i3.getWidth() && i1.getHeight() == i2.getHeight()
				&& i2.getHeight() == i3.getHeight();
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
		return GetMaxDigitWidth() * 9;
	}

	public int GetAccWidth() {
		return (GetMaxDigitWidth() * 5) + digits[10].getWidth() + digits[11].getWidth();
	}

	public int GetMaxDigitWidth() {
		int max = 0;
		for (int i = 0; i < 10; i++) {
			if (digits[i].getWidth() > max) {
				max = digits[i].getWidth();
			}
		}
		return max;
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
		System.arraycopy(composeSpritesFor(hkey1, hkey2, hkey3, columns), 0, a, 18 + columns + columns, columns);
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
