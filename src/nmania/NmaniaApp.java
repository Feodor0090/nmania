package nmania;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.json.me.JSONObject;

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
			Player p = new Player(b);
			Display.getDisplay(inst).setCurrent(p);
			Thread t = new PlayerThread(p);
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
