package nmania.replays;

import nmania.IInputOverrider;
import nmania.Player;

public class AutoplayRunner implements IInputOverrider {

	private int[][] columns;
	private int columnsCount;
	private int[] currentNote;
	private boolean[] localKeys; // just a local cache

	public int UpdatePlayer(Player player, int time) {
		if (columns == null) {
			// initializing player
			columns = player.GetMap();
			columnsCount = columns.length;
			currentNote = new int[columnsCount];
			localKeys = new boolean[columnsCount];
		}

		// checking all columns for incoming hits
		// this is intended to be a full copy of player's code
		for (int column = 0; column < columnsCount; column++) {

			if (currentNote[column] >= columns[column].length) {
				// checks for columns with no more notes
				continue; // this column is empty
			}

			// diff between current time and note hit time.
			// positive - it's late, negative - it's early.
			final int diff = time - columns[column][currentNote[column]];

			// hold length
			int dur = columns[column][currentNote[column] + 1];

			if (dur == 0) {
				// it's a hit. Lets hold a little.
				dur = 20; // 1 frame (for 50 fps)
			}

			if (diff >= 0) {
				if (!localKeys[column]) {
					// key is not holded yet
					localKeys[column] = true; // caching hold
					player.ToggleColumnInputState(column, true); // forwarding
				} else {
					if (diff - dur >= 0) {
						// it's time to release the key
						localKeys[column] = false; // caching
						player.ToggleColumnInputState(column, false); // forwarding
					}
				}
			}

		}

		// we don't aim perfect playback, let's let it play as it wants
		return time;
	}

}
