package nmania;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import nmania.ui.MainScreen;
import symnovel.SNUtils;

public final class Nmania extends MIDlet {

	public static Nmania inst;
	public boolean running;
	public static BeatmapManager bm;
	public static Skin skin;
	public static String[] commonText;
	public static String version;

	public Nmania() {
		inst = this;
		version = getAppProperty("MIDlet-Version");
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}


	public static void LoadManager(String dir) throws IOException {
		bm = new BeatmapManager(dir);
		bm.Init();
	}

	protected void startApp() throws MIDletStateChangeException {
		if (running)
			return;
		Settings.Load();
		commonText = getStrings("common");
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

	public static String GetDevice() {
		return System.getProperty("microedition.platform");
	}

	/**
	 * Loads localization file.
	 * 
	 * @param cat    Category of strings.
	 * @param locale Language code to use.
	 * @return List of strings to use.
	 */
	public static String[] getStrings(String cat) {
		try {
			InputStream s = Nmania.class.getResourceAsStream("/text/" + cat + "_" + Settings.locale + ".txt");
			if (s == null)
				s = Nmania.class.getResourceAsStream("/text/" + cat + "_en.txt");

			char[] buf = new char[32 * 1024];
			InputStreamReader isr = new InputStreamReader(s, "UTF-8");
			int l = isr.read(buf);
			isr.close();
			String r = new String(buf, 0, l).replace('\r', ' ');
			return SNUtils.splitFull(r, '\n');
		} catch (Exception e) {
			e.printStackTrace();
			// null is returned to avoid massive try-catch constructions near every call.
			// Normally, it always returns english file.
			return null;
		}
	}

}
