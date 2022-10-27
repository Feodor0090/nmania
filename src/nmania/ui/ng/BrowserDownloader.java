package nmania.ui.ng;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.*;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

import nmania.GL;

public class BrowserDownloader extends Alert implements Runnable {

	private String fileName;
	private String url;
	Thread t;

	public BrowserDownloader(String title, String url, String fileName) {
		super("Connecting...", "Downloading beatmap " + title + ". Closing this screen will cancel the action.");
		this.url = url;
		this.fileName = fileName;
	}

	public void run() {
		download();
	}

	public void OnEnter(IDisplay d) {
		t = new Thread(this, "BM downloader");
		t.start();
	}

	public boolean OnExit(IDisplay d) {
		if (t != null)
			t.interrupt();
		return super.OnExit(d);
	}

	public void download() {
		FileConnection fc = null;
		OutputStream out = null;
		HttpConnection hc = null;
		InputStream in = null;
		try {
			fc = (FileConnection) Connector.open(fileName, Connector.READ_WRITE);

			if (fc.exists()) {
				fc.close();
				return;
			}
			fc.create();
			hc = (HttpConnection) Connector.open(url);
			int r;
			try {
				r = hc.getResponseCode();
			} catch (IOException e) {
				try {
					hc.close();
				} catch (Exception e2) {
				}
				Thread.sleep(2000);
				hc = (HttpConnection) Connector.open(url);
				hc.setRequestMethod("GET");
				r = hc.getResponseCode();
			}
			while (r == 301 || r == 302) {
				String redir = hc.getHeaderField("Location");
				if (redir.startsWith("/")) {
					String tmp = url.substring(url.indexOf("//") + 2);
					String host = url.substring(0, url.indexOf("//")) + "//" + tmp.substring(0, tmp.indexOf("/"));
					redir = host + redir;
				}
				hc.close();
				hc = (HttpConnection) Connector.open(redir);
				hc.setRequestMethod("GET");
				r = hc.getResponseCode();
			}
			out = fc.openOutputStream();
			in = hc.openInputStream();
			int len = (int) hc.getLength();
			GL.Log("(browser) Downloading " + title);
			GL.Log("(browser) Target file: " + fileName);
			GL.Log("(browser) Expected size: " + (len >> 10) + "KB");
			title = "Downloading";
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
				Thread.sleep(1);
			}
			title = "Done! Close this menu.";
		} catch (Exception e) {
			e.printStackTrace();
			title = "Error: " + e.toString();
			GL.Log("(browser) Download failed");
			GL.Log("(browser) " + e.toString());
		} finally {
			t = null;
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
