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

public class Player extends GameCanvas {

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
			int scoreW = fontL.stringWidth("0000000000");
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

		// step 7: lock graphics
		log.log("Locking graphics");
		g = getGraphics();

		log.log("Ready.");
		System.gc();
	}

	final int columnsCount;
	final int[][] columns;
	final int[] currentNote;
	final boolean[] lastHoldKeys;
	final boolean[] holdKeys;
	final int[] keyMappings;
	final int[] hitWindows;
	final ScoreController score;
	final AudioController track;
	final Image bg;
	final Graphics g;
	final int scrW, scrH;
	final Font fontL;
	final Image scoreBg;
	final Image accBg;

	final char[] accText = new char[] { '1', '0', '0', ',', '0', '0', '%' };

	int time;
	int rollingScore = 0;
	int lastJudgementTime;
	int lastJudgement;

	protected final void keyPressed(int k) {
		for (int i = 0; i < columnsCount; i++) {
			if (keyMappings[i] == k) {
				holdKeys[i] = true;
				DrawKey(i, true);
				return;
			}
		}
	}

	protected void keyReleased(int k) {
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

		// missing unpressed notes
		for (int i = 0; i < columnsCount; i++) {
			if (time - columns[i][currentNote[i]] > hitWindows[0]) {
				score.CountHit(0);
				lastJudgement = 0;
				lastJudgementTime = time;
				currentNote[i] += 2;
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
		g.setClip(0, 0, scrW, scrH - Settings.keyboardHeight);
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
			int realScore = score.maxHitScore;
			if (realScore != rollingScore) {
				rollingScore += (realScore - rollingScore) / 60 + 1;
			}
			g.drawImage(scoreBg, scrW, 0, Graphics.RIGHT | Graphics.TOP);
			g.setFont(fontL);
			int num = rollingScore;
			int x1 = scrW - 0;
			int anchor = Graphics.TOP | Graphics.RIGHT;
			while (true) {
				int d = num % 10;
				g.drawChar((char) (d + '0'), x1, 0, anchor);
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
			g.drawChars(accText, 0, accText.length, scrW, scrH, 40);
		}
		// judgment
		if (time - lastJudgementTime < 200) {
			final int x = Settings.leftOffset + (Settings.columnWidth + 1) * columnsCount / 2;
			g.drawString(judgements[lastJudgement], x + 1, 99, 17);
			g.drawString(judgements[lastJudgement], x - 1, 99, 17);
			g.drawString(judgements[lastJudgement], x + 1, 101, 17);
			g.drawString(judgements[lastJudgement], x - 1, 101, 17);
			g.setColor(judgementColors[lastJudgement]);
			g.drawString(judgements[lastJudgement], x, 100, 17);
		}
	}

	private final void FillBg() {
		g.drawImage(bg, 0, 0, 0);
	}

	private final void DrawBorders() {
		g.setColor(-1);
		for (int i = 0; i <= columnsCount; i++) {
			int x = Settings.leftOffset + (i * (Settings.columnWidth + 1));
			g.drawLine(x, 0, x, scrH);
		}
		int ky = scrH - Settings.keyboardHeight;
		g.drawLine(Settings.leftOffset, ky, Settings.leftOffset + columnsCount * (Settings.columnWidth + 1), ky);
	}

	private final void DrawKey(int k, boolean hold) {
		int x = Settings.leftOffset + 1 + (k * (Settings.columnWidth + 1));
		int x2 = Settings.columnWidth + x - 1;
		int topClr = hold ? keyColorTopHold : keyColorTop;
		int y = scrH - Settings.keyboardHeight + 1;
		for (int i = 0; i < Settings.keyboardHeight; i++) {
			g.setColor(ColorUtils.blend(keyColorBottom, topClr, i * 255 / Settings.keyboardHeight));
			g.drawLine(x, y, x2, y);
			y++;
		}
	}

	private final int keyColorTop = SNUtils.toARGB("0x777");
	private final int keyColorTopHold = SNUtils.toARGB("0x0FF");
	private final int keyColorBottom = SNUtils.toARGB("0x69D");

	private final int scrollDiv = 4;

	private final void RedrawNotes() {
		// TODO OPTIMIZATION!!11!1!!
		int hitLineY = scrH - Settings.keyboardHeight;
		int notesY = hitLineY + time / scrollDiv;
		for (int column = 0; column < columnsCount; column++) {
			int x = Settings.leftOffset + 1 + (column * (Settings.columnWidth + 1));
			int[] c = columns[column];
			int lastY = hitLineY;
			for (int i = currentNote[column]; i < c.length; i += 2) {
				int noteY = c[i] / scrollDiv;
				int dur = c[i + 1];
				noteY = notesY - noteY;
				if (lastY > noteY) {
					g.setColor(0);
					g.fillRect(x, noteY, Settings.columnWidth, lastY - noteY);
				}
				g.setColor(255, 0, 0);
				lastY = noteY - Settings.noteHeight;
				g.fillRect(x, lastY, Settings.columnWidth, Settings.noteHeight);
				if (dur != 0) {
					lastY = noteY - dur / scrollDiv;
					g.setColor(0);
					g.fillRect(x, lastY, Settings.columnWidth, dur / scrollDiv - Settings.noteHeight);
					g.setColor(0, 255, 0);
					g.fillRect(x + (Settings.columnWidth - Settings.holdWidth) / 2, lastY, Settings.holdWidth,
							dur / scrollDiv - Settings.noteHeight);
				}
				if (lastY < 0)
					break;
			}
		}
	}

	private final String[] judgements = new String[] { "MISS", "MEH", "OK", "GOOD", "GREAT", "PERFECT" };
	private final int[] judgementColors = new int[] { SNUtils.toARGB("0xF00"), SNUtils.toARGB("0xFA0"),
			SNUtils.toARGB("0x3C3"), SNUtils.toARGB("0x0F0"), SNUtils.toARGB("0x44F"), SNUtils.toARGB("0x50F") };
	private final int[] numsWidthCache;

}
