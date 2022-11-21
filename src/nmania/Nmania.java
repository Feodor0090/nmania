package nmania;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
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

	private static Nmania inst;
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
		GL.Log("(app) Pausing the midlet");
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
		if (running) {
			GL.Log("(app) Unpausing the midlet!");
			return;
		}
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
		GL.Log("(app) Changing global displayable to " + d.getClass().getName());
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
		GL.Log("(app) Exit requested!");
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

	public static byte[] get(String url) throws IOException, InterruptedException {
		if (url == null)
			throw new IllegalArgumentException("URL is null");
		ByteArrayOutputStream o = null;
		HttpConnection hc = null;
		InputStream in = null;
		try {
			hc = (HttpConnection) Connector.open(url);
			hc.setRequestMethod("GET");
			hc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
			int r = hc.getResponseCode();
			int redirects = 0;
			while (r == 301 || r == 302 && hc.getHeaderField("Location") != null) {
				String redir = hc.getHeaderField("Location");
				if (redir.startsWith("/")) {
					String tmp = url.substring(url.indexOf("//") + 2);
					String host = url.substring(0, url.indexOf("//")) + "//" + tmp.substring(0, tmp.indexOf("/"));
					redir = host + redir;
				}
				hc.close();
				hc = (HttpConnection) Connector.open(redir);
				hc.setRequestMethod("GET");
				if (redirects++ > 8) {
					throw new IOException("Too many redirects!");
				}
			}
			if (r >= 400 && r != 500)
				throw new IOException(r + " " + hc.getResponseMessage());
			in = hc.openInputStream();
			Thread.sleep(200);
			int read;
			o = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			while ((read = in.read(b)) != -1) {
				o.write(b, 0, read);
			}
			return o.toByteArray();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (hc != null)
					hc.close();
			} catch (IOException e) {
			}
			try {
				if (o != null)
					o.close();
			} catch (IOException e) {
			}
		}
	}

	public static String getUtf(String url) throws IOException, InterruptedException {
		byte[] b = get(url);
		try {
			return new String(b, "UTF-8");
		} catch (Throwable e) {
			e.printStackTrace();
			return new String(b);
		}
	}

	public static String encodeUrl(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ((65 <= ch) && (ch <= 90)) {
				sbuf.append((char) ch);
			} else if ((97 <= ch) && (ch <= 122)) {
				sbuf.append((char) ch);
			} else if ((48 <= ch) && (ch <= 57)) {
				sbuf.append((char) ch);
			} else if (ch == 32) {
				sbuf.append("%20");
			} else if ((ch == 45) || (ch == 95) || (ch == 46) || (ch == 33) || (ch == 126) || (ch == 42) || (ch == 39)
					|| (ch == 40) || (ch == 41) || (ch == 58) || (ch == 47)) {
				sbuf.append((char) ch);
			} else if (ch <= 127) {
				sbuf.append(hex(ch));
			} else if (ch <= 2047) {
				sbuf.append(hex(0xC0 | ch >> 6));
				sbuf.append(hex(0x80 | ch & 0x3F));
			} else {
				sbuf.append(hex(0xE0 | ch >> 12));
				sbuf.append(hex(0x80 | ch >> 6 & 0x3F));
				sbuf.append(hex(0x80 | ch & 0x3F));
			}
		}
		return sbuf.toString();
	}

	private static String hex(int ch) {
		String x = Integer.toHexString(ch);
		return "%" + (x.length() == 1 ? "0" : "") + x;
	}

}
