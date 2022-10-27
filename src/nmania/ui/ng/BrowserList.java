package nmania.ui.ng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

public class BrowserList extends ListScreen implements Runnable, IListSelectHandler {

	private String search;
	private boolean notRanked;
	private JSONArray arr;
	Thread t;

	public BrowserList(String search, boolean notRanked) {
		this.search = search;
		this.notRanked = notRanked;
	}

	public String GetTitle() {
		return search;
	}

	public boolean ShowLogo() {
		return false;
	}

	public String GetOption() {
		return null;
	}

	public void OnOptionActivate(IDisplay d) {
	}

	public void OnEnter(IDisplay d) {
		loadingState = true;
		t = new Thread(this, "Browser IO");
		t.start();
	}

	public boolean OnExit(IDisplay d) {
		if (t != null)
			t.interrupt();
		return super.OnExit(d);
	}

	public void run() {
		try {
			String s = encodeUrl(search);
			String url = encodeUrl(
					"https://kitsu.moe/api/search?query=" + s + "&mode=3&amount=100" + (notRanked ? "" : "&status=1"));
			String r = getUtf("http://nnp.nnchan.ru/glype/browse.php?u=" + url);
			arr = new JSONArray(r);
			Vector items = new Vector();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				items.addElement(new ListItem(i,
						o.optString("Artist") + " - " + o.optString("Title") + " (" + o.optString("Creator") + ")",
						this));
			}
			SetItems(items);
		} catch (Exception e) {
			arr = null;
			SetItems(new ListItem[] { new ListItem("Failed to load.", this) });
			search = e.toString();
			e.printStackTrace();
		}
		loadingState = false;
		t = null;
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

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		try {
			display.Push(new BrowserView(arr.getJSONObject(item.UUID)));
		} catch (Exception e) {
		}
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
	}

}
