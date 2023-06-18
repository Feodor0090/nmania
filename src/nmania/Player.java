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
import nmania.skin.Skin;
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
		try {
			track = new AudioController(map, true);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to init player: " + e.toString());
		}
		Thread.sleep(1);

		// step 3: setup difficulty
		log.log("Setting scoring up");
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
		Thread.sleep(1);
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
		accText = score.currentAcc; // chaining (local ref)
		// SKIN SETUP
		{
			// score & accuracy
			{
				Object numHud = s.GetNumericHUD();
				if (numHud instanceof Integer) {
					countersColor = ((Integer) numHud).intValue();
					numbers = null;
					for (int i = 0; i < 10; i++) {
						numsWidthCache[i] = fontL.charWidth((char) ('0' + i));
					}
					fillCountersH = fontL.getHeight();
					fillScoreW = fontL.stringWidth("000000000");
					fillAccW = fontL.charsWidth(accText, 0, 7);
					zeroW = fontL.charWidth('0');
				} else if (numHud instanceof Image[]) {
					countersColor = -1;
					numbers = (Image[]) numHud;
					int nmw = 0;
					int h = 0;
					for (int i = 0; i < 12; i++) {
						int iw = numbers[i].getWidth();
						if (i < 10)
							numsWidthCache[i] = iw;
						if (iw > nmw)
							nmw = iw;
						if (h == 0)
							h = numbers[i].getHeight();
						if (h != numbers[i].getHeight())
							throw new IllegalArgumentException("Numbers in skin have not equal height!");
					}
					fillCountersH = numbers[0].getHeight();
					fillScoreW = nmw * 9;
					fillAccW = nmw * 7;
					zeroW = nmw;
				} else {
					throw new IllegalArgumentException("Numbers in skin have broken type!");
				}
			}
			// judgments
			{
				judgmentSprites = s.GetJudgments();
			}
			// base stage metrics
			{
				// left offset autofit
				int skinlo = s.GetLeftOffset();
				int hudTakes = Settings.drawHUD ? Math.max(fillAccW, fillScoreW) : fontL.stringWidth("AUTO");
				if (skinlo < 0)
					skinlo = 0;
				if (scrW - hudTakes - skinlo < 40)
					skinlo = 0;
				targetLeftOffset = skinlo;
				leftOffset = targetLeftOffset;

				int skinhw = s.GetHoldWidth();
				int skincw = s.GetColumnWidth();
				if (!data.forbidAftoFit) {
					int colsTake = columnsCount * skincw;
					int avail = scrW - hudTakes - skinlo - HEALTH_WIDTH;
					if (avail < colsTake) {
						skincw = (avail / columnsCount) - 2;
					}
				}
				if (skincw < 0)
					skincw = 1;
				if (skinhw > skincw)
					skinhw = skincw;

				kbH = s.GetKeyboardHeight();
				colW = skincw;
				colWp1 = colW + 1;
				noteH = s.GetNoteHeight();
				holdW = skinhw;
			}
			// calculated stage metrics
			{
				kbY = scrH - kbH - 1;
				judgmentCenter = targetLeftOffset + colWp1 * columnsCount / 2;
				localHoldX = (colW - holdW) / 2;
				fillColsW = 1 + (colWp1 * columnsCount) + HEALTH_WIDTH;
				fillAccX = scrW - fillAccW;
				fillScoreX = scrW - fillScoreW;
				UpdateHealthX();
			}
			// keyboard fill
			{
				Object k = s.GetKeyboardLook(columnsCount);
				Object hk = s.GetHoldKeyboardLook(columnsCount);
				if (k instanceof int[][]) {
					keysColors = (int[][]) k;
					keysSprites = null;
				} else if (k instanceof Image[]) {
					keysColors = null;
					keysSprites = (Image[]) k;
				} else
					throw new IllegalArgumentException();
				if (hk instanceof int[][]) {
					holdKeysColors = (int[][]) hk;
					holdKeysSprites = null;
				} else if (hk instanceof Image[]) {
					holdKeysColors = null;
					holdKeysSprites = (Image[]) hk;
				} else
					throw new IllegalArgumentException();
			}
			// notes fill
			{
				Object n = s.GetNotesLook(columnsCount);
				Object h = s.GetHoldHeadsLook(columnsCount);
				if (n instanceof int[][]) {
					notesColors = (int[][]) n;
					notesSprites = null;
				} else if (n instanceof Image[]) {
					notesColors = null;
					notesSprites = (Image[]) n;
				} else
					throw new IllegalArgumentException();
				if (h instanceof int[][]) {
					holdsColors = (int[][]) h;
					holdsSprites = null;
				} else if (h instanceof Image[]) {
					holdsColors = null;
					holdsSprites = (Image[]) h;
				} else
					throw new IllegalArgumentException();
			}
			// holds body fill
			{
				holdsBodyColors = s.GetHoldBodiesLook(columnsCount);
			}
			// bg fill
			{
				columnsBg = s.GetColumnsBackground(columnsCount);
			}
			// border
			{
				bordersColor = s.GetBordersColor();
			}
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
	private final Image[] judgmentSprites;
	private final int[] numsWidthCache;
	private final int countersColor;
	private final Image[] numbers;
	private final Displayable menu;
	private final int[] columnsBg;
	private final Image[] notesSprites;
	private final int[][] notesColors;
	private final Image[] holdsSprites;
	private final int[][] holdsColors;
	private final int[] holdsBodyColors;
	private final Image[] keysSprites, holdKeysSprites;
	private final int[][] keysColors, holdKeysColors;
	private final int bordersColor;
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
	private static final int HEALTH_WIDTH = 6;
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
	 * Set this flag to true to allow interacting with pause menu.
	 */
	public boolean hudAcceptsInput = true;
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
	private int attempts = 1;

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
			if (!hudAcceptsInput)
				return;
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
			if (!hudAcceptsInput)
				return;
			if (k == -1 || k == '2' || k == -2 || k == '8') {
				pauseItem = pauseItem == 0 ? 1 : 0;
				return;
			}
			if (k == -5 || k == -6 || k == 32 || k == '5' || k == 10) {
				if (pauseItem == 0) {
					ResetPlayer();
					return;
				}
				exitNow = true;
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
		attempts++;
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
			if (!hudAcceptsInput)
				return;
			if (failed) {
				if (y < scrH / 3)
					return;
				pauseItem = (y * 3 / scrH) - 1;
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

			// process pause BEFORE time refresh to avoid desync due to shitty MMAPI
			// implementations
			if (isPaused) {
				PauseUpdateLoop();
				GL.Log("(player) Player returned from pause loop.");
				time = track.Now();
			}

			// sync
			int prevtime = time;
			time = track.Now();
			if (input != null) {
				// replay handling
				time = input.UpdatePlayer(this, time);
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
						RedrawAllHUD();
						g.setClip(0, 0, scrW, fade);
						FillBg();
						g.setClip(0, 0, scrW, scrH);
						flushGraphics();
					} else if (timeleft < 500) {
						// fading playfield in (break will end soon)
						int fade = scrH * (500 - timeleft) / 500;
						FillBg();
						g.setClip(0, 0, scrW, fade); // clip for stage
						RedrawAllHUD();
						DrawBorders();
						for (int i = 0; i < columnsCount; i++) {
							DrawKey(i, false);
						}
						g.setClip(0, 0, scrW, Math.min(kbY, fade)); // clip for notes
						RedrawNotes();
						g.setClip(0, 0, scrW, scrH); // clip reset
						flushGraphics();
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
		long s = System.currentTimeMillis();
		while (true) {
			int p = (int) (System.currentTimeMillis() - s);
			int r = scrW + scrH;
			if (p < 500) {
				int a = 90 * p / 500;
				int x = scrW / 2 - r;
				int y = scrH / 2 - r;
				g.setColor(NmaniaDisplay.PINK_COLOR);
				g.fillArc(x, y, r + r, r + r, 90 - a, a + a);
				g.setColor(NmaniaDisplay.NMANIA_COLOR);
				g.fillArc(x, y, r + r, r + r, 180 + 90 - a, a + a);
			} else if (p < 1000) {
				FillBg();
				int w2 = scrW >> 1;
				int h2 = scrH >> 1;
				int x = w2 * (p - 500) / 500;
				g.setColor(NmaniaDisplay.PINK_COLOR);
				g.fillRect(0, 0, w2 - x, scrH >> 1);
				g.fillRect(w2 + x, 0, w2, h2);
				g.setColor(NmaniaDisplay.NMANIA_COLOR);
				g.fillRect(0, h2, w2 - x, scrH >> 1);
				g.fillRect(w2 + x, h2, w2, h2);
			} else {
				break;
			}
			flushGraphics();
		}
		Dispose();
		Nmania.Push(new ResultsScreen(data, score, input, recorder, track, applause, bg, menu));
	}

	/**
	 * Loop method, that handles pause menu redrawing.
	 */
	private final void PauseUpdateLoop() {
		hudAcceptsInput = false; // block overlay while it's not visible
		long s = System.currentTimeMillis();
		while (true) {
			int p = (int) (System.currentTimeMillis() - s);
			if (p < 1000) {
				final int a = 360 - (p * 360 / 1000);
				final int x = scrW / 2 - 30;
				final int y = scrH / 2 - 30;
				final int d = 60;
				g.setColor(-1);
				g.fillArc(x, y, d, d, 0, 360);
				g.setColor(0);
				g.fillArc(x, y, d, d, 90, a);
				g.setColor(-1);
				g.drawArc(x - 1, y - 1, d, d, 0, 360);
				flushGraphics();
			} else {
				hudAcceptsInput = true;
				break;
			}
		}
		while (isPaused) {
			int sh3 = scrH / 3;
			int bh = sh3 * 2 / 3;

			DrawPauseButton("CONTINUE", pauseItem == 0, (bh >> 2), bh);
			DrawPauseButton("RETRY", pauseItem == 1, sh3 + (bh >> 2), bh);
			DrawPauseButton("QUIT", pauseItem == 2, sh3 + sh3 + (bh >> 2), bh);

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
	 * Method that handles failing animation. Player will be paused and show
	 * retry-quit menu.
	 */
	private final void FailSequence() {
		track.Pause();
		if (sectionFail != null) {
			sectionFail.Play();
		}
		final String t1 = "YOU FAILED AT " + (time * 100 / track.Total()) + "%";
		final String t2 = "WITH " + score.currentHitScore + " SCORE POINTS";
		final String t3 = "TOTAL ATTEMPTS: " + attempts;

		long s = System.currentTimeMillis();
		while (true) {
			int p = (int) (System.currentTimeMillis() - s);
			if (p < 350) {
				int anchor = healthX + (HEALTH_WIDTH >> 1);
				int mw = Math.max(anchor, scrW - anchor);
				int f = mw * p / 350;

				g.setColor(255, 0, 0);
				g.fillRect(anchor - f, 0, f + f, scrH);
			} else if (p < 700) {
				g.setColor(255, 0, 0);
				g.fillRect(0, 0, scrW, scrH);
				g.setColor(0);
				g.fillRect(0, 0, scrW, scrH * (p - 350) / 350);
			} else {
				g.setColor(0);
				g.fillRect(0, 0, scrW, scrH);
				flushGraphics();
				break;
			}
			flushGraphics();
		}
		// text
		int y = g.getFont().getHeight() >> 1;
		g.setColor(-1);
		g.drawString(t1, scrW / 2, y, 17);
		y += g.getFont().getHeight();
		g.setColor(-1);
		g.drawString(t2, scrW / 2, y, 17);
		y += g.getFont().getHeight();
		g.setColor(-1);
		g.drawString(t3, scrW / 2, y, 17);

		// flush
		flushGraphics();

		pauseItem = 0;
		isPaused = true;
		while (isPaused) {
			int sh3 = scrH / 3;
			int bh = sh3 * 2 / 3;

			DrawPauseButton("RETRY", pauseItem == 0, sh3 + (bh >> 2), bh);
			DrawPauseButton("QUIT", pauseItem == 1, sh3 + sh3 + (bh >> 2), bh);

			flushGraphics();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				isPaused = false;
				running = false;
			}

			if (exitNow) {
				QuitFromPauseSequence();
				return;
			}
		}
		Refill();
		Redraw(true);

	}

	private final void DrawPauseButton(String t, boolean selected, int y, int h) {
		g.setColor(selected ? NmaniaDisplay.PINK_COLOR : 0x444444);
		g.fillRoundRect(scrW >> 2, y, scrW >> 1, h, h, h);

		int ty = y + (h >> 1) - (g.getFont().getHeight() >> 1);
		g.setColor(NmaniaDisplay.NEGATIVE_COLOR);
		g.drawString(t, scrW / 2 + 1, ty - 1, 17);
		g.drawString(t, scrW / 2 - 1, ty - 1, 17);
		g.drawString(t, scrW / 2 + 1, ty + 1, 17);
		g.drawString(t, scrW / 2 - 1, ty + 1, 17);
		g.setColor(-1);
		g.drawString(t, scrW / 2, ty, 17);
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
	private final void Refill() {
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
	 * Method to redraw hot areas. Called by update loop. Performs flush.
	 *
	 * @param flushAll True to force fullscreen.
	 */
	private final void Redraw(boolean flushAll) {
		g.setClip(0, 0, scrW, kbY);
		RedrawNotes();
		g.setClip(0, 0, scrW, scrH);
		if (Settings.redrawBorders)
			DrawBorders();
		RedrawAllHUD();

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
	 * Redraws score and acc. Draws nothing if hud is disabled.
	 */
	private final void RedrawScoreAndAcc() {
		if (!Settings.drawHUD)
			return;

		final int realScore = score.currentHitScore;
		if (realScore != rollingScore) {
			rollingScore += ((realScore - rollingScore) >>> 6) + 1;
		}

		if (numbers == null) {
			// vector

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
			g.setColor(countersColor);
			// score
			g.drawImage(scoreBg, scrW, 0, 24);
			g.drawChars(hudCache, l, 16 - l, scrW, 0, 24);
			// acc
			g.drawImage(accBg, scrW, scrH, 40);
			g.drawChars(accText, 0, 7, scrW, scrH, 40);
		} else {
			// raster

			// bgs
			g.drawImage(scoreBg, scrW, 0, 24);
			g.drawImage(accBg, scrW, scrH, 40);

			int num = rollingScore;
			int x1 = scrW;
			while (true) {
				final int d = num % 10;
				g.drawImage(numbers[d], x1, 0, 24);
				x1 -= numsWidthCache[d];
				if (num < 10)
					break;
				num /= 10;
			}
			x1 = scrW;
			for (int i = 6; i >= 0; i--) {
				final char c = accText[i];
				int si;
				if (c == '%')
					si = 12;
				else if (c == ',')
					si = 11;
				else
					si = (c - '0');

				g.drawImage(numbers[si], x1, scrH, 40);
				x1 -= numbers[si].getWidth();
			}
		}
	}

	private final void RedrawJudgment() {
		if (judgmentSprites == null) {
			// vector
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
		} else {
			// raster
			if (time - lastJudgementTime < 200) {
				g.drawImage(judgmentSprites[lastJudgement], judgmentCenter, 100, 17);
			}
		}
	}

	private final void RedrawCombo() {
		int combo = score.GetGameplayCombo();
		if (combo == 0)
			return;
		if (!Settings.drawHUD)
			return;
		if (numbers == null) {
			// vector
			int green = Math.min(combo >> 1, 150);
			int l = 15;
			while (true) {
				final int nnum = (int) ((combo * 0x66666667L) >>> 34);
				final int d = combo - nnum * 10;
				hudCache[l] = (char) (d + '0');
				if (combo < 10)
					break;
				combo = nnum;
				l--;
			}
			int count = 16 - l;
			g.setColor(-1);
			g.drawChars(hudCache, l, count, judgmentCenter - 1, 99, 33);
			g.drawChars(hudCache, l, count, judgmentCenter + 1, 99, 33);
			g.drawChars(hudCache, l, count, judgmentCenter - 1, 97, 33);
			g.drawChars(hudCache, l, count, judgmentCenter + 1, 97, 33);
			g.setColor(255, green, 0);
			g.drawChars(hudCache, l, count, judgmentCenter, 98, 33);
		} else {
			// raster
			if (combo > 99999)
				combo = 99999;
			if (combo < 10) {
				g.drawImage(numbers[combo], judgmentCenter, 100, 33);
			} else if (combo < 100) {
				g.drawImage(numbers[combo % 10], judgmentCenter, 100, 36);
				g.drawImage(numbers[combo / 10], judgmentCenter, 100, 40);
			} else if (combo < 1000) {
				int zw2 = zeroW >> 1;
				g.drawImage(numbers[combo % 10], judgmentCenter + zw2, 100, 36);
				combo /= 10;
				g.drawImage(numbers[combo % 10], judgmentCenter, 100, 33);
				combo /= 10;
				g.drawImage(numbers[combo], judgmentCenter - zw2, 100, 40);
			} else {
				g.drawImage(numbers[combo % 10], judgmentCenter + zeroW, 100, 36);
				combo /= 10;
				g.drawImage(numbers[combo % 10], judgmentCenter, 100, 36);
				combo /= 10;
				g.drawImage(numbers[combo % 10], judgmentCenter, 100, 40);
				combo /= 10;
				g.drawImage(numbers[combo % 10], judgmentCenter - zeroW, 100, 40);
				// failsafe for 9999+ combo
				if (combo > 10) {
					combo /= 10;
					g.drawImage(numbers[combo % 10], judgmentCenter - zeroW * 2, 10, 40);
				}
				// Yeah, 99999+ is not supported.
			}
		}
	}

	private final void RedrawHealth() {
		if (health != rollingHealth) {
			final int delta = (health - rollingHealth);
			rollingHealth += (delta >> 3) + (delta > 0 ? 1 : -1);
		}
		final int red = Math.min(255, rollingHealth >> 1);
		g.setColor(red < 0 ? 0 : (255 - red), 0, 0);
		g.fillRect(healthX, 0, HEALTH_WIDTH, scrH);
		if (rollingHealth > 0) {
			int hh = scrH * rollingHealth / 1000;
			g.setColor(-1);
			g.fillRect(healthX, scrH - hh, HEALTH_WIDTH, hh);
		}
	}

	/**
	 * Redraws score, acc, health and judgment.
	 */
	private final void RedrawAllHUD() {
		RedrawScoreAndAcc();
		RedrawJudgment();
		RedrawCombo();
		RedrawHealth();
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
		g.setColor(bordersColor);
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
		if (hold) {
			if (holdKeysSprites == null) {
				int[] colors = holdKeysColors[k];
				if (colors.length == 1) {
					g.setColor(colors[0]);
					g.fillRect(x, kbY + 1, colW, kbH);
				} else {
					final int x2 = colW + x - 1;
					for (int i = 0; i < kbH; i++) {
						int y = kbY + i + 1;
						g.setColor(colors[i]);
						g.drawLine(x, y, x2, y);
					}
				}
			} else {
				g.drawImage(holdKeysSprites[k], x, kbY + 1, 0);
			}
		} else {
			if (keysSprites == null) {
				// dublicates the piece above
				int[] colors = keysColors[k];
				if (colors.length == 1) {
					g.setColor(colors[0]);
					g.fillRect(x, kbY + 1, colW, kbH);
				} else {
					final int x2 = colW + x - 1;
					for (int i = 0; i < kbH; i++) {
						int y = kbY + i + 1;
						g.setColor(colors[i]);
						g.drawLine(x, y, x2, y);
					}
				}
			} else {
				g.drawImage(keysSprites[k], x, kbY + 1, 0);
			}
		}
	}

	private final void RedrawNotes() {
		// current Y offset due to scroll
		final int notesY = kbY + ((time * scrollDiv) >> 5);

		// column X
		int x = leftOffset + 1;

		for (int column = 0; column < columnsCount; column++) {

			// current column
			final int[] c = columns[column];

			// clearing the column
			g.setColor(columnsBg[column]);
			g.fillRect(x, 0, colW, kbY);

			// iterating through notes
			for (int i = currentNote[column]; i < c.length; i += 2) {
				// the note Y
				final int noteY = notesY - ((c[i] * scrollDiv) >> 5);

				// all visible are drawn
				if (noteY < 0)
					break;

				// hold duration (zero if "hit" note)
				final int dur = c[i + 1];

				// selecting pallete or sprite
				// also drawing hold if it is
				Image spr = null;
				int[] clr = null;
				if (dur == 0) {
					// hit
					if (notesSprites == null) {
						clr = notesColors[column];
					} else {
						spr = notesSprites[column];
					}
				} else {
					// hold
					if (holdsSprites == null) {
						clr = holdsColors[column];
					} else {
						spr = holdsSprites[column];
					}

					// drawing hold body UNDER head
					final int holdLen = ((dur * scrollDiv) >> 5);
					g.setColor(holdsBodyColors[column]);
					g.fillRect(x + localHoldX, noteY - holdLen, holdW, holdLen);
				}

				// actual note drawing
				if (spr == null) {
					// vector
					final int h = noteH;
					if (clr.length == 1) {
						g.setColor(clr[0]);
						g.fillRect(x, noteY - h, colW, noteH);
					} else {
						int ly = noteY - h;
						final int x2 = x + colW - 1;
						for (int j = 0; j < h; j++) {
							g.setColor(clr[j]);
							g.drawLine(x, ly, x2, ly);
							ly++;
						}
					}
				} else {
					// raster
					g.drawImage(spr, x, noteY, 36);
				}

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
