package nmania.ui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import nmania.BeatmapManager;
import nmania.BeatmapSet;
import nmania.Nmania;
import nmania.PlayerLoader;
import tube42.lib.imagelib.ImageUtils;

public class BeatmapSetPage extends Form implements Runnable, ItemCommandListener, CommandListener {

	BeatmapManager bm;
	String dir;
	BeatmapSet set;
	ChoiceGroup mode = new ChoiceGroup("Mode", Choice.EXCLUSIVE, new String[] { "Normal play", "Automated demo" },
			null);

	private BeatmapSetsList list;
	private Command back = new Command("Back", Command.BACK, 1);

	public BeatmapSetPage(BeatmapManager bm, String dir, BeatmapSetsList list) {
		super("Beatmapset page");
		this.bm = bm;
		this.dir = dir;
		this.list = list;
		this.setCommandListener(this);
		append(new Gauge("Parsing beatmaps", false, -1, Gauge.CONTINUOUS_RUNNING));
		(new Thread(this)).start();
	}

	public void run() {
		try {
			set = bm.FromBMSDirectory(dir + "/");
			if (set == null) {
				deleteAll();
				addCommand(back);
				append(new StringItem("Failed to read BMS",
						"There must be at least one valid osu! / nmania beatmap in the folder. "
								+ "Also, check folder name - it must not be too long or contain special characters."));
				return;
			}
			Image img = null;
			try {
				img = BeatmapManager.getImgFromFS(set.wdPath + set.folderName + set.image);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (img != null)
				img = ImageUtils.resize(img, 350, (int) (img.getHeight() / (img.getWidth() / 350f)), true, false);
			deleteAll();
			append(new ImageItem(img == null ? "Failed to read beatmap's background." : null, img,
					Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_AFTER, null));
			append(set.artist + " - " + set.title + " (" + set.mapper + ")");
			for (int i = 0; i < set.files.length; i++) {
				String f = set.files[i];
				if (f.endsWith(".osu") || f.endsWith(".nmbm")) {
					StringItem btn = new StringItem(null, f.substring(f.indexOf('[') + 1, f.lastIndexOf(']')),
							StringItem.BUTTON);
					btn.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
					btn.setItemCommandListener(this);
					btn.setDefaultCommand(new Difficulty(f));
					append(btn);
				}
			}
			mode.setSelectedIndex(0, true);
			append(mode);
			addCommand(back);
		} catch (

		Exception e) {
			e.printStackTrace();
			deleteAll();
			addCommand(back);
			append(new StringItem("Failed to read BMS", e.toString()));
		}
	}

	public void commandAction(Command c, Item arg1) {
		if (c instanceof Difficulty) {
			(new PlayerLoader(set, ((Difficulty) c).fileName, mode.getSelectedIndex() == 1, this)).start();
		}
	}

	public class Difficulty extends Command {
		public Difficulty(String file) {
			super("Play", Command.ITEM, 1);
			fileName = file;
		}

		public final String fileName;

	}

	public void commandAction(Command c, Displayable arg1) {
		if (c == back)
			Nmania.Push(list);
	}
}
