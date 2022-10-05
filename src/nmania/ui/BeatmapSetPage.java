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
import nmania.IInputOverrider;
import nmania.Nmania;
import nmania.PlayerBootstrapData;
import nmania.PlayerLoader;
import nmania.replays.AutoplayRunner;
import tube42.lib.imagelib.ImageUtils;

public class BeatmapSetPage extends Form implements Runnable, ItemCommandListener, CommandListener {

	String[] text = Nmania.getStrings("bms_page");
	BeatmapManager bm;
	String dir;
	BeatmapSet set;
	ChoiceGroup daMod = new ChoiceGroup(text[9], Choice.POPUP, new String[] { text[1], "Hard Rock", "Easy" }, null);
	ChoiceGroup mode = new ChoiceGroup(text[0], Choice.POPUP,
			new String[] { text[1], "Sudden Death", "No Fail", text[2], "Watch replay" }, null);
	ChoiceGroup record = new ChoiceGroup("Replay recording", Choice.MULTIPLE, new String[] { "Enable" }, null);

	private BeatmapSetsList list;
	private Command back = new Command(text[3], Command.BACK, 1);

	public BeatmapSetPage(BeatmapManager bm, String dir, BeatmapSetsList list) {
		super("Beatmapset page");
		this.bm = bm;
		this.dir = dir;
		this.list = list;
		this.setCommandListener(this);
		append(new Gauge(text[4], false, -1, Gauge.CONTINUOUS_RUNNING));
		(new Thread(this)).start();
	}

	public void run() {
		try {
			set = bm.FromBMSDirectory(dir + "/");
			if (set == null) {
				deleteAll();
				addCommand(back);
				append(new StringItem(text[5], text[6]));
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
			append(new ImageItem(img == null ? text[7] : null, img, Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_AFTER,
					null));
			append(set.artist + " - " + set.title + " (" + set.mapper + ")");
			record.setSelectedIndex(0, false);
			append(record);
			for (int i = 0; i < set.files.length; i++) {
				String f = set.files[i];
				if (f.endsWith(".osu") || f.endsWith(".nmbm")) {
					StringItem btn = new StringItem(null, f.substring(f.indexOf('[') + 1, f.lastIndexOf(']')),
							Item.BUTTON);
					btn.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
					btn.setItemCommandListener(this);
					btn.setDefaultCommand(new Difficulty(f));
					append(btn);
				}
			}
			mode.setSelectedIndex(0, true);
			append(mode);
			daMod.setSelectedIndex(0, true);
			append(daMod);
			addCommand(back);
		} catch (Exception e) {
			e.printStackTrace();
			deleteAll();
			addCommand(back);
			append(new StringItem(text[8], e.toString()));
		}
	}

	public PlayerBootstrapData FromChoices() {
		PlayerBootstrapData opts = new PlayerBootstrapData();
		boolean[] rr = new boolean[1];
		record.getSelectedFlags(rr);
		opts.recordReplay = rr[0];
		opts.set = set;
		switch (mode.getSelectedIndex()) {
		case 1:
			opts.failMod = 1;
			break;
		case 2:
			opts.failMod = -1;
			break;
		}
		switch (daMod.getSelectedIndex()) {
		case 1:
			opts.daMod = 1;
			break;
		case 2:
			opts.daMod = -1;
			break;
		}
		return opts;
	}

	public void commandAction(Command c, Item arg1) {
		if (c instanceof Difficulty) {
			PlayerBootstrapData opts = FromChoices();
			opts.mapFileName = ((Difficulty) c).fileName;
			if (mode.getSelectedIndex() == 4) {
				Nmania.Push(new ReplaySelector(opts, this));
				return;
			}
			IInputOverrider input = mode.getSelectedIndex() == 3 ? new AutoplayRunner() : null;
			(new PlayerLoader(opts, input, this)).start();
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
