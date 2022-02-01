package nmania;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import nmania.Beatmap.ManiaNote;
import nmania.ui.MainScreen;

public final class Nmania extends MIDlet {

	public static Nmania inst;
	public boolean running;

	public Nmania() {
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	public static BeatmapManager bm;

	public static void LoadManager(String dir) throws IOException {
		bm = new BeatmapManager(dir);
		bm.Init();
	}

	protected void startApp() throws MIDletStateChangeException {
		inst = this;
		if (running)
			return;
		Settings.Load();
		Display.getDisplay(inst).setCurrent(new MainScreen());
	}

	public static void Push(Displayable d) {
		Display.getDisplay(inst).setCurrent(d);
	}

	ManiaNote[] RandomNotes(int c, int cols, float beatLength, int offset) {
		if (cols < 2)
			throw new IllegalArgumentException("This generator can handle only 2+ column maps.");
		Random r = new Random();
		Vector notes = new Vector();
		for (int i = 10; i < c; i++) {
			int noteType = r.nextInt(100);
			if (noteType > 80) {
				// single hit
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), r.nextInt(cols) + 1, 0));
			} else if (noteType > 60) {
				// double hit
				int col = r.nextInt(cols) + 1;
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), col, 0));
				int scol;
				while ((scol = r.nextInt(cols) + 1) == col) {
				}
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), scol, 0));
			} else if (noteType > 40) {
				// semi-ladder
				int col = r.nextInt(cols) + 1;
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), col, 0));
				int scol;
				while ((scol = r.nextInt(cols) + 1) == col) {
				}
				notes.addElement(new ManiaNote(offset + (int) ((i * 2 + 1) * beatLength), scol, 0));
			} else if (noteType > 20) {
				// generic holds
				int col = r.nextInt(cols) + 1;
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), col, (int) (beatLength * 2)));
				int scol;
				while ((scol = r.nextInt(cols) + 1) == col) {
				}
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), scol,
						r.nextFloat() > 0.5f ? 0 : (int) (beatLength * 2)));
				i++;
			} else {
				// hold & tap
				int col = r.nextInt(cols) + 1;
				notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), col, (int) (beatLength * 2)));
				int scol;
				while ((scol = r.nextInt(cols) + 1) == col) {
				}
				if (r.nextFloat() > 0.5f)
					notes.addElement(new ManiaNote(offset + (int) (i * beatLength * 2), scol, 0));
				while ((scol = r.nextInt(cols) + 1) == col) {
				}
				notes.addElement(new ManiaNote(offset + (int) ((i + 1) * beatLength * 2), scol, 0));
				i++;
			}
		}
		ManiaNote[] n = new ManiaNote[notes.size()];
		notes.copyInto(n);
		return n;
	}

	public static void exit() {
		inst.notifyDestroyed();
	}

	public static void open(String link) {
		try {
			inst.platformRequest(link);
		} catch (ConnectionNotFoundException e) {
		}
	}
	public static String version() {
		return inst.getAppProperty("MIDlet-Version");
	}

}
