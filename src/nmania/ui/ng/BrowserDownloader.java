package nmania.ui.ng;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.*;
import javax.microedition.io.file.FileConnection;

import nmania.GL;

public class BrowserDownloader extends Alert implements Runnable {

	private String fileName;
	private String url;
	Thread t;
	private boolean stop;

	public BrowserDownloader(String title, String url, String fileName) {
		super("Connecting...", "Downloading beatmap " + title + ". Closing this screen will cancel the action.");
		this.url = url;
		this.fileName = fileName;
	}

	public void run() {
		download();
		t = null;
	}

	public void OnEnter(IDisplay d) {
		t = new Thread(this, "BM downloader");
		t.start();
	}

	public boolean OnExit(IDisplay d) {
		if (t != null)
			t.interrupt();
		stop = true;
		return super.OnExit(d);
	}

	public void download() {
		GL.Log("(browser) Downloading " + title);
		GL.Log("(browser) Target file: " + fileName);
		FileConnection fc = null;
		OutputStream out = null;
		HttpConnection hc = null;
		InputStream in = null;
		try {
			fc = (FileConnection) Connector.open(fileName, Connector.READ_WRITE);
			Thread.sleep(1);
			if (fc.exists()) {
				fc.close();
				title = "File already exists. Aborted.";
				return;
			}
			fc.create();
			hc = (HttpConnection) Connector.open(url);
			hc.setRequestMethod("GET");
			int r;
			try {
				r = hc.getResponseCode();
				Thread.sleep(1);
			} catch (Exception e) { // both interrupt and IO fail
				try {
					fc.delete();
					fc.close();
					hc.close();
				} catch (Exception e2) {
					GL.Log("(browser) Failed to close OSZ connection! " + e2.toString());
				}
				GL.Log("(browser) Failed to get response code");
				title = "Failed to connect!";
				return;
			}
			while (r == 301 || r == 302) {
				String redir = hc.getHeaderField("Location");
				GL.Log("(browser) Redirected to " + redir + " with code " + r);
				if (redir.startsWith("/")) {
					String tmp = url.substring(url.indexOf("//") + 2);
					String host = url.substring(0, url.indexOf("//")) + "//" + tmp.substring(0, tmp.indexOf("/"));
					redir = host + redir;
				}
				hc.close();
				hc = (HttpConnection) Connector.open(redir);
				hc.setRequestMethod("GET");
				Thread.sleep(1);
				r = hc.getResponseCode();
				Thread.sleep(1);
			}
			if (r >= 400) {
				GL.Log("(browser) HTTP error " + r);
				title = "Connection failed (http code " + r + ")";
				try {
					fc.delete();
					fc.close();
					hc.close();
				} catch (Exception e2) {
					GL.Log("(browser) Failed to close OSZ connection! " + e2.toString());
				}
				return;
			}

			int len = (int) hc.getLength();
			GL.Log("(browser) Expected size: " + (len >> 10) + "KB");
			long aval = fc.availableSize(); // leave 128KB
			if (len > aval - (1024L * 128)) {
				GL.Log("(browser) Available space is " + (aval >> 10) + "KB but " + (len >> 10) + "KB is needed");
				title = "Not enough space!";
				SetText("Beatmap file is " + (len >> 10)
						+ "KB in size. Your memory card on which game is working has only " + (aval >> 10)
						+ "KB available. Free up more space or use another disk.");
				try {
					fc.delete();
					fc.close();
					hc.close();
				} catch (Exception e2) {
					GL.Log("(browser) Failed to close OSZ connection! " + e2.toString());
				}
				return;
			}
			title = "Downloading";
			out = fc.openOutputStream();
			in = hc.openInputStream();
			final int bufSize = 1024 * 8;
			byte[] buf = new byte[bufSize];
			int read = 0;
			int total = 0;
			while ((read = in.read(buf)) != -1) {
				out.write(buf, 0, read);
				total += read;
				String pc = "";
				if (len != 0) {
					pc = " (" + (total * 100 / len) + "%)";
				}
				title = "Downloaded " + (total / 1024) / 10 / 100f + "MB" + pc + ", wait...";
				Thread.yield();
				if (stop) {
					out.close();
					out = null;
					fc.delete();
					fc.close();
					fc = null;
					in.close();
					in = null;
					hc.close();
					hc = null;
					return;
				}
			}
			title = "Done! Close this menu.";
			SetText("Your beatmap was successfully downloaded.");
		} catch (Exception e) {
			e.printStackTrace();
			title = "Error: " + e.toString();
			GL.Log("(browser) Download failed");
			GL.Log("(browser) " + e.toString());
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
			try {
				if (fc != null)
					fc.close();
			} catch (Exception e) {
				GL.Log("(browser) Could not close file: " + e.toString());
			}
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
			try {
				if (hc != null)
					hc.close();
			} catch (Exception e) {
			}
		}
	}

}
