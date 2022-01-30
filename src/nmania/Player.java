package nmania;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.MediaException;

import symnovel.SNUtils;
import tube42.lib.imagelib.ColorUtils;
import tube42.lib.imagelib.ImageFxUtils;
import tube42.lib.imagelib.ImageFxUtils.PixelModifier;
import tube42.lib.imagelib.ImageUtils;

public final class Player extends GameCanvas {

	protected Player(Beatmap map, ILogger log) throws IOException, MediaException {
		super(false);
		setFullScreenMode(true);

		scrW = getWidth();
		scrH = getHeight();

		// step 1: loading background
		log.log("Loading map background");
		Image _bg = Image.createImage(map.ToGlobalPath(map.image));
		_bg = ImageUtils.resize(_bg, scrW, scrH, true, false);
		bg = ImageFxUtils.applyModifier(_bg, new PixelModifier() {
			public int apply(int p, int x, int y) {
				return ColorUtils.blend(p, 0xff000000, (int) ((1f - Settings.bgDim) * 255));
			}
		});
		_bg = null;

		// step 2: loading music
		log.log("Loading music");
		track = new AudioController(map);

		// step 3: setup difficulty
		// TODO
		log.log("Setting scoring up");
		hitWindows = new int[] { 200, 150, 100, 50, 25, 10 };
		score = new ScoreController();

		// step 4: setup configs
		log.log("Bootstrapping player");
		columnsCount = map.columnsCount;
		columns = new int[columnsCount][];
		currentNote = new int[columnsCount];
		holdKeys = new boolean[columnsCount];
		lastHoldKeys = new boolean[columnsCount];
		keyMappings = Settings.keyLayout[columnsCount - 1];

		// step 5: loading beatmap
		log.log("Loading beatmap hitobjects");
		SNUtils.sort(map.notes);
		Vector[] _cols = new Vector[columnsCount];
		for (int i = 0; i < _cols.length; i++)
			_cols[i] = new Vector();
		for (int i = 0; i < map.notes.length; i++) {
			int c = map.notes[i].column - 1;
			_cols[c].addElement(new Integer(map.notes[i].time));
			_cols[c].addElement(new Integer(map.notes[i].duration));
		}
		for (int i = 0; i < columnsCount; i++) {
			columns[i] = new int[_cols[i].size()];
			for (int j = 0; j < _cols[i].size(); j++) {
				columns[i][j] = ((Integer) _cols[i].elementAt(j)).intValue();
			}
		}
		_cols = null;

		// step 6: cache data for HUD drawing
		log.log("Caching service data");
		fontL = Font.getFont(0, 0, 16);
		int fontLh = fontL.getHeight();
		{
			int scoreW = fontL.stringWidth("000000000");
			scoreBg = ImageUtils.crop(bg, scrW - scoreW, 0, scrW - 1, fontLh + 1);
		}
		numsWidthCache = new int[10];
		for (int i = 0; i < 10; i++) {
			numsWidthCache[i] = fontL.charWidth((char) ('0' + i));
		}
		{
			int accW = fontL.charsWidth(accText, 0, accText.length);
			accBg = ImageUtils.crop(bg, scrW - accW, scrH - fontLh, scrW - 1, scrH - 1);
		}
		kbH = Settings.keyboardHeight;
		kbY = scrH - kbH;
		colWp1 = Settings.columnWidth + 1;
		judgmentCenter = Settings.leftOffset + (Settings.columnWidth + 1) * columnsCount / 2;
		localHoldX = (Settings.columnWidth - Settings.holdWidth) / 2;

		// step 7: lock graphics
		log.log("Locking graphics");
		g = getGraphics();
		g.setFont(fontL);

		log.log("Ready.");
		System.gc();
	}

	private final int columnsCount;
	private final int[][] columns;
	private final int[] currentNote;
	private final boolean[] lastHoldKeys;
	private final boolean[] holdKeys;
	private final int[] keyMappings;
	private final int[] hitWindows;
	public final ScoreController score;
	public final AudioController track;
	private final Image bg;
	private final Graphics g;
	private final int scrW, scrH;
	private final Font fontL;
	private final Image scoreBg;
	private final Image accBg;
	private final int[] numsWidthCache;

	private final int kbH, kbY, colWp1;
	private final int judgmentCenter;
	private final int localHoldX;

	private final char[] accText = new char[] { '1', '0', '0', ',', '0', '0', '%' };

	private int time;
	private int rollingScore = 0;
	private int lastJudgementTime;
	private int lastJudgement;

	private final static String[] judgements = new String[] { "MISS", "MEH", "OK", "GOOD", "GREAT", "PERFECT" };
	private final static int[] judgementColors = new int[] { SNUtils.toARGB("0xF00"), SNUtils.toARGB("0xFA0"),
			SNUtils.toARGB("0x494"), SNUtils.toARGB("0x0B0"), SNUtils.toARGB("0x44F"), SNUtils.toARGB("0x90F") };
	private final int keyColorTop = SNUtils.toARGB("0x777");
	private final int keyColorTopHold = SNUtils.toARGB("0x0FF");
	private final int keyColorBottom = SNUtils.toARGB("0x69D");

	private final static int scrollDiv = 4;

	protected final void keyPressed(final int k) {
		for (int i = 0; i < columnsCount; i++) {
			if (keyMappings[i] == k) {
				holdKeys[i] = true;
				DrawKey(i, true);
				return;
			}
		}
	}

	protected final void keyReleased(final int k) {
		for (int i = 0; i < columnsCount; i++) {
			if (keyMappings[i] == k) {
				holdKeys[i] = false;
				DrawKey(i, false);
				return;
			}
		}
	}

	public final void Update() {
		// sync
		time = track.Now();

		// checking all columns
		for (int column = 0; column < columnsCount; column++) {

			// diff between current time and note hit time.
			// positive - it's late, negative - it's early.
			final int diff = time - columns[column][currentNote[column]];

			// is it too early to handle?
			if (diff < -hitWindows[0])
				continue;

			// if we have input
			if (holdKeys[column]) {
				// absolute difference
				final int adiff = Math.abs(diff);
				// checking hitwindow
				for (int j = 5; j > -1; j--) {
					if (adiff < hitWindows[j]) {
						score.CountHit(j);
						lastJudgement = j;
						lastJudgementTime = time;
						currentNote[column] += 2;
						break;
					}
				}
				continue;
			}

			// missing unpressed notes
			if (diff > hitWindows[0]) {
				score.CountHit(0);
				lastJudgement = 0;
				lastJudgementTime = time;
				currentNote[column] += 2;
				continue;
			}
		}
	}

	// drawing section

	public final void Refill() {
		FillBg();
		DrawBorders();
		for (int i = 0; i < columnsCount; i++) {
			DrawKey(i, false);
		}
		flushGraphics();
	}

	public final void Redraw() {
		g.setClip(0, 0, scrW, kbY);
		RedrawNotes();
		g.setClip(0, 0, scrW, scrH);
		RedrawHUD();
		{
			g.setColor(0);
			g.fillRect(0, 0, Settings.leftOffset, fontL.getHeight());
			g.setColor(0, 255, 0);
			g.drawString(String.valueOf(PlayerThread.fps), 0, 0, 0);
		}
		flushGraphics();
	}

	private void RedrawHUD() {
		g.setColor(-1);
		// score
		{
			final int realScore = score.currentHitScore;
			if (realScore != rollingScore) {
				rollingScore += (realScore - rollingScore) / 60 + 1;
			}
			g.drawImage(scoreBg, scrW, 0, 24);
			int num = rollingScore;
			int x1 = scrW - 0;
			while (true) {
				final int d = num % 10;
				g.drawChar((char) (d + '0'), x1, 0, 24);
				x1 -= numsWidthCache[d];
				if (num < 10)
					break;
				num /= 10;
			}
		}
		// acc
		{
			int accRaw = score.GetAccuracy();
			accText[5] = (char) (accRaw % 10 + '0');
			accRaw /= 10;
			accText[4] = (char) (accRaw % 10 + '0');
			accRaw /= 10;
			accText[2] = (char) (accRaw % 10 + '0');
			accRaw /= 10;
			accText[1] = (char) (accRaw % 10 + '0');
			accRaw /= 10;
			accText[0] = accRaw == 0 ? ' ' : '1';
			g.drawImage(accBg, scrW, scrH, 40);
			g.drawChars(accText, 0, 7, scrW, scrH, 40);
		}
		// judgment
		if (time - lastJudgementTime < 200) {
			final String j = judgements[lastJudgement];
			g.drawString(j, judgmentCenter + 1, 99, 17);
			g.drawString(j, judgmentCenter - 1, 99, 17);
			g.drawString(j, judgmentCenter + 1, 101, 17);
			g.drawString(j, judgmentCenter - 1, 101, 17);
			g.setColor(judgementColors[lastJudgement]);
			g.drawString(j, judgmentCenter, 100, 17);
		}
	}

	private final void FillBg() {
		g.drawImage(bg, 0, 0, 0);
	}

	private final void DrawBorders() {
		g.setColor(-1);
		int x = Settings.leftOffset;
		for (int i = 0; i <= columnsCount; i++) {
			g.drawLine(x, 0, x, scrH);
			x += colWp1;
		}
		g.drawLine(Settings.leftOffset, kbY, Settings.leftOffset + columnsCount * colWp1, kbY);
	}

	private final void DrawKey(final int k, final boolean hold) {
		final int x = Settings.leftOffset + 1 + (k * (Settings.columnWidth + 1));
		final int x2 = Settings.columnWidth + x - 1;
		final int topClr = hold ? keyColorTopHold : keyColorTop;
		int y = kbY;
		for (int i = 1; i < kbH; i++) {
			y++;
			g.setColor(ColorUtils.blend(keyColorBottom, topClr, i * 255 / kbH));
			g.drawLine(x, y, x2, y);
		}
	}

	private final void RedrawNotes() {

		// current Y offset due to scroll
		final int notesY = kbY + time / scrollDiv;

		// column X
		int x = Settings.leftOffset + 1;

		for (int column = 0; column < columnsCount; column++) {

			// current column
			final int[] c = columns[column];

			// y to which we can fill the column with black
			int lastY = kbY;

			// iterating through notes
			for (int i = currentNote[column]; i < c.length; i += 2) {
				// the note Y
				final int noteY = notesY - (c[i] / scrollDiv);
				// hold duration
				final int dur = c[i + 1];
				// filling empty space
				if (lastY > noteY) {
					g.setColor(0);
					g.fillRect(x, noteY, Settings.columnWidth, lastY - noteY);
				}
				// drawing note
				g.setColor(255, 0, 0);
				lastY = noteY - Settings.noteHeight;
				g.fillRect(x, lastY, Settings.columnWidth, Settings.noteHeight);
				// drawing hold
				if (dur != 0) {
					final int holdLen = dur / scrollDiv;
					final int holdH = holdLen - Settings.noteHeight;
					lastY = noteY - holdLen;
					g.setColor(0);
					g.fillRect(x, lastY, Settings.columnWidth, holdH);
					g.setColor(0, 255, 0);
					g.fillRect(x + localHoldX, lastY, Settings.holdWidth, holdH);
				}
				// are we above the screen?
				if (lastY < 0)
					break;
			}
			x += colWp1;
		}
	}

}
