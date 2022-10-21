package nmania.ui.ng;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nmania.BeatmapManager;
import nmania.BeatmapSet;
import nmania.IInputOverrider;
import nmania.ModsState;
import nmania.PlayerBootstrapData;
import nmania.Settings;
import nmania.replays.AutoplayRunner;
import tube42.lib.imagelib.ColorUtils;

public class DifficultySelect extends ListScreen implements Runnable, IListSelectHandler {

	private String folder;
	private String title = "READING YOR CHART...";
	private BeatmapManager bm;
	BeatmapSet set;
	IDisplay d;
	int keysH;
	public ModsState mods = new ModsState();
	public int mode;
	public String[] modes = new String[] { "normal", "autoplay", "replay" };

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
		super.Paint(g, w, bottomY - 10);

		for (int i = 0; i <= bottomH; i++) {
			g.setColor(ColorUtils.blend(NmaniaDisplay.DARKER_COLOR, NmaniaDisplay.NMANIA_COLOR, (i * 255 / bottomH)));
			g.drawLine(0, bottomY + i, w, bottomY + i);
		}
		g.setFont(font);

		NmaniaDisplay.print(g, "Mode:", 10, bottomY, -1, NmaniaDisplay.BG_COLOR, 0);
		final int sp = 12;
		int x = font.stringWidth("Mode:") + 10;
		for (int i = 0; i < 3; i++) {
			int sw = font.stringWidth(modes[i]);
			if (i == mode) {
				int trs = bottomY + (font.getHeight() >> 1);
				int trb = bottomY + font.getHeight() - 4;
				g.setColor(NmaniaDisplay.PINK_COLOR);
				g.fillTriangle(x + 2, trs, x + 8, bottomY + 4, x + 8, trb);
				g.fillRect(x + sp - 2, bottomY + 2, sw + 4, font.getHeight() - 4);
				g.fillTriangle(x + sp + sw + 4, bottomY + 4, x + sp + sw + 4, trb, x + sp + sw + 10, trs);
			}
			NmaniaDisplay.print(g, modes[i], x + sp, bottomY, -1, NmaniaDisplay.BG_COLOR, 0);
			x += sp + sw;
		}
		NmaniaDisplay.print(g, "Mods: " + mods.toString(), w - 10, bottomY, -1, NmaniaDisplay.BG_COLOR,
				Graphics.TOP | Graphics.RIGHT);
		NmaniaDisplay.print(g, "Beatmap analysis disabled.", 10, bottomY + font.getHeight(), -1, NmaniaDisplay.BG_COLOR,
				0);
	}

	public void OnOptionActivate(IDisplay d) {
		d.Push(new ModsSelectScreen(mods));
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
			Thread.sleep(200);
			try {
				Image img = BeatmapManager.getImgFromFS(set.wdPath + set.folderName + set.image);
				d.SetBg(img);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
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
		PlayerBootstrapData opts = new PlayerBootstrapData();
		opts.recordReplay = Settings.recordReplay;
		opts.set = set;
		opts.mods = mods;
		opts.mapFileName = ((DifficultyItem) item).fileName;
		if (mode == 2) {
			display.Push(new ReplaySelect(opts));
			return;
		}
		IInputOverrider input = mode == 1 ? new AutoplayRunner() : null;
		display.Push(new PlayerLoaderScreen(input, opts));
	}

	public void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display) {
		mode += direction;
		if (mode < 0)
			mode = modes.length - 1;
		if (mode >= modes.length)
			mode = 0;
	}
}
