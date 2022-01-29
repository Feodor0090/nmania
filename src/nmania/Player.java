package nmania;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.MediaException;

import symnovel.SNUtils;
import tube42.lib.imagelib.ImageFxUtils;
import tube42.lib.imagelib.ImageFxUtils.PixelModifier;
import tube42.lib.imagelib.ImageUtils;

public class Player extends GameCanvas {

	protected Player(Beatmap map) throws IOException, MediaException {
		super(false);

		int scrW = getWidth();
		int scrH = getHeight();

		// step 1: loading background
		Image _bg = Image.createImage(map.ToGlobalPath(map.image));
		_bg = ImageUtils.resize(_bg, scrW, scrH, true, false);
		bg = ImageFxUtils.applyModifier(_bg, new PixelModifier() {
			public int apply(int p, int x, int y) {
				return p;
			}
		});

		// step 2: loading music
		track = new AudioController(map);

		// step 3: setup difficulty
		// TODO

		// step 4: setup configs
		columnsCount = map.columnsCount;
		columns = new int[columnsCount][];
		currentNote = new int[columnsCount];
		holdKeys = new boolean[columnsCount];
		keyMappings = Settings.keyLayout[columnsCount];

		// step 5: loading beatmap
		SNUtils.sort(map.notes);
		Vector[] _cols = new Vector[columnsCount];
		for (int i = 0; i < map.notes.length; i++) {
			int c = map.notes[i].column;
			_cols[c].addElement(new Integer(map.notes[i].time));
			_cols[c].addElement(new Integer(map.notes[i].duration));
		}
		for (int i = 0; i < columnsCount; i++) {
			columns[i] = new int[_cols[i].size()];
			for (int j = 0; j < _cols[i].size(); j++) {
				columns[i][j] = ((Integer) _cols[i].elementAt(j)).intValue();
			}
		}

		// step 6: cache background hot areas
		// TODO
	}

	final int columnsCount;
	final int[][] columns;
	final int[] currentNote;
	final boolean[] holdKeys;
	final int[] keyMappings;
	final int[] hitWindows;
	final AudioController track;
	final Image bg;

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
