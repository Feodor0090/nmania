package nmania.ui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;

import nmania.Nmania;

public class InfoScreen extends List implements CommandListener {

	private Command toMenu = new Command("Back", Command.BACK, 1);
	private Command toList = new Command("Back", Command.BACK, 1);

	public InfoScreen() {
		super("Information", Choice.IMPLICIT, new String[] { "About this app", "How to load osu!mania beatmaps",
				"How to load nmania beatmaps", "Skinning", "Low FPS & lags troubleshooting" }, null);
		this.addCommand(toMenu);
		this.setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == toList) {
			Nmania.Push(this);
		} else if (c == toMenu) {
			Nmania.Push(new MainScreen());
		} else if (c == List.SELECT_COMMAND) {
			Form f = new Form(getString(getSelectedIndex()));
			f.setCommandListener(this);
			f.addCommand(toList);
			switch (getSelectedIndex()) {
			case 0:
				f.append(new StringItem("nmania v" + Nmania.version(),
						"Open source piano-like rhythm game for J2ME, compatible with osu!mania beatmaps."));
				f.append(new Link("GitHub", "https://github.com/Feodor0090/nmania"));
				f.append(new Link("TG chat", "https://t.me/nnmidletschat"));
				f.append(new StringItem("Code & design", "Feodor0090"));
				f.append("\n\nLibraries used: org.json.me, ");
				f.append(new Link("https://github.com/tube42/imagelib"));
				break;
			case 1:
				f.append("1. Download beatmaps that you want from ");
				f.append(new Link("https://osu.ppy.sh/beatmapsets"));
				f.append(", any mirror, copy from your osu!stable \"Songs\" folder or export from lazer.");
				f.append(
						"\n2. Make sure it is osu!mania beatmap, not osu! or for any other ruleset - we don't have a converter.");
				f.append("\n3. If you use another device, you can delete video and storyboard files, ");
				f.append(
						"compress backgrounds and music to make a beatmapset lighter, but usually it's not necessary.");
				f.append("\n4. Transfer files to this device.");
				AppendWD(f, 5);
				f.append("\n6. Using any file manager (for ex., x-plore), extract your oszs/zips to this folder.");
				AppendHowToPlayFooter(f, 7, "osu");
				break;
			case 2:
				f.append("1. Download the archive with your beatmapset.");
				AppendWD(f, 2);
				f.append(" Using any file manager, extract the archive here.");
				f.append(
						"\n3. If you have a standalone beatmap (only single \"nmbm\" file), create one subfolder in working folder. ");
				f.append(
						"Place your beatmap there. Find a music track for your beatmapset somewhere and place it there too.");
				AppendHowToPlayFooter(f, 4, "nmbm");
				break;
			case 3:
				f.append(new StringItem("Vector skin",
						"Skin, where you can define set of numeric parameters and colors. "
								+ "Nmania will draw your game using LCDUI graphic primitives (lines, rects, etc.) keeping in mind "
								+ "your sizes/colors. This skin type is recommended for middle-tier devices."));
				f.append(new StringItem("Rich skin",
						"Skin, where you can provide a set of images. Nmania will draw them on place "
								+ "of gameplay elements. This lets you customize the look of the game more, but spends more system resources. "
								+ "Can be used on high-end devices."));
				f.append(new StringItem("Column width, left offset",
						"Defines, how wide in pixels is each column and space between stage and left side of the screen."));
				f.append(new StringItem("Keyboard height",
						"Defines height of synthesizer's keys in the bottom of the screen in pixels. Width is equal to column's width."));
				f.append(new StringItem("Hold trail width",
						"Defines width of hold's body in pixels. This value should be less then column's width, otherwise holds will cause graphical artefacts."));
				f.append(new StringItem("Note height",
						"Defines note's height. Note's width is equal to column's width."));
				f.append(new StringItem("Notes pallete",
						"Each pair of colors define notes' colors in different columns. The first pair will be used in non-odd columns, the second in odd, the third in central column. "
								+ "If two colors in a pair are equal, notes will be solid. If colors are different, gradient will be drawn. See \"Use vertical gradient\" description for further information."));
				f.append(new StringItem("Holds pallete",
						"The 2nd, 4th and 6th colors will be used for hold bodies in non-odd, odd and central columns accordingly. "
								+ "If different palletes usage is enabled, this pallete also will be used to color hold's head notes (instead of generic notes pallete). Colors mapping and gradient type is equal to generic notes pallete."));
				f.append(new StringItem("Use vertical gradient",
						"If enabled, gradients will be vertical. The 1st color is used for bottom, the 2nd for top.\n"
								+ "If disabled, gradients will be horizontal. The 1st color is used for center, the 2nd for left/right sides."));
				f.append(new StringItem("Use different palletes for notes and hold heads",
						"See \"Holds pallete\" description."));
				f.append(new StringItem("Keys pallete",
						"Colors for synthesizer's keys. The 1st&2nd, 3rd&4th and 5th&6th colors will be used for keys in non-odd, odd and central columns accordingly. The 1st color in each pair is for key's top, the 2nd for bottom."));
				f.append(new StringItem("Hold keys pallete",
						"Colors for synthesizer's holded keys. See \"Keys pallete\" to learn about colors roles."));
				// f.append(new StringItem("", ""));
				break;
			case 4:
				f.append("If your phone doesn't meet system requirements, just accept it and try to play as is.");
				f.append("\n\nFor the first, disable hitsounds and feedback samples. See if this helps.\n\n");
				f.append("Some advices:\n");
				f.append("Make sure nothing is downloading.\n");
				f.append("Close browser, social clients, player, anything that can do something in background.");
				f.append("\nTry to disable menus keeping is settings.");
				f.append("\nUse vector skin. Modify color pallete to use solid fills, not gradients.");
				f.append(
						"\nDo not use bluetooth keyboard and/or headset. Their support is expensive in CPU resources.");
				f.append("\nDisconnect all OTG/BT/etc. devices. Use native device's keyboard if possible.");
				f.append("\nDisconnect from the internet. Disable cell network.");
				f.append("\nDisable any system reaction for input (keyboard sounds, etc.)");
				f.append("\nTry to uninstall apps that modify the system (custom bars, etc.)");
				f.append("\nDisable debug tools, if you have them running (PerfMon, emulator's utils, etc.).");
				break;
			}
			Nmania.Push(f);
		}
	}

	public static final void AppendHowToPlayFooter(Form f, int start, String ext) {
		f.append("\n" + start
				+ ". Storage structure is identical to osu!stable - the working folder must contain several folders, one child folder for each beatmapset.");
		f.append(" Nesting is not supported, \".../nmania/BMS1/map1.");
		f.append(ext);
		f.append("\" will be read, \".../nmania/BMS2/dir/map2.");
		f.append(ext);
		f.append("\" will be ignored.");
		f.append("\n" + (start + 1) + ". Open the game, go to \"play\" submenu. Here is list of your beatmapsets. ");
		f.append(" If some are missed, check their location, try to rename, etc.");
		f.append("\n" + (start + 2) + ". Open any to proceed to it's difficulties. Choose one to play it.");
		f.append("\nIf you have troubles, read everything in this manual again. ");
		f.append(
				"If you still have troubles, ask in our TG chat or open a GitHub discussion (links are in \"about\" section).");
	}

	public static final void AppendWD(Form f, int n) {
		f.append("\n" + n + ". Game's working folder is ");
		f.append(new Link("file:///C:/Data/Sounds/nmania/"));
		f.append(". If there is no one, create it on EXACT path.");
	}

	public static class Link extends StringItem implements ItemCommandListener {

		private static Command open = new Command("Go to", Command.ITEM, 1);

		public Link(String text) {
			super(null, text, StringItem.HYPERLINK);
			this.setItemCommandListener(this);
			this.addCommand(open);
		}

		public Link(String label, String text) {
			super(label, text, StringItem.HYPERLINK);
			this.setItemCommandListener(this);
			this.addCommand(open);
		}

		public void commandAction(Command c, Item i) {
			if (c == open) {
				Nmania.open(this.getText());
			}
		}

	}

}
