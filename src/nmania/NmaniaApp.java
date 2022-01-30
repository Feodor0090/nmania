package nmania;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.json.me.JSONObject;

import nmania.Beatmap.ManiaNote;
import symnovel.SNUtils;

public final class NmaniaApp extends MIDlet implements ILogger {

	public static NmaniaApp inst;
	public boolean running;

	public NmaniaApp() {
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	Alert a;

	public void log(String s) {
		a.setString(s);
	}

	protected void startApp() throws MIDletStateChangeException {
		inst = this;
		if (running)
			return;
		try {
			final int keys = 4;
			a = new Alert("nmania", "Creating test data", null, AlertType.INFO);
			KeyboardSetup ks = new KeyboardSetup(keys, a);
			Display.getDisplay(inst).setCurrent(ks);
			while(!(Display.getDisplay(inst).getCurrent() instanceof Alert)) 
				Thread.sleep(100);
			Display.getDisplay(inst).setCurrent(a);
			BeatmapSet s = new BeatmapSet();
			s.wdPath = "";
			s.folderName = "test";
			Beatmap b = new Beatmap(new JSONObject(SNUtils.readJARRes("/test/map.json", 4096)));
			b.set = s;
			a.setString("Spawning notes");
			b.columnsCount = keys;
			b.notes = RandomNotes(400, b.columnsCount, 60000f / 160, 200);
			a.setString("Running player");
			Player p = new Player(b, this);
			Thread t = new PlayerThread(p);
			t.start();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	ManiaNote[] RandomNotes(int c, int cols, float beatLength, int offset) {
		if (cols <= 2 || cols > 7)
			throw new IllegalArgumentException("This generator can handle only 3-7 column maps.");
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

}
