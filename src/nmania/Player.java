package nmania;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.MediaException;

import nmania.Beatmap.Break;
import nmania.replays.ReplayRecorder;
import nmania.ui.ResultsScreen;
import nmania.ui.ng.NmaniaDisplay;
import symnovel.SNUtils;
import tube42.lib.imagelib.ColorUtils;
import tube42.lib.imagelib.ImageUtils;

public final class Player extends GameCanvas {

	public Player(Beatmap map, PlayerBootstrapData opts, Skin s, ILogger log, Displayable next, IInputOverrider input)
			throws IOException, MediaException, InterruptedException {
		super(false);
		setFullScreenMode(true);
		this.menu = next;
		this.data = opts;

		scrW = getWidth();
		scrH = getHeight();

		// step 1: loading background
		log.log("Loading map background");

		Image _bg = null;
		try { // ?full
				// it won't be used anyway if 100%
			if (Settings.dimLevel < 100) // ?full
				_bg = BeatmapManager.getImgFromFS(map.ToGlobalPath(map.image)); // ?full
		} catch (OutOfMemoryError e) { // ?full
		} // ?full
		if (_bg != null) {
			try {
				_bg = NmaniaDisplay.CreateBackground(_bg, scrW, scrH);
			} catch (OutOfMemoryError e) {
				_bg = null;
			}
			bg = _bg;
			_bg = null;
			Thread.sleep(1);
		} else {
			bg = null;
		}

		// step 2: loading music
		log.log("Loading music");
		track = new AudioController(map);
		Thread.sleep(1);

		// step 3: setup difficulty
		// TODO TODO what the fuck i meant a year ago?
		float od = map.difficulty;
		int perfectHW = 16;
		int daMod = opts.mods.GetDA();
		int faMod = opts.mods.GetFA();
		if (daMod < 0) {
			od = od / 1.4f;
			perfectHW = 22;
		} else if (daMod > 0) {
			od = od * 1.4f;
			perfectHW = 11;
		}
		log.log("Setting scoring up");
		hitWindows = new int[] { (int) (188 - 3 * od), (int) (151 - 3 * od), (int) (127 - 3 * od), (int) (97 - 3 * od),
				(int) (64 - 3 * od), perfectHW };
		if (faMod == 2) {
			// perfect mod
			healthValues = new int[] { -1001, -1001, -1001, -1001, -50, 100 };
		} else {
			// sudden death & anything else
			healthValues = new int[] { faMod == 1 ? -1001 : -100, -10, 0, 20, 40, 50 };
		}
		score = new ScoreController(input);
		failCondition = faMod;

		// step 4: setup configs
		log.log("Bootstrapping player");
		this.input = input;
		columnsCount = map.columnsCount;
		columns = new int[columnsCount][];
		currentNote = new int[columnsCount];
		tempKeys = new boolean[columnsCount];
		lastHoldKeys = new boolean[columnsCount];
		pointersNumbers = new int[columnsCount];
		for (int i = 0; i < columnsCount; i++)
			pointersNumbers[i] = -1;
		int[] kbl = Settings.keyLayout[columnsCount - 1];
		if (kbl == null) {
			// this is a fallback to allow autoplay without set layout
			kbl = new int[columnsCount + 1];
			kbl[columnsCount] = -7;
		}
		keyMappings = kbl;
		holdHeadScored = new boolean[columnsCount];
		Thread.sleep(1);

		// step 5: loading beatmap
		log.log("Loading beatmap hitobjects");
		holdHoldingTimes = new int[columnsCount];
		// sorting by time
		SNUtils.sort(map.notes);
		Vector[] _cols = new Vector[columnsCount];
		// filling columns
		for (int i = 0; i < _cols.length; i++)
			_cols[i] = new Vector();
		for (int i = 0; i < map.notes.length; i++) {
			int c = map.notes[i].column - 1;
			_cols[c].addElement(new Integer(map.notes[i].time));
			_cols[c].addElement(new Integer(map.notes[i].duration));
		}
		Thread.sleep(1);
		// burning columns in player
		for (int i = 0; i < columnsCount; i++) {
			columns[i] = new int[_cols[i].size()];
			for (int j = 0; j < _cols[i].size(); j++) {
				columns[i][j] = ((Integer) _cols[i].elementAt(j)).intValue();
			}
		}
		_cols = null;
		Thread.sleep(1);
		currentBreak = 0; // we are starting in gameplay mode
		breaks = Break.Inline(map.breaks); // just burning them in
		// NO CHECKS WILL BE PERFORMED
		if (input == null && data.recordReplay)
			recorder = new ReplayRecorder(score);
		else
			recorder = null;
		Thread.sleep(1);

		// step 6: samples
		log.log("Loading samples");
		if (Settings.gameplaySamples) {
			String sn;
			if (Settings.useBmsSamples && (sn = map.set.findFile("combobreak")) != null) {
				Sample cb;
				try {
					cb = new Sample(map.ToGlobalPath(sn), null);
				} catch (Exception e) { // some maps may have wrongly encoded this shit
					e.printStackTrace();
					cb = new Sample("/sfx/miss.mp3", "audio/mpeg");
				}
				combobreak = cb;
			} else {
				combobreak = new Sample("/sfx/miss.mp3", "audio/mpeg");
			}
			if (Settings.useBmsSamples && (sn = map.set.findFile("sectionpass")) != null) {
				sectionPass = new Sample(map.ToGlobalPath(sn), null);
			} else {
				sectionPass = new Sample("/sfx/pass.mp3", "audio/mpeg");
			}
			if (Settings.useBmsSamples && (sn = map.set.findFile("failsound")) != null) {
				sectionFail = new Sample(map.ToGlobalPath(sn), null);
			} else {
				sectionFail = new Sample("/sfx/fail.mp3", "audio/mpeg");
			}
			if (Settings.useBmsSamples && (sn = map.set.findFile("applause")) != null) {
				applause = map.ToGlobalPath(sn);
			} else {
				applause = "/sfx/applause.wav";
			}
			restart = new Sample("/sfx/restart.wav", "audio/wav");
		} else {
			combobreak = null;
			sectionPass = null;
			sectionFail = null;
			restart = null;
			applause = null;
		}
		if (Settings.hitSamples) { // ?full
			String[] sets = new String[] { "normal", "soft", "drum" }; // ?full
			String[] types = new String[] { "normal", "whistle", "finish", "clap" }; // ?full
			hitSounds = new MultiSample[3][]; // ?full
			for (int i = 0; i < sets.length; i++) { // ?full
				hitSounds[i] = new MultiSample[types.length]; // ?full
				for (int j = 0; j < types.length; j++) { // ?full
					hitSounds[i][j] = new MultiSample(true, "/sfx/" + sets[i] + "-hit" + types[j] + ".wav", "audio/wav", // ?full
							4); // ?full
					Thread.sleep(1); // ?full
				} // ?full
			} // ?full
		} else { // ?full
			hitSounds = null; // ?full
		} // ?full
		defaultHSSet = map.defaultSampleSet; // ?full

		// step 7: cache data for HUD drawing
		log.log("Caching service data");
		fontL = Font.getFont(0, 0, 16);
		numsWidthCache = new int[10];
		accText = score.currentAcc; // chaining
		if (s.richSkin == null) {
			rich = null;
			fillCountersH = fontL.getHeight();
			fillScoreW = fontL.stringWidth("000000000");
			for (int i = 0; i < 10; i++) {
				numsWidthCache[i] = fontL.charWidth((char) ('0' + i));
			}
			fillAccW = fontL.charsWidth(accText, 0, 7);
			kbH = s.keyboardHeight;
			colW = s.GetColumnWidth();
			noteH = s.noteHeight;
			notesColors = s.GetNoteColors(columnsCount);
			notesWithGr = new boolean[columnsCount];
			for (int i = 0; i < columnsCount; i++) {
				notesWithGr[i] = notesColors[i * 2] != notesColors[i * 2 + 1];
			}
			keysColors = s.GetKeyColors(columnsCount);
			holdKeysColors = s.GetHoldKeyColors(columnsCount);
			zeroW = fontL.charWidth('0');
		} else {
			rich = s.richSkin.toPlayerCache(columnsCount);
			fillCountersH = s.richSkin.GetCounterHeight();
			fillScoreW = s.richSkin.GetScoreWidth();
			for (int i = 0; i < 10; i++) {
				numsWidthCache[i] = s.richSkin.digits[i].getWidth();
			}
			fillAccW = s.richSkin.GetAccWidth();
			kbH = s.richSkin.GetKeyboardHeight();
			colW = s.richSkin.GetColumnWidth();
			noteH = s.richSkin.GetNoteHeight();
			notesColors = null;
			notesWithGr = null;
			keysColors = null;
			holdKeysColors = null;
			zeroW = s.richSkin.GetMaxDigitWidth();
		}
		if (bg == null) {
			Image tmp = Image.createImage(fillScoreW, fillCountersH);
			Graphics bgg = tmp.getGraphics();
			bgg.setColor(0);
			bgg.fillRect(0, 0, scrW, scrH);
			scoreBg = Image.createImage(tmp);
			tmp = Image.createImage(fillAccW, fillCountersH);
			bgg = tmp.getGraphics();
			bgg.setColor(0);
			bgg.fillRect(0, 0, scrW, scrH);
			accBg = Image.createImage(tmp);
		} else {
			scoreBg = ImageUtils.crop(bg, scrW - fillScoreW, 0, scrW - 1, fillCountersH + 1);
			accBg = ImageUtils.crop(bg, scrW - fillAccW, scrH - fillCountersH, scrW - 1, scrH - 1);
		}
		kbY = scrH - kbH;
		colWp1 = colW + 1;
		judgmentCenter = s.leftOffset + colWp1 * columnsCount / 2;
		holdW = s.holdWidth;
		localHoldX = (colW - holdW) / 2;
		fillColsW = 1 + (colWp1 * columnsCount) + 6;
		fillAccX = scrW - fillAccW;
		fillScoreX = scrW - fillScoreW;
		UpdateHealthX();
		targetLeftOffset = s.leftOffset;
		leftOffset = s.leftOffset;
		holdsColors = s.GetHoldColors(columnsCount);
		holdsWithGr = new boolean[columnsCount];
		for (int i = 0; i < columnsCount; i++) {
			holdsWithGr[i] = holdsColors[i * 2] != holdsColors[i * 2 + 1];
		}
		colorHoldHeadsAsHolds = s.holdsHaveOwnColors;
		verticalGr = s.verticalGradientOnNotes;
		// don't try to fix the mess above

		Thread.sleep(1);

		// step 8: lock graphics
		log.log("Locking graphics");
		g = getGraphics();
		g.setFont(fontL);
		FillBg();
		flushGraphics();

		if (input != null)
			input.Reset();
		log.log("Ready.");
		System.gc();
	}

	private final void UpdateHealthX() {
		healthX = leftOffset + 1 + (colWp1 * columnsCount);
	}

	// private final boolean playerCanPlay;
	private final int columnsCount;
	private final int[][] columns;
	private final int[] currentNote;
	/**
	 * Copy of {@link #tempKeys}, lags 1 frame behind.
	 */
	private final boolean[] lastHoldKeys;
	/**
	 * Stores currently holded keys.
	 */
	private final boolean[] tempKeys;
	private final int[] pointersNumbers;
	private final int[] holdHoldingTimes;
	private final boolean[] holdHeadScored;
	private final int[] keyMappings;
	private final int[] hitWindows;
	private final int[] healthValues;
	/**
	 * @see ModsState#GetFA
	 */
	private final int failCondition;
	private final int[] breaks;
	private int currentBreak;
	public final ScoreController score;
	public final AudioController track;
	private final PlayerBootstrapData data;
	private final IInputOverrider input;
	private final Image bg;
	private final Graphics g;
	private final int scrW, scrH;
	private final Font fontL;
	private final Image scoreBg;
	private final Image accBg;
	private final int[] numsWidthCache;
	private final Displayable menu;
	private final int[] notesColors;
	private final boolean[] notesWithGr;
	private final int[] holdsColors;
	private final boolean[] holdsWithGr;
	private final int[] keysColors, holdKeysColors;
	private final boolean verticalGr;
	private final boolean colorHoldHeadsAsHolds;
	private final char[] accText;
	private static final char[] hudCache = new char[16];
	private final int kbH, kbY, colW, colWp1;
	private final int fillColsW, fillCountersH, fillScoreW, fillAccW, fillScoreX, fillAccX;
	private final int judgmentCenter;
	private final int localHoldX;
	private int healthX;
	private final int targetLeftOffset;
	private int leftOffset;
	private final int noteH;
	private final int holdW;
	private final int zeroW;
	private final Image[] rich;
	private final ReplayRecorder recorder;

	/**
	 * Gameplay time.
	 */
	private int time;
	private int rollingScore = 0;
	private int lastJudgementTime = -10000;
	private int lastJudgement;
	private int health = 1000;
	private int rollingHealth = 1000;
	/**
	 * Totally processed frames.
	 */
	private int framesPassed = 0;
	// profiler temps
	private int _lastFrames, _lastTime, _lastFps;

	/**
	 * Keep this flag true to keep gameplay in pause (or failed pause) loop and
	 * block input.
	 */
	public boolean isPaused = false;
	/**
	 * Make this flag false to end update loop.
	 */
	public boolean running = true;
	/**
	 * Make this flag true to make gameplay logic enter failed state on next frame.
	 */
	public boolean failed = false;
	/**
	 * Make this flag true before bumping failed state to make player exit as soon
	 * as possible.
	 */
	private boolean exitNow = false;
	private int pauseItem = 0;

	private final Sample combobreak;
	private final Sample sectionPass;
	private final Sample sectionFail;
	private final Sample fail = null;
	private final Sample restart;
	private final MultiSample[][] hitSounds; // ?full
	private final String applause;
	private final int defaultHSSet; // ?full

	public final static String[] judgements = new String[] { "MISS", "MEH", "OK", "GOOD", "GREAT", "PERFECT" };
	public final static int[] judgementColors = new int[] { SNUtils.toARGB("0xF00"), SNUtils.toARGB("0xFA0"),
			SNUtils.toARGB("0x494"), SNUtils.toARGB("0x0B0"), SNUtils.toARGB("0x44F"), SNUtils.toARGB("0x90F") };

	private final int scrollDiv = Settings.speedDiv;

	/**
	 * Clears allocated samples.
	 */
	public final void Dispose() {
		if (hitSounds != null) { // ?full
			for (int i = 0; i < hitSounds.length; i++) { // ?full
				for (int j = 0; j < hitSounds[i].length; j++) { // ?full
					hitSounds[i][j].Dispose(); // ?full
				} // ?full
			} // ?full
		} // ?full
		if (restart != null)
			restart.Dispose();
		if (combobreak != null)
			combobreak.Dispose();
		if (sectionPass != null)
			sectionPass.Dispose();
		if (sectionFail != null)
			sectionFail.Dispose();
		if (fail != null)
			fail.Dispose();
	}

	protected final void keyPressed(final int k) {
		if (isPaused && !failed) {
			if (k == -1 || k == '2') {
				pauseItem--;
				if (pauseItem < 0)
					pauseItem = 2;
			} else if (k == -2 || k == '8') {
				pauseItem++;
				if (pauseItem > 2)
					pauseItem = 0;
			} else if (k == -5 || k == -6 || k == 32 || k == '5' || k == 10) {
				if (pauseItem == 0) {
					System.gc();
					isPaused = false;
					track.Play();
				} else if (pauseItem == 1) {
					ResetPlayer();
				} else if (pauseItem == 2) {
					// order has meaning
					exitNow = true;
					failed = true;
					isPaused = false;
				}
			}
			return;
		}
		if (isPaused && failed) {
			if (k == -1 || k == '2' || k == -2 || k == '8') {
				pauseItem = pauseItem == 0 ? 1 : 0;
				return;
			}
			if (k == -5 || k == -6 || k == 32 || k == '5' || k == 10) {
				if (pauseItem == 0) {
					ResetPlayer();
					return;
				}
				ExitPlayerFromFailedState();
			}
			return;
		}
		if (k == keyMappings[columnsCount]) {
			TriggerPause();
			return;
		}
		if (input != null) {
			GL.Log("(input) Key " + k + " is pressed, but input overrider is attached. It will be ignored.");
			return;
		}
		for (int i = 0; i < columnsCount; i++) {
			if (keyMappings[i] == k) {
				ToggleColumnInputState(i, true);
				return;
			}
		}
	}

	/**
	 * Makes player stop itself and return to menu. Must be used only from failed
	 * state.
	 */
	private final void ExitPlayerFromFailedState() {
		running = false;
		isPaused = false;
		track.Stop();
		Dispose();
		if (menu == null)
			Nmania.PushMainScreen();
		else
			Nmania.Push(menu);
	}

	/**
	 * Performs gameplay restart.
	 */
	private final void ResetPlayer() {
		GL.Log("(player) Reset requested!");
		if (restart != null)
			restart.Play();
		track.Reset();
		rollingHealth = 1000;
		health = 1000;
		rollingScore = 0;
		lastJudgementTime = -10000;
		currentBreak = 0;
		score.Reset();
		for (int i = 0; i < currentNote.length; i++) {
			currentNote[i] = 0;
			holdHeadScored[i] = false;
			tempKeys[i] = false;
			lastHoldKeys[i] = false;
		}
		if (input != null)
			input.Reset();
		if (recorder != null)
			recorder.Reset();
		System.gc();
		failed = false;
		isPaused = false;
		track.Play();
		GL.Log("");
		GL.Log("(player) Player was reset. Now: " + track.Now() + "ms");
	}

	protected final void keyReleased(final int k) {
		if (isPaused)
			return;
		if (input != null)
			return;
		for (int i = 0; i < columnsCount; i++) {
			if (keyMappings[i] == k) {
				ToggleColumnInputState(i, false);
				return;
			}
		}
	}

	public final void ToggleColumnInputState(int column, boolean state) {
		tempKeys[column] = state;
		GL.Log("(input) " + state + " on column " + column + " at time " + time + ", global frame " + framesPassed);
		if (recorder != null)
			recorder.Receive(time, column, state);
	}

	public final void TriggerPause() {
		isPaused = true;
		track.Pause();
		pauseItem = 0;
	}

	protected final void pointerPressed(final int x, final int y) {
		if (isPaused) {
			if (failed) {
				pauseItem = (y << 1) / scrH;
			} else {
				pauseItem = y * 3 / scrH;
			}
			keyPressed(-5);
		} else {
			if (x < leftOffset) {
				TriggerPause();
				return;
			}
			final int column = (x - leftOffset) / colWp1;
			if (column >= columnsCount) {
				TriggerPause();
				return;
			}
			if (input != null)
				return;
			if (pointersNumbers[column] == -1) {
				String pn = System.getProperty("com.nokia.pointer.number");
				int n = pn == null ? 0 : (pn.charAt(0) - '0');
				GL.Log("(input) touch " + n + " pressed at x=" + x);
				pointersNumbers[column] = n;
				ToggleColumnInputState(column, true);
			}
		}
	}

	protected final void pointerReleased(final int x, final int y) {
		if (input != null)
			return;
		String pn = System.getProperty("com.nokia.pointer.number");
		int n = pn == null ? 0 : (pn.charAt(0) - '0');
		GL.Log("(input) touch " + n + " released at x=" + x);
		for (int i = 0; i < columnsCount; i++) {
			if (pointersNumbers[i] == n) {
				GL.Log("(input) column " + i + " released due to touch release");
				pointersNumbers[i] = -1;
				ToggleColumnInputState(i, false);
				break;
			}
		}
	}

	/**
	 * Method that is called by update thread. Contains gameplay logic.
	 */
	public final void Loop() {
		// intro animation
		{
			int s = -fillColsW - 20;
			leftOffset = s;
			int trl = targetLeftOffset - leftOffset;
			for (int i = 0; i < 50; i++) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					return;
				}
				leftOffset = s + (trl * i / 50);
				UpdateHealthX();
				Refill();
				Redraw(true);
			}
			leftOffset = targetLeftOffset;
			UpdateHealthX();
			Refill();
			Redraw(true);
		}
		// begin track
		track.Play();
		// loop
		while (running) {
			// TODO optimize all this shit
			framesPassed++;
			// sync
			int prevtime = time;
			time = track.Now();
			if (input != null) {
				// replay handling
				time = input.UpdatePlayer(this, time);
			}

			if (isPaused) {
				PauseUpdateLoop();
				GL.Log("(player) Player returned from pause loop.");
				time = track.Now();
			}
			// "quit" button in pause
			if (exitNow) {
				QuitFromPauseSequence();
				continue;
			}
			if (failed) {
				FailSequence();
				GL.Log("(player) Player returned from fail loop.");
				continue;
			}

			boolean breakActive = false;
			if (breaks[currentBreak] - time < 0) {
				// break is in progress
				if (breaks[currentBreak] + breaks[currentBreak + 1] - time < 0) {
					// break has just ended, skipping it and repainting bg JIC
					Refill();
					currentBreak += 2;
				} else {
					breakActive = true;
					int timepassed = time - breaks[currentBreak];
					int timeleft = breaks[currentBreak] + breaks[currentBreak + 1] - time;
					if (timepassed < 500) {
						// fading playfield out (break started)
						int fade = scrH * timepassed / 500;
						if (rich == null)
							RedrawHUDVector();
						else
							RedrawHUDRich();
						g.setClip(0, 0, scrW, fade);
						FillBg();
						g.setClip(0, 0, scrW, scrH);
						flushGraphics();
					} else if (timeleft < 500) {
						// fading playfield in (break will end soon)
						int fade = scrH * (500 - timeleft) / 500;
						g.setClip(0, 0, scrW, fade);
						FillBg();
						DrawBorders();
						for (int i = 0; i < columnsCount; i++) {
							DrawKey(i, false);
						}
						Redraw(true);
						if (rich == null)
							RedrawHUDVector();
						else
							RedrawHUDRich();
						g.setClip(0, 0, scrW, scrH);
					} else {
						// idle (break is in progress)
						g.setClip(0, 0, scrW, scrH);
						FillBg();
						// drawing countdown
						DrawBreakCountdown(timeleft);
						flushGraphics();
					}
				}
			}

			// is beatmap over?
			int emptyColumns = 0;

			// checking all columns for incoming hits
			for (int column = 0; column < columnsCount; column++) {
				// loop for each column
				boolean colKey = tempKeys[column];

				// drawing keys (if changed)
				if (colKey && !lastHoldKeys[column])
					DrawKey(column, true);
				else if (!colKey && lastHoldKeys[column])
					DrawKey(column, false);

				// checks for columns with no more notes
				if (currentNote[column] >= columns[column].length) {
					emptyColumns++; // this column is empty
					lastHoldKeys[column] = colKey;
					continue; // nothing to do here anymore
				}

				// diff between current time and note head hit time.
				// positive - it's late, negative - it's early.
				final int diff = time - columns[column][currentNote[column]];
				// abs of diff
				final int adiff = Math.abs(diff);
				// hold length
				final int dur = columns[column][currentNote[column] + 1];
				// diff between time and note tail. Negative=early
				final int taildiff = diff - dur;

				// is it too early to handle?
				if (diff < -hitWindows[0]) {
					lastHoldKeys[column] = colKey;
					continue; // note can't be hit yet
				}

				// if we have input
				if (colKey) {
					// it is a single note
					if (dur == 0) {
						// we are waiting press, not enter with hold.
						if (!lastHoldKeys[column]) { // press, not prehold
							// checking hitwindows
							for (int j = 5; j > -1; j--) {
								if (adiff < hitWindows[j]) {
									GL.Log("(detect) Hit note at " + columns[column][currentNote[column]] + " c="
											+ column + " is hit"); // ?dbg
									CountHit(j);
									currentNote[column] += 2;
									if (hitSounds != null) { // ?full
										if (j != 0) // ?full
											hitSounds[defaultHSSet][0].Play(); // ?full
									} // ?full
									break; // loop on HW
								}
							}
							lastHoldKeys[column] = colKey;
							continue;
						}
					} else {
						// it is a hold
						if (!lastHoldKeys[column]) { // head press
							if (holdHeadScored[column]) {
								GL.Log("(detect) Head note at " + columns[column][currentNote[column]] + " c=" + column
										+ " not hit because it was already hit"); // ?dbg
							} else {
								// checking hitwindow
								for (int j = 5; j > -1; j--) {
									if (adiff < hitWindows[j]) {
										GL.Log("(detect) Head note at " + columns[column][currentNote[column]] + " c="
												+ column + " is hit"); // ?dbg
										CountHit(j);
										if (hitSounds != null) { // ?full
											if (j != 0) // ?full
												hitSounds[defaultHSSet][0].Play(); // ?full
										} // ?full
										holdHeadScored[column] = true;
										break; // loop on HW
									}
								}
								lastHoldKeys[column] = colKey;
								continue;
							}
							holdHoldingTimes[column] = 0; // ready to count holding ms
						} else if (holdHeadScored[column]) {
							// quick guard not to start holding hit without hitting it's head
							// holding the hold!
							holdHoldingTimes[column] += time - prevtime;
							if (holdHoldingTimes[column] > 100) {
								holdHoldingTimes[column] -= 100;
								score.CountTick();
							}
						}
					}
				} else if (lastHoldKeys[column]) {
					// releases
					if (dur != 0) { // nothing to do with hit notes
						// released hold
						if (holdHeadScored[column]) {
							if (taildiff > -hitWindows[0]) {
								// it's its tail
								final int ataildiff = Math.abs(taildiff);
								for (int j = 5; j > -1; j--) {
									if (ataildiff < hitWindows[j]) {
										int time = columns[column][currentNote[column]]; // ?dbg
										GL.Log("(detect) Tail note at " + time + "+" + dur + "=" + (time + dur) + " c="
												+ column + " is hit"); // ?dbg
										CountHit(j);
										currentNote[column] += 2;
										holdHeadScored[column] = false;
										if (hitSounds != null) { // ?full
											if (j != 0) // ?full
												hitSounds[defaultHSSet][2].Play(); // ?full
										} // ?full
										break;
									}
								}
								lastHoldKeys[column] = colKey;
								continue;
							} // else it's still body of hold
						} // else do nothing
					}
				}

				// missing unpressed notes
				if (diff > hitWindows[0]) {
					if (dur == 0) {
						GL.Log("(detect) Hit on c=" + column + " n=" + currentNote[column] + " t=" + time + " has "
								+ diff + "ms diff, skipping"); // ?dbg
						CountHit(0);
						currentNote[column] += 2;
						lastHoldKeys[column] = colKey;
						continue;
					}
					if (!holdHeadScored[column]) {
						GL.Log("(detect) Head on c=" + column + " n=" + currentNote[column] + " t=" + time + " has "
								+ diff + "ms diff, skipping"); // ?dbg
						CountHit(0); // counting hit only for head
						holdHeadScored[column] = true;
						lastHoldKeys[column] = colKey;
						continue;
					} // else hold is holded
				}
				if (dur != 0) {
					if (taildiff > hitWindows[0]) {
						GL.Log("(detect) Tail on c=" + column + " n=" + currentNote[column] + " t=" + time + " has "
								+ taildiff + "ms diff, skipping"); // ?dbg
						CountHit(0);
						currentNote[column] += 2;
						holdHeadScored[column] = false;
					}
				}
				lastHoldKeys[column] = colKey;
			}

			if (emptyColumns == columnsCount) {
				GL.Log("(player) Beatmap passed!");
				PassSequence();
			} else if (!breakActive) {
				Redraw(false);
			}

			if (Settings.forceThreadSwitch)
				Thread.yield();
		}
		GL.Log("(player) Player loop is no longer running.");
	}

	private final void DrawBreakCountdown(int msLeft) {
		int tl = (msLeft / 1000) + 1;
		if (tl > 999)
			tl = 999;
		final int center = scrW >> 1;
		final int ch = scrH >> 1;
		int red;
		if (tl <= 2) {
			red = 255;
		} else if (tl == 3) {
			red = 191;
		} else if (tl == 4) {
			red = 127;
		} else if (tl == 4) {
			red = 63;
		} else {
			red = 0;
		}
		hudCache[0] = '<';
		hudCache[4] = '>';
		hudCache[3] = (char) ('0' + tl % 10);
		tl /= 10;
		hudCache[2] = (char) ('0' + tl % 10);
		tl /= 10;
		hudCache[1] = (char) ('0' + tl);

		g.setColor(-1);
		g.drawChars(hudCache, 0, 5, center - 1, ch - 1, 33);
		g.drawChars(hudCache, 0, 5, center - 1, ch + 1, 33);
		g.drawChars(hudCache, 0, 5, center + 1, ch - 1, 33);
		g.drawChars(hudCache, 0, 5, center + 1, ch + 1, 33);
		g.setColor(red, 0, 0);
		g.drawChars(hudCache, 0, 5, center, ch, 33);
	}

	private final void PassSequence() {
		running = false;
		final String j = "BEATMAP PASSED";
		final int w2 = scrW / 2;
		final int h2 = scrH / 2;
		final int ty = h2 - g.getFont().getHeight() / 2;
		int len = 30;
		final int maxS = (int) Math.sqrt(w2 * w2 + h2 * h2);
		for (int i = 0; i < len; i++) {
			g.setColor(-1);
			int arcS = maxS * i / len;
			g.fillArc(w2 - arcS, h2 - arcS, arcS * 2, arcS * 2, 0, 360);
			g.setColor(0);
			g.drawString(j, w2 + 1, ty - 1, 17);
			g.drawString(j, w2 - 1, ty - 1, 17);
			g.drawString(j, w2 + 1, ty + 1, 17);
			g.drawString(j, w2 - 1, ty + 1, 17);
			g.setColor(20, 255, 20);
			g.drawString(j, w2, ty, 17);
			flushGraphics();
			try {
				Thread.sleep(13);
			} catch (Exception e) {
			}
		}
		Dispose();
		Nmania.Push(new ResultsScreen(data, score, input, recorder, track, applause, bg, menu));
	}

	public final static String[] pt = new String[] { "Continue", "Retry", "Quit" };

	/**
	 * Loop method, that handles pause menu redrawing.
	 */
	private final void PauseUpdateLoop() {
		while (isPaused) {
			int sw3 = scrW / 3;
			int sh5 = scrH / 5;
			for (int i = 0; i < 3; i++) {
				int ry = (scrH * 2 / 5 * (i + 1) / 4) + (sh5 * i);
				g.setGrayScale(i == pauseItem ? 63 : 0);
				g.fillRect(sw3, ry, sw3 - 1, sh5 - 1);
				g.setColor((i == pauseItem ? 255 : 0), 0, 0);
				g.drawRect(sw3, ry, sw3 - 1, sh5 - 1);
				g.setColor(-1);
				g.drawString(pt[i], scrW / 2, ry + sh5 / 2 - fillCountersH / 2, 17); // hcenter+top
			}
			flushGraphics();
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				isPaused = false;
				running = false;
			}
		}
		Refill();
		Redraw(true);
	}

	/**
	 * Method which handles quitting from paused state animation. Will destoroy
	 * player and bring menu.
	 */
	private final void QuitFromPauseSequence() {
		long s = System.currentTimeMillis();
		while (true) {
			int p = (int) (System.currentTimeMillis() - s);
			int h2 = scrH >> 1;
			if (p < 500) {
				final int trw = 50;
				int x = (scrW + trw) * p / 500;
				g.setColor(NmaniaDisplay.DARKER_COLOR);

				g.fillTriangle(x - trw, 0, x - trw, h2, x, h2 >> 1);
				g.fillRect(0, 0, x - trw, h2);
				g.fillTriangle(scrW - x + trw, h2, scrW - x + trw, scrH, scrW - x, h2 + (h2 >> 1));
				g.fillRect(scrW - x + trw, h2, x - trw, h2);
			} else if (p < 800) {
				int h = (scrH - (scrH * (p - 500) / 300)) >> 1;
				FillBg();
				g.setColor(NmaniaDisplay.DARKER_COLOR);
				g.fillRect(0, 0, scrW, h);
				g.fillRect(0, scrH - h, scrW, h);
			} else {
				break;
			}
			flushGraphics();
		}
		ExitPlayerFromFailedState();
	}

	/**
	 * Method that handles failing animation. Player will be paused and show retry-quit menu.
	 */
	private final void FailSequence() {
		track.Pause();
		if (sectionFail != null) {
			sectionFail.Play();
		}
		final String j = "FAILED";
		final int length = 50;
		//TODO animation
		for (int i = 0; i <= length; i++) {
			int h1 = scrH * i / length / 2;
			int w1 = scrW * i / length / 2;
			// rects
			g.setColor(0);
			g.fillRect(0, 0, w1, scrH); // left
			g.fillRect(scrW - w1, 0, w1, scrH); // right
			g.fillRect(0, 0, scrW, h1); // top
			g.fillRect(0, scrH - h1, scrW, h1); // bottom
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

			pauseItem = 0;
			isPaused = true;
			while (isPaused) {
				int sw3 = scrW / 3;
				int sh5 = scrH / 5;
				for (int i = 0; i < 2; i++) {
					int ry = sh5 * (i * 2 + 1);
					g.setGrayScale(i == pauseItem ? 63 : 0);
					g.fillRect(sw3, ry, sw3 - 1, sh5 - 1);
					g.setColor((i == pauseItem ? 255 : 0), 0, 0);
					g.drawRect(sw3, ry, sw3 - 1, sh5 - 1);
					g.setColor(-1);
					g.drawString(i == 0 ? "Retry" : "Quit", scrW >> 1, ry + sh5 / 2 - fillCountersH / 2, 17); // hcenter+top
				}
				flushGraphics();
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					isPaused = false;
					running = false;
				}
			}
			Refill();
			Redraw(true);

	}

	/**
	 * Manages incoming hit: notifies score manager, adjusts health, plays miss sfx
	 * if needed, sets visuals.
	 * 
	 * @param j Type of hit to count
	 */
	private final void CountHit(final int j) {
		GL.Log("(judgment) " + judgements[j] + " on " + time);
		score.CountHit(j);
		health += healthValues[j];
		lastJudgement = j;
		lastJudgementTime = time;

		if (health > 1000)
			health = 1000;
		if (health < 0) {
			if (failCondition == -1) {
				// NoFail can't fail, NF is -1
				health = 1;
			} else if (j == 0) {
				// miss, health is negative - it's a fail
				failed = true;
			} else if (failCondition > 0) {
				// running to -1 with SD/PF fails even if it wasn't miss.
				failed = true;
			} else {
				// meh/ok, NoMod. One more chance.
				health = 0;
			}
		}
		if (combobreak != null) // ?full
			if (j == 0 && !failed && score.currentCombo >= 20) // ?full
				combobreak.Play();// ?full
	}

	/**
	 * Fully redraws the basics of the game. Does not perform flush!
	 */
	public final void Refill() {
		FillBg();
		DrawBorders();
		for (int i = 0; i < columnsCount; i++) {
			DrawKey(i, false);
		}
		if (recorder != null || input != null) {
			String t = input == null ? "REC" : input.GetName();
			final int x = leftOffset + 11 + (columnsCount * colWp1);
			g.setColor(-1);
			g.drawString(t, x + 1, 40, 0);
			g.drawString(t, x - 1, 40, 0);
			g.drawString(t, x + 1, 42, 0);
			g.drawString(t, x - 1, 42, 0);
			if (input == null)
				g.setColor(255, 0, 0);
			else
				g.setColor(0, 200, 0);
			g.drawString(t, x, 41, 0);
		}
	}

	/**
	 * Method to redraw hot areas. Called by update loop.
	 */
	public final void Redraw(boolean flushAll) {
		if (rich == null) {
			g.setClip(0, 0, scrW, kbY);
			RedrawNotesVector();
			g.setClip(0, 0, scrW, scrH);
			RedrawHUDVector();
		} else {
			g.setClip(0, 0, scrW, kbY);
			RedrawNotesRich();
			g.setClip(0, 0, scrW, scrH);
			RedrawHUDRich();
		}
		if (Settings.fullScreenFlush || flushAll) {
			flushGraphics();
		} else {
			// cols
			flushGraphics(leftOffset, 0, fillColsW, scrH);
			// score & acc
			if (Settings.drawHUD) {
				flushGraphics(fillScoreX, 0, fillScoreW, fillCountersH);
				flushGraphics(fillAccX, scrH - fillCountersH, fillAccW, fillCountersH);
			}
		}
	}

	/**
	 * Redraws score, acc, health and judgment.
	 */
	private final void RedrawHUDVector() {
		g.setColor(-1);
		// score & acc
		if (Settings.drawHUD) {
			final int realScore = score.currentHitScore;
			if (realScore != rollingScore) {
				rollingScore += ((realScore - rollingScore) >>> 6) + 1;
			}

			// score calc
			int num = rollingScore;
			int l = 15;
			while (true) {
				final int nnum = (int) ((num * 0x66666667L) >>> 34);
				final int d = num - nnum * 10;
				hudCache[l] = (char) (d + '0');
				if (num < 10)
					break;
				num = nnum;
				l--;
			}
			// score
			g.drawImage(scoreBg, scrW, 0, 24);
			g.drawChars(hudCache, l, 16 - l, scrW, 0, 24);
			// acc
			g.drawImage(accBg, scrW, scrH, 40);
			g.drawChars(accText, 0, 7, scrW, scrH, 40);
		}
		// judgment & combo
		if (time - lastJudgementTime < 256) {
			final String j = judgements[lastJudgement];
			int mix = (time - lastJudgementTime);
			int color = ColorUtils.blend(0, -1, mix);
			g.setColor(color);
			g.drawString(j, judgmentCenter + 1, 99, 17);
			g.drawString(j, judgmentCenter - 1, 99, 17);
			g.drawString(j, judgmentCenter + 1, 101, 17);
			g.drawString(j, judgmentCenter - 1, 101, 17);
			color = ColorUtils.blend(-1, judgementColors[lastJudgement], mix);
			g.setColor(color);
			g.drawString(j, judgmentCenter, 100, 17);
		}
		// combo
		final int combo = score.GetGameplayCombo();
		if (Settings.drawHUD && combo > 0) {
			int num = combo;
			int l = 15;
			while (true) {
				final int nnum = (int) ((num * 0x66666667L) >>> 34);
				final int d = num - nnum * 10;
				hudCache[l] = (char) (d + '0');
				if (num < 10)
					break;
				num = nnum;
				l--;
			}
			num = 16 - l; // reusing var
			g.setColor(-1);
			g.drawChars(hudCache, l, num, judgmentCenter - 1, 99, 33);
			g.drawChars(hudCache, l, num, judgmentCenter + 1, 99, 33);
			g.drawChars(hudCache, l, num, judgmentCenter - 1, 97, 33);
			g.drawChars(hudCache, l, num, judgmentCenter + 1, 97, 33);
			g.setColor(255, Math.min(combo >> 1, 150), 0);
			g.drawChars(hudCache, l, num, judgmentCenter, 98, 33);
		}
		// health
		{
			if (health != rollingHealth) {
				final int delta = (health - rollingHealth);
				rollingHealth += (delta >> 3) + (delta > 0 ? 1 : -1);
			}
			final int red = Math.min(255, rollingHealth >> 1);
			g.setColor(red < 0 ? 0 : (255 - red), 0, 0);
			g.fillRect(healthX, 0, 6, scrH);
			if (rollingHealth > 0) {
				int hh = scrH * rollingHealth / 1000;
				g.setColor(-1);
				g.fillRect(healthX, scrH - hh, 6, hh);
			}
		}
		// fps
		if (Settings.profiler) {
			if (time < _lastTime) {
				_lastTime = 0;
			} else if (time - _lastTime > 1000) {
				_lastTime += 1000;
				_lastFps = framesPassed - _lastFrames;
				_lastFrames = framesPassed;
			}
			g.setColor(0, 255, 0);
			int num = _lastFps;
			int l = 15;
			while (true) {
				final int nnum = (int) ((num * 0x66666667L) >>> 34);
				final int d = num - nnum * 10;
				hudCache[l] = (char) (d + '0');
				if (num < 10)
					break;
				num = nnum;
				l--;
			}
			g.drawChars(hudCache, l, 16 - l, leftOffset + columnsCount * colW, 0, 24);
		}
	}

	/**
	 * Redraws score, acc, health and judgment.
	 */
	private final void RedrawHUDRich() {
		g.setColor(-1);
		// score & acc
		if (Settings.drawHUD) {
			final int realScore = score.currentHitScore;
			if (realScore != rollingScore) {
				rollingScore += (realScore - rollingScore) / 60 + 1;
			}
			g.drawImage(scoreBg, scrW, 0, 24);
			int num = rollingScore;
			int x1 = scrW;
			while (true) {
				final int d = num % 10;
				g.drawImage(rich[d + 6], x1, 0, 24);
				x1 -= numsWidthCache[d];
				if (num < 10)
					break;
				num /= 10;
			}
			g.drawImage(accBg, scrW, scrH, 40);
			x1 = scrW;
			for (int i = 6; i >= 0; i--) {
				final char c = accText[i];
				if (c == '%') {
					g.drawImage(rich[17], x1, scrH, 40);
					x1 -= rich[17].getWidth();
				} else if (c == ',') {
					g.drawImage(rich[16], x1, scrH, 40);
					x1 -= rich[16].getWidth();
				} else if (c != ' ') {
					int idx = (c - '0');
					g.drawImage(rich[idx + 6], x1, scrH, 40);
					x1 -= numsWidthCache[idx];
				}
			}
		}
		// judgment & combo
		if (time - lastJudgementTime < 200) {
			g.drawImage(rich[lastJudgement], judgmentCenter, 100, 17);
		}
		int combo = score.currentCombo;
		if (Settings.drawHUD && combo > 0) {
			if (combo < 10) {
				g.drawImage(rich[combo + 6], judgmentCenter, 100, 33);
			} else if (combo < 100) {
				g.drawImage(rich[combo % 10 + 6], judgmentCenter, 100, 36);
				g.drawImage(rich[combo / 10 + 6], judgmentCenter, 100, 40);
			} else if (combo < 1000) {
				int zw2 = zeroW >> 1;
				g.drawImage(rich[combo % 10 + 6], judgmentCenter + zw2, 100, 36);
				combo /= 10;
				g.drawImage(rich[combo % 10 + 6], judgmentCenter, 100, 33);
				combo /= 10;
				g.drawImage(rich[combo + 6], judgmentCenter - zw2, 100, 40);
			} else {
				g.drawImage(rich[combo % 10 + 6], judgmentCenter + zeroW, 100, 36);
				combo /= 10;
				g.drawImage(rich[combo % 10 + 6], judgmentCenter, 100, 36);
				combo /= 10;
				g.drawImage(rich[combo % 10 + 6], judgmentCenter, 100, 40);
				combo /= 10;
				g.drawImage(rich[combo % 10 + 6], judgmentCenter - zeroW, 100, 40);
				// failsafe for 9999+ combo
				if (combo > 10) {
					combo /= 10;
					g.drawImage(rich[combo % 10 + 6], judgmentCenter - zeroW * 2, 10, 40);
				}
				// Yeah, 99999+ is not supported.
			}
		}
		// health
		{
			if (health != rollingHealth) {
				final int delta = (health - rollingHealth);
				rollingHealth += (delta >> 3) + (delta > 0 ? 1 : -1);
			}
			final int red = Math.min(255, rollingHealth >> 1);
			g.setColor(red < 0 ? 0 : (255 - red), 0, 0);
			g.fillRect(healthX, 0, 6, scrH);
			if (rollingHealth > 0) {
				int hh = scrH * rollingHealth / 1000;
				g.setColor(-1);
				g.fillRect(healthX, scrH - hh, 6, hh);
			}
		}
		// fps
		if (Settings.profiler) {
			g.setColor(0, 255, 0);

			int num = _lastFps;
			int l = 15;
			while (true) {
				final int d = num % 10;
				hudCache[l] = (char) (d + '0');
				if (num < 10)
					break;
				num /= 10;
				l--;
			}
			g.drawChars(hudCache, l, 16 - l, leftOffset + columnsCount * colW, 0, 24);
		}
	}

	/**
	 * Draws bg image.
	 */
	private final void FillBg() {
		if (bg == null) {
			g.setColor(0);
			g.fillRect(0, 0, scrW, scrH);
			return;
		}
		g.drawImage(bg, 0, 0, 0);
	}

	/**
	 * Draws borders around columns.
	 */
	private final void DrawBorders() {
		g.setColor(-1);
		int x = leftOffset;
		for (int i = 0; i <= columnsCount; i++) {
			g.drawLine(x, 0, x, scrH);
			x += colWp1;
		}
		g.drawLine(leftOffset, kbY, leftOffset + columnsCount * colWp1, kbY);
	}

	/**
	 * Draws synthesizer's key.
	 * 
	 * @param k    Key index.
	 * @param hold Is it held down?
	 */
	private final void DrawKey(final int k, final boolean hold) {
		final int x = leftOffset + 1 + (k * colWp1);
		if (rich == null) {
			final int x2 = colW + x - 1;
			final int topClr = (hold ? holdKeysColors : keysColors)[k << 1];
			final int btmClr = (hold ? holdKeysColors : keysColors)[(k << 1) + 1];
			int y = kbY;
			for (int i = 1; i < kbH; i++) {
				y++;
				g.setColor(ColorUtils.blend(btmClr, topClr, (i << 8) / kbH));
				g.drawLine(x, y, x2, y);
			}
		} else {
			if (hold)
				g.drawImage(rich[18 + (columnsCount << 1) + k], x, kbY, 0);
			else
				g.drawImage(rich[18 + k], x, kbY, 0);
		}
	}

	/**
	 * Redraws notes.
	 */
	private final void RedrawNotesVector() {

		// current Y offset due to scroll
		final int notesY = kbY + time / scrollDiv;

		// column X
		int x = leftOffset + 1;

		for (int column = 0; column < columnsCount; column++) {

			// current column
			final int[] c = columns[column];
			final int column2 = column + column;
			final int column21 = column2 + 1;

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
					g.fillRect(x, noteY, colW, lastY - noteY);
				}
				// drawing note
				lastY = noteY - noteH;
				final boolean haveGr = ((dur != 0 && colorHoldHeadsAsHolds) ? holdsWithGr : notesWithGr)[column];
				final int[] noteClr = (dur != 0 && colorHoldHeadsAsHolds) ? holdsColors : notesColors;
				if (haveGr) {
					if (verticalGr) {
						int ly = lastY;
						final int x2 = x + colW - 1;
						for (int j = 0; j < noteH; j++) {
							g.setColor(ColorUtils.blend(noteClr[column2], noteClr[column21], (j << 8) / noteH));
							g.drawLine(x, ly, x2, ly);
							ly++;
						}
					} else {
						int lx = x;
						for (int j = 0; j < colW; j++) {
							g.setColor(ColorUtils.blend(noteClr[column2], noteClr[column21],
									Math.abs((510 * j / colW) - 255)));
							g.drawLine(lx, lastY, lx, lastY + noteH - 1);
							lx++;
						}
					}
				} else {
					g.setColor(noteClr[column2]);
					g.fillRect(x, lastY, colW, noteH);
				}
				// drawing hold
				if (dur != 0) {
					final int holdLen = dur / scrollDiv;
					final int holdH = holdLen - noteH;
					lastY = noteY - holdLen;
					g.setColor(0);
					g.fillRect(x, lastY, colW, holdH);
					g.setColor(holdsColors[column21]);
					g.fillRect(x + localHoldX, lastY, holdW, holdH);
				}
				// are we above the screen?
				if (lastY < 0)
					break;
			}
			if (lastY > 0) {
				g.setColor(0);
				g.fillRect(x, 0, colW, lastY);
			}
			x += colWp1;
		}
	}

	/**
	 * Redraws notes.
	 */
	private final void RedrawNotesRich() {

		// current Y offset due to scroll
		final int notesY = kbY + time / scrollDiv;

		// column X
		int x = leftOffset + 1;

		for (int column = 0; column < columnsCount; column++) {

			// clearing the column
			g.setColor(0);
			g.fillRect(x, 0, colW, kbY);

			// current column
			final int[] c = columns[column];

			// iterating through notes
			for (int i = currentNote[column]; i < c.length; i += 2) {
				// the note Y
				final int noteY = notesY - (c[i] / scrollDiv);

				if (noteY < 0)
					break;

				// hold duration
				final int dur = c[i + 1];

				// drawing hold
				if (dur != 0) {
					final int holdLen = dur / scrollDiv;
					g.setColor(holdsColors[(column << 1) + 1]);
					g.fillRect(x + localHoldX, noteY - holdLen, holdW, holdLen);
				}

				// drawing note
				g.drawImage(rich[18 + columnsCount + column], x, noteY, 36);

			}
			x += colWp1;
		}
	}

	/**
	 * Gets inlined map data.
	 * 
	 * @return Timings for each column. DO NOT TOUCH THIS DATA!
	 */
	public final int[][] GetMap() {
		return columns;
	}
}
