package nmania;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.MediaException;

import nmania.ui.MainScreen;
import nmania.ui.ResultsScreen;
import symnovel.SNUtils;
import tube42.lib.imagelib.ColorUtils;
import tube42.lib.imagelib.ImageFxUtils;
import tube42.lib.imagelib.ImageFxUtils.PixelModifier;
import tube42.lib.imagelib.ImageUtils;

public final class Player extends GameCanvas {

	protected Player(Beatmap map, boolean enableJudgements, ILogger log, Displayable next)
			throws IOException, MediaException {
		super(false);
		setFullScreenMode(true);
		this.menu = next;

		scrW = getWidth();
		scrH = getHeight();

		// step 1: loading background
		log.log("Loading map background");
		Image _bg = BeatmapManager.getImgFromFS(map.ToGlobalPath(map.image));
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
		this.enableJudgements = enableJudgements;

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

		// step 6: samples
		log.log("Loading samples");
		if (Settings.gameplaySamples) {
			combobreak = new Sample(true, "/sfx/miss.mp3", "audio/mpeg");
		} else {
			combobreak = null;
		}
		if (Settings.hitSamples) {
			String[] sets = new String[] { "normal", "soft", "drum" };
			String[] types = new String[] { "normal", "whistle", "finish", "clap" };
			hitSounds = new MultiSample[sets.length][];
			for (int i = 0; i < sets.length; i++) {
				hitSounds[i] = new MultiSample[types.length];
				for (int j = 0; j < types.length; j++) {
					hitSounds[i][j] = new MultiSample(true, "/sfx/" + sets[i] + "-hit" + types[j] + ".wav", "audio/wav",
							4);
				}
			}
		} else {
			hitSounds = null;
		}

		// step 7: cache data for HUD drawing
		log.log("Caching service data");
		fontL = Font.getFont(0, 0, 16);
		fillCountersH = fontL.getHeight();
		{
			fillScoreW = fontL.stringWidth("000000000");
			scoreBg = ImageUtils.crop(bg, scrW - fillScoreW, 0, scrW - 1, fillCountersH + 1);
		}
		numsWidthCache = new int[10];
		for (int i = 0; i < 10; i++) {
			numsWidthCache[i] = fontL.charWidth((char) ('0' + i));
		}
		{
			fillAccW = fontL.charsWidth(accText, 0, accText.length);
			accBg = ImageUtils.crop(bg, scrW - fillAccW, scrH - fillCountersH, scrW - 1, scrH - 1);
		}
		kbH = Settings.keyboardHeight;
		kbY = scrH - kbH;
		colWp1 = Settings.columnWidth + 1;
		judgmentCenter = Settings.leftOffset + (Settings.columnWidth + 1) * columnsCount / 2;
		localHoldX = (Settings.columnWidth - Settings.holdWidth) / 2;
		fillColsW = 1 + (colWp1 * columnsCount) + 6;
		fillAccX = scrW - fillAccW;
		fillScoreX = scrW - fillScoreW;
		healthX = Settings.leftOffset + 1 + (colWp1 * columnsCount);

		// step 8: lock graphics
		log.log("Locking graphics");
		g = getGraphics();
		g.setFont(fontL);

		log.log("Ready.");
		System.gc();
	}

	private final boolean enableJudgements;
	private final int columnsCount;
	private final int[][] columns;
	private final int[] currentNote;
	private final boolean[] lastHoldKeys;
	private final boolean[] holdKeys;
	private final int[] keyMappings;
	private final int[] hitWindows;
	private final int[] healthValues = new int[] { -50, -10, 0, 20, 60, 120 };
	public final ScoreController score;
	public final AudioController track;
	private final Image bg;
	private final Graphics g;
	private final int scrW, scrH;
	private final Font fontL;
	private final Image scoreBg;
	private final Image accBg;
	private final int[] numsWidthCache;
	private final Displayable menu;

	private final int kbH, kbY, colWp1;
	private final int fillColsW, fillCountersH, fillScoreW, fillAccW, fillScoreX, fillAccX;
	private final int judgmentCenter;
	private final int localHoldX;
	private final int healthX;

	private final char[] accText = new char[] { '1', '0', '0', ',', '0', '0', '%' };

	private int time;
	private int rollingScore = 0;
	private int lastJudgementTime = Integer.MIN_VALUE;
	private int lastJudgement;
	private int health = 1000;
	private int rollingHealth = 1000;

	public boolean isPaused;
	public boolean running = true;
	public boolean failed = false;

	private final Sample combobreak;
	private Sample playOver;

	private final MultiSample[][] hitSounds;

	public final static String[] judgements = new String[] { "MISS", "MEH", "OK", "GOOD", "GREAT", "PERFECT" };
	public final static int[] judgementColors = new int[] { SNUtils.toARGB("0xF00"), SNUtils.toARGB("0xFA0"),
			SNUtils.toARGB("0x494"), SNUtils.toARGB("0x0B0"), SNUtils.toARGB("0x44F"), SNUtils.toARGB("0x90F") };
	private final int keyColorTop = SNUtils.toARGB("0x777");
	private final int keyColorTopHold = SNUtils.toARGB("0x0FF");
	private final int keyColorBottom = SNUtils.toARGB("0x69D");

	private final int scrollDiv = Settings.speedDiv;

	protected final void keyPressed(final int k) {
		if (!enableJudgements)
			return;
		for (int i = 0; i < columnsCount; i++) {
			if (keyMappings[i] == k) {
				holdKeys[i] = true;
				DrawKey(i, true);
				return;
			}
		}
	}

	protected final void keyReleased(final int k) {
		if (!enableJudgements)
			return;
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

		if (failed) {
			FailSequence();
			return;
		}

		// is beatmap over?
		int emptyColumns = 0;

		if (enableJudgements) {
			// checking all columns
			for (int column = 0; column < columnsCount; column++) {

				if (currentNote[column] >= columns[column].length) {
					emptyColumns++;
					continue;
				}
				// diff between current time and note hit time.
				// positive - it's late, negative - it's early.
				final int diff = time - columns[column][currentNote[column]];

				// hold length
				final int dur = columns[column][currentNote[column] + 1];

				// is it too early to handle?
				if (diff < -hitWindows[0])
					continue;

				// if we have input
				if (holdKeys[column]) {
					// it is a single note
					if (dur == 0) {
						// we are waiting press, not hold
						if (!lastHoldKeys[column]) {
							// absolute difference
							final int adiff = Math.abs(diff);
							// checking hitwindow
							for (int j = 5; j > -1; j--) {
								if (adiff < hitWindows[j]) {
									CountHit(j);
									score.CountHit(j);
									lastJudgement = j;
									lastJudgementTime = time;
									currentNote[column] += 2;
									if (hitSounds != null && j != 0)
										hitSounds[0][0].Play();
									break;
								}
							}
						}
					} else {
						// it is a hold
						if (!lastHoldKeys[column]) {
							// absolute difference
							final int adiff = Math.abs(diff);
							// checking hitwindow
							for (int j = 5; j > -1; j--) {
								if (adiff < hitWindows[j]) {
									CountHit(j);
									score.CountHit(j);
									lastJudgement = j;
									lastJudgementTime = time;
									if (hitSounds != null && j != 0)
										hitSounds[0][0].Play();
									break;
								}
							}
						} else {
							// holding the hold!
						}
					}
					continue;
				} else if (!holdKeys[column] && lastHoldKeys[column] && dur != 0) {
					// released hold

					// absolute difference
					final int adiff = Math.abs(diff - dur);
					// checking hitwindow
					for (int j = 5; j > -1; j--) {
						if (adiff < hitWindows[j]) {
							CountHit(j);
							score.CountHit(j);
							lastJudgement = j;
							lastJudgementTime = time;
							currentNote[column] += 2;
							if (hitSounds != null && j != 0)
								hitSounds[0][2].Play();
							break;
						}
					}
					if (adiff >= hitWindows[0]) {
						CountHit(0);
						score.CountHit(0);
						lastJudgement = 0;
						lastJudgementTime = time;
						currentNote[column] += 2;
					}
					continue;
				}

				// missing unpressed notes
				if (diff > hitWindows[0]) {
					CountHit(0); // holds decreasing health only once
					score.CountHit(0);
					if (dur != 0)
						score.CountHit(0);
					lastJudgement = 0;
					lastJudgementTime = time;
					currentNote[column] += 2;
					continue;
				}
			}
		} else {
			// auto mode
			for (int column = 0; column < columnsCount; column++) {
				if (currentNote[column] >= columns[column].length) {
					emptyColumns++;
					continue;
				}
				final int diff = time - columns[column][currentNote[column]];
				final int dur = columns[column][currentNote[column] + 1];
				if (diff < 0)
					continue;

				if (dur == 0) {
					CountHit(5);
					score.CountHit(5);
					lastJudgement = 5;
					lastJudgementTime = time;
					currentNote[column] += 2;
					if (hitSounds != null)
						hitSounds[0][0].Play();
				} else {
					if (diff - dur > 0) {
						holdKeys[column] = false;
						CountHit(5);
						score.CountHit(5);
						lastJudgement = 5;
						lastJudgementTime = time;
						currentNote[column] += 2;
						if (hitSounds != null)
							hitSounds[0][2].Play();
					} else if (!holdKeys[column]) {
						holdKeys[column] = true;
						CountHit(5);
						score.CountHit(5);
						lastJudgement = 5;
						lastJudgementTime = time;
						if (hitSounds != null)
							hitSounds[0][0].Play();
					}
				}
			}
		}
		System.arraycopy(holdKeys, 0, lastHoldKeys, 0, columnsCount);

		if (emptyColumns == columnsCount) {
			running = false;
			try {
				if (Settings.gameplaySamples) {
					playOver = new Sample(true, "/sfx/pass.mp3", "audio/mpeg");
					playOver.Play();
				}
			} catch (IOException e1) {
			} catch (MediaException e1) {
			}
			final String j = "DIFFICULTY PASSED";
			for (int i = 0; i < 5; i++) {
				g.setColor(-1);
				g.drawString(j, scrW / 2 + 1, 49, 17);
				g.drawString(j, scrW / 2 - 1, 49, 17);
				g.drawString(j, scrW / 2 + 1, 51, 17);
				g.drawString(j, scrW / 2 - 1, 51, 17);
				if (i % 2 == 0)
					g.setColor(0, 190, 0);
				g.drawString(j, scrW / 2, 50, 17);
				flushGraphics();
				try {
					Thread.sleep(250);
				} catch (Exception e) {
				}
			}
			if (playOver != null)
				playOver.Dispose();
			Display.getDisplay(Nmania.inst).setCurrent(new ResultsScreen(score, track, bg, menu));
		}
	}

	private void FailSequence() {
		running = false;
		track.Stop();
		try {
			if (Settings.gameplaySamples) {
				playOver = new Sample(true, "/sfx/fail.mp3", "audio/mpeg");
				playOver.Play();
			}
		} catch (IOException e1) {
		} catch (MediaException e1) {
		}
		final String j = "FAILED";
		final int length = 50;
		for (int i = 0; i <= length; i++) {
			int h1 = (int) (scrH * i / length) / 2;
			int w1 = (int) (scrW * i / length) / 2;
			// rects
			g.setColor(0);
			g.fillRect(0, 0, h1, scrW);
			g.fillRect(scrH - h1, 0, h1, scrW);
			g.fillRect(0, 0, scrH, w1);
			g.fillRect(0, scrW - w1, scrH, w1);
			// text
			g.setColor(-1);
			g.drawString(j, scrW / 2 + 1, 49, 17);
			g.drawString(j, scrW / 2 - 1, 49, 17);
			g.drawString(j, scrW / 2 + 1, 51, 17);
			g.drawString(j, scrW / 2 - 1, 51, 17);
			g.setColor(210, 0, 0);
			g.drawString(j, scrW / 2, 50, 17);
			// flush & wait
			flushGraphics();
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
		if (playOver != null)
			playOver.Dispose();
		Nmania.Push(menu == null ? (new MainScreen()) : menu);
	}

	private final void CountHit(int j) {
		health += healthValues[j];
		if (health > 1000)
			health = 1000;
		if (health < 0) {
			if (j == 0) {
				failed = true;
			} else {
				health = 0;
			}
		}
		if (j == 0 && !failed && score.currentCombo >= 20 && combobreak != null)
			combobreak.Play();
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
		// cols
		flushGraphics(Settings.leftOffset, 0, fillColsW, scrH);
		// score & acc
		flushGraphics(fillScoreX, 0, fillScoreW, fillCountersH);
		flushGraphics(fillAccX, scrH - fillCountersH, fillAccW, fillCountersH);
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
		// health
		{
			if (health != rollingHealth) {
				final int delta = (health - rollingHealth);
				rollingHealth += delta / 10 + (delta > 0 ? 1 : -1);
			}
			g.setColor(0);
			g.fillRect(healthX, 0, 6, scrH);
			if (rollingHealth > 0) {
				int hh = scrH * rollingHealth / 1000;
				final int clr = Math.min(255, rollingHealth / 2);
				g.setColor(255, clr, clr);
				g.fillRect(healthX, scrH - hh, 6, hh);
			}
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
			if (lastY > 0) {
				g.setColor(0);
				g.fillRect(x, 0, Settings.columnWidth, lastY);
			}
			x += colWp1;
		}
	}

}
