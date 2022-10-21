package nmania.ui.ng;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nmania.BeatmapManager;
import nmania.BeatmapSet;
import nmania.ModsState;
import tube42.lib.imagelib.ColorUtils;

public class DifficultySelect extends ListScreen implements Runnable, IListSelectHandler {

	private String folder;
	private String title = "READING YOR CHART...";
	private BeatmapManager bm;
	BeatmapSet set;
	IDisplay d;
	int keysH;
	public ModsState mods = new ModsState();

	public DifficultySelect(BeatmapManager bm, String folder) {
		this.bm = bm;
		this.folder = folder;

	}

	public String GetTitle() {
		return title;
	}

	public boolean ShowLogo() {
		return true;
	}

	public String GetOption() {
		return "MODS";
	}

	public void Paint(Graphics g, int w, int h) {
		int bottomY = h - font.getHeight() * 2;
		int bottomH = h - bottomY + keysH;
		for (int i = 0; i <= bottomH; i++) {
			g.setColor(ColorUtils.blend(NmaniaDisplay.DARKER_COLOR, NmaniaDisplay.NMANIA_COLOR, (i * 255 / bottomH)));
			g.drawLine(0, bottomY + i, w, bottomY + i);
		}
		g.setFont(font);
		NmaniaDisplay.print(g, "Mode: NM AT Replay", 10, bottomY, -1, NmaniaDisplay.BG_COLOR, 0);
		NmaniaDisplay.print(g, "Mods: " + mods.toString(), w - 10, bottomY, -1, NmaniaDisplay.BG_COLOR,
				Graphics.TOP | Graphics.RIGHT);
		NmaniaDisplay.print(g, "Beatmap analysis disabled.", 10, bottomY + font.getHeight(), -1, NmaniaDisplay.BG_COLOR,
				0);
		super.Paint(g, w, bottomY - 10);
	}

	public void OnOptionActivate(IDisplay d) {
		// TODO Auto-generated method stub

	}

	public void OnEnter(IDisplay d) {
		this.d = d;
		keysH = ((NmaniaDisplay) d).keysH;
		loadingState = true;
		(new Thread(this)).start();
	}

	public void run() {
		try {
			set = bm.FromBMSDirectory(folder + "/");
			if (set == null) {
				// TODO error
				return;
			}

			try {
				Image img = BeatmapManager.getImgFromFS(set.wdPath + set.folderName + set.image);
				d.SetBg(img);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			title = set.artist + " - " + set.title;

			Vector items = new Vector();
			for (int i = 0; i < set.files.length; i++) {
				String f = set.files[i];
				if (f.endsWith(".osu") || f.endsWith(".nmbm")) {
					items.addElement(new DifficultyItem(f, this));
				}
			}
			SetItems(items);
			loadingState = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class DifficultyItem extends ListItem {

		public String fileName;

		public DifficultyItem(String fileName, IListSelectHandler handler) {
			super(BeatmapSet.GetDifficultyNameFast(fileName), handler);
			this.fileName = fileName;
		}

	}

	public void OnSelect(ListItem item, ListScreen screen, IDisplay display) {
		// TODO Auto-generated method stub

	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		// TODO Auto-generated method stub

	}
}
