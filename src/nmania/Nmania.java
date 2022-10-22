package nmania;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import nmania.ui.ng.IDisplay;
import nmania.ui.ng.NmaniaDisplay;
import symnovel.SNUtils;

public final class Nmania extends MIDlet implements CommandListener {

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

	/**
	 * Loads BM on a folder.
	 * @param dir Directory to use. Must contain trailing slash. Must not contain file:///.
	 * @throws IOException
	 */
	public static void LoadManager(String dir) throws IOException {
		bm = new BeatmapManager("file:///"+dir);
		bm.Init();
	}

	protected void startApp() throws MIDletStateChangeException {
		if (running)
			return;
		Settings.Load();
		commonText = getStrings("common");
		if (Settings.name == null) {
			final TextBox box = new TextBox("What's your name?", "", 50, 0);
			box.addCommand(new Command("Next", Command.OK, 0));
			box.setCommandListener(this);
			Push(box);
		} else {
			PushMainScreen();
		}

	}

	public static void PushMainScreen() {
		NmaniaDisplay d = new NmaniaDisplay(new nmania.ui.ng.MainScreen());
		Push(d);
		d.Start();
	}

	public static void Push(Displayable d) {
		Display.getDisplay(inst).setCurrent(d);
		if (d instanceof IDisplay)
			((IDisplay) d).ResumeRendering();
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

	public void commandAction(Command arg0, Displayable d) {
		if (d instanceof TextBox) {
			String name = ((TextBox) d).getString();
			if (name.length() == 0)
				return;
			name = name.replace('\n', ' ');
			Settings.name = name;
			Settings.Save();
			PushMainScreen();
		}
	}

}
