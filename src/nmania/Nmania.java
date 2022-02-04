package nmania;

import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

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
	public static Skin skin;

	public static void LoadManager(String dir) throws IOException {
		bm = new BeatmapManager(dir);
		bm.Init();
	}

	protected void startApp() throws MIDletStateChangeException {
		inst = this;
		if (running)
			return;
		Settings.Load();
		Push(new MainScreen());
	}

	public static void Push(Displayable d) {
		Display.getDisplay(inst).setCurrent(d);
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
	
	public static String GetDevice() {
		return System.getProperty("microedition.platform");
	}

}
