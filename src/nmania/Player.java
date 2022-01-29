package nmania;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.MediaException;

import tube42.lib.imagelib.ImageFxUtils;
import tube42.lib.imagelib.ImageFxUtils.PixelModifier;
import tube42.lib.imagelib.ImageUtils;

public class Player extends GameCanvas {

	protected Player(Beatmap map) throws IOException, MediaException {
		super(false);

		int scrW = getWidth();
		int scrH = getHeight();

		// step 1: loading background
		Image bg = Image.createImage(map.ToGlobalPath(map.image));
		bg = ImageUtils.resize(bg, scrW, scrH, true, false);
		bg = ImageFxUtils.applyModifier(bg, new PixelModifier() {
			public int apply(int p, int x, int y) {
				return p;
			}
		});

		// step 2: loading music
		track = new AudioController(map);

		// step 3: setup difficulty
		// TODO

		// step 4: setup configs
		// TODO

		// step 5: loading beatmap
		// TODO
	}

	final int columnsCount;
	final int[][] columns;
	final int[] currentNote;
	final boolean[] holdKeys;
	final int[] keyMappings;
	final int[] hitWindows;
	final AudioController track;

	int time;

	protected final void keyPressed(int k) {
		int column = -1;
		for (int i = 0; i < columnsCount; i++)
			if (keyMappings[i] == k)
				column = i;
		if (column == -1)
			return;

	}

	final void Update() {
		time = track.Now();
	}
}
