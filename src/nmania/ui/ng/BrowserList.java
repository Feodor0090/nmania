package nmania.ui.ng;

import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import nmania.GL;
import nmania.Nmania;

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
			String s = Nmania.encodeUrl(search);
			if (search.length() == 0)
				search = "BEATMAPS LISTING";
			String url = Nmania.encodeUrl(
					"https://kitsu.moe/api/search?query=" + s + "&mode=3&amount=100" + (notRanked ? "" : "&status=1"));
			String r = Nmania.getUtf("http://nnp.nnchan.ru/glype/browse.php?u=" + url);
			if (r.charAt(0) != '{' && r.charAt(0) != '[') {
				GL.Log("(browser) Non-json answer from server!");
				SetNoItems();
				if (r.startsWith("<doctype")
						&& (r.indexOf("502") != -1 || r.indexOf("500") != -1 || r.indexOf("400") != -1)) {
					search = "Proxy is in bad state, try again later";
					GL.Log("(browser) http error recieved");
				} else {
					search = "Unknown error. Try debug version.";
					search = "Unknown error. Check logs"; // ?dbg
					GL.Log(r);
				}
				loadingState = false;
				t = null;
				return;
			}
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
			SetNoItems();
			search = e.toString();
			GL.Log("(browser) API request failed with " + e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			search = "Not enough memory!";
		}
		loadingState = false;
		t = null;
	}

	private void SetNoItems() {
		SetItems(new ListItem[] { new ListItem("Failed to load.", this) });
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
