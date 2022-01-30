package nmania;

import java.util.Random;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.json.me.JSONObject;

import nmania.Beatmap.ManiaNote;
import symnovel.SNUtils;

public final class NmaniaApp extends MIDlet {

	public static NmaniaApp inst;
	public boolean running;
	public NmaniaApp() {
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		inst = this;
		if(running) return;
		try {
			BeatmapSet s = new BeatmapSet();
			s.wdPath="";
			s.folderName="test";
			Beatmap b = new Beatmap(new JSONObject(SNUtils.readJARRes("/test/map.json", 4096)));
			b.set = s;
			b.notes = RandomNotes(1000, 4, 60000f/160);
			Player p = new Player(b);
			Display.getDisplay(inst).setCurrent(p);
			Thread t = new PlayerThread(p);
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	ManiaNote[] RandomNotes(int c, int cols, float beatLength) {
		Random r = new Random();
		ManiaNote[] n = new ManiaNote[c];
		for (int i = 0; i < c; i++) {
			n[i] = new ManiaNote((int) (i*beatLength), r.nextInt(cols)+1, (int)(r.nextInt()%2==1?beatLength/2:0));
		}
		return n;
	}

}
