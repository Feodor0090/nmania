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
				"How to load nmania beatmaps", "Low FPS troubleshooting", "Lagspikes troubleshooting" }, null);
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
				f.append("nmania\nver. ");
				f.append(Nmania.version());
				f.append("\nOpen source piano-like rhythm game for J2ME, compatible with osu!mania beatmaps.");
				f.append("\nGitHub: ");
				f.append(new Link("https://github.com/Feodor0090/nmania"));
				f.append("\nTG chat: ");
				f.append(new Link("https://t.me/nnmidletschat"));
				f.append("\nCode & design by Feodor0090");
				f.append("\nLibraries used: ");
				f.append(new Link("https://github.com/tube42/imagelib"));
				f.append(", org.json.me");
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
				f.append(
						"If your phone does not meet minimal system requirements, just accept it and try to play as is.\n\n");
				f.append("Some advices:");
				f.append("\nMake sure nothing is downloading. Close browser, social clients, player, anything that can do something in background.");
				f.append("\nDo not use bluetooth keyboard and/or headset. Their support is quite expensive in CPU resources.");
				f.append("\nDisconnect all OTG/BT/etc. devices. Use native device's keyboard if possible. Disconnect from the internet. Disable cell network.");
				f.append("\nDisable debug tools, if you have them running");
				break;
			case 4:
				f.append(
						"If your phone does not meet minimal system requirements, just accept it and try to play as is.\n\n");
				f.append("For the first, disable hitsounds and feedback samples. See if this helps.\n\n");
				f.append(
						"The cause of lagspikes are short high-priority tasks, that your phone runs sometimes in the background while you are playing.");
				f.append("\nA list of common ones:");
				f.append("\nJava GC: try to free up more RAM for the game by closing apps, disabling UI keeping and HUD. This reduces allocated memory and makes GC to run less often.");
				f.append("\nMedia subloading: try to move beatmaps to a faster storage (internal memory is usually better than SD cards / USB flash disks). Disable hitsounds and samples. Try to compress music track.");
				f.append("\nKeyboard handler: try to adjust skin settings related to keys, switch to another skin type. If something is wrong there, it may make handling code running too long causing lagspikes.");
				f.append("\nBackground apps refreshes: close everything that you can. Disconnect from the internet.");
				f.append("\nSystem tasks: disable any system reaction for input (keyboard sounds, etc.). Disable cell network. Make sure nothing is downloading/etc.");
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
		f.append("If some are missed, check their location, try to rename, etc.");
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

		public void commandAction(Command c, Item i) {
			if (c == open) {
				Nmania.open(this.getText());
			}
		}

	}

}
