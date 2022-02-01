package nmania;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

import tube42.lib.imagelib.ImageUtils;

public class BeatmapSetPage extends Form implements Runnable, ItemCommandListener {

	BeatmapManager bm;
	String dir;
	BeatmapSet set;

	public BeatmapSetPage(BeatmapManager bm, String dir) {
		super("Beatmapset page");
		this.bm = bm;
		this.dir = dir;
		append(new Gauge("Parsing beatmaps", false, -1, Gauge.CONTINUOUS_RUNNING));
		(new Thread(this)).start();
	}

	public void run() {
		try {
			set = bm.FromBMSDirectory(dir + "/");
			if (set == null) {
				deleteAll();
				append(new StringItem("Failed to read BMS",
						"There must be at least one valid osu! / nmania beatmap in the folder. "
								+ "Also, check folder name - it must not be too long or contain special characters."));
			}
			Image img = BeatmapManager.getImgFromFS(set.wdPath + set.folderName + set.image);
			img = ImageUtils.resize(img, 350, (int) (img.getHeight() / (img.getWidth() / 350f)), true, false);
			deleteAll();
			append(new ImageItem(set.artist + " - " + set.title + " (" + set.mapper + ")", img, Item.LAYOUT_CENTER,
					null));
			for (int i = 0; i < set.files.length; i++) {
				String f = set.files[i];
				if (f.endsWith(".osu") || f.endsWith(".nmbm")) {
					StringItem btn = new StringItem(null, f.substring(f.indexOf('[') + 1, f.indexOf(']')),
							StringItem.BUTTON);
					btn.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
					btn.setItemCommandListener(this);
					btn.addCommand(new Difficulty(f));
					append(btn);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			deleteAll();
			append(new StringItem("Failed to read BMS", e.toString()));
		}
	}

	public void commandAction(Command c, Item arg1) {
		if (c instanceof Difficulty) {
			(new PlayerLoader(set, ((Difficulty) c).fileName)).start();
		}
	}

	public class Difficulty extends Command {
		public Difficulty(String file) {
			super("Play", Command.ITEM, 1);
			fileName = file;
		}

		public final String fileName;

	}
}
