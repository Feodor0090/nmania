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

		// synchronizing player
		int finalnewtime = time;
		for (int column = 0; column < columnsCount; column++) {
			if (currentNote[column] >= columns[column].length)
				continue;
			final int start = columns[column][currentNote[column]];
			final int diff = time - start;
			int dur = columns[column][currentNote[column] + 1];
			if (dur == 0) {
				// COPY OF CODE BELOW!
				if (currentNote[column] + 2 > columns[column].length) {
					int next = columns[column][currentNote[column] + 2];
					if (next - start < 30)
						dur = (next - start) / 2;
					else
						dur = 20;
				}
			}

			if (diff >= 5 && !localKeys[column]) {
				int newtime = time - diff + 1;
				if (newtime < finalnewtime)
					finalnewtime = newtime;
			}
			if (localKeys[column] && diff - dur >= 5) {
				int newtime = time - (diff - dur) + 1;
				if (newtime < finalnewtime)
					finalnewtime = newtime;
			}
		}
		time = finalnewtime;

		// checking all columns for incoming hits
		// this is intended to be a full copy of player's code
		for (int column = 0; column < columnsCount; column++) {

			if (currentNote[column] >= columns[column].length) {
				// checks for columns with no more notes
				continue; // this column is empty
			}

			// diff between current time and note hit time.
			// positive - it's late, negative - it's early.
			final int start = columns[column][currentNote[column]];
			final int diff = time - start;

			// hold length
			int dur = columns[column][currentNote[column] + 1];

			if (dur == 0) {
				// it's a hit. Lets hold a little.
				if (currentNote[column] + 2 > columns[column].length) {
					int next = columns[column][currentNote[column] + 2];
					if (next - start < 30)
						dur = (next - start) / 2;
					else
						dur = 20; // 1 frame (for 50 fps)
				}
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
						currentNote[column] += 2; // tracking next note
					}
				}
			}

		}

		return time;
	}

	public void Reset() {
		if (columns != null && columnsCount > 0) {
			currentNote = new int[columnsCount];
			localKeys = new boolean[columnsCount];
		}
	}

}
