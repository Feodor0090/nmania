package nmania;

import java.io.IOException;

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

public final class Nmania extends MIDlet implements CommandListener {

	public static Nmania inst;
	public boolean running;
	public static BeatmapManager bm;
	public static Skin skin;
	public static String version;
	private static Display disp;

	public Nmania() {
		inst = this;
		String v = getAppProperty("MIDlet-Version");
		String commit = getAppProperty("Commit");
		if (commit != null) {
			v = v + "+" + commit;
		}
		version = v + " [lite]";
		version = v; // ?full
		version = v + " [debug]";// ?dbg
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	/**
	 * Loads BM on a folder.
	 * 
	 * @param dir Directory to use. Must contain trailing slash. Must not contain
	 *            file:///.
	 * @throws IOException
	 */
	public static void LoadManager(String dir) throws IOException {
		bm = new BeatmapManager("file:///" + dir);
		bm.Init();
	}

	protected void startApp() throws MIDletStateChangeException {
		if (running)
			return;
		Settings.Load();
		GL.Create(true);// ?dbg
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
		GL.Log("(ui) Changing global displayable to " + d.getClass().getName());
		if (disp == null)
			disp = Display.getDisplay(inst);

		Displayable curr = disp.getCurrent();
		if (curr == d)
			return;
		if (curr instanceof IDisplay)
			((IDisplay) curr).PauseRendering();
		disp.setCurrent(d);
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

	public void commandAction(Command arg0, Displayable d) {
		if (d instanceof TextBox) {
			String name = ((TextBox) d).getString().trim();
			if (name.length() == 0)
				return;
			name = name.replace('\n', ' ');
			Settings.name = name;
			Settings.Save();
			PushMainScreen();
		}
	}

}
