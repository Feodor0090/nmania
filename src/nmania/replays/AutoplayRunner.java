package nmania.replays;

import java.util.Calendar;
import java.util.Date;

import nmania.IInputOverrider;
import nmania.Player;

public class AutoplayRunner implements IInputOverrider {

	private int[][] columns;
	private int columnsCount;
	private int[] currentNote;
	private boolean[] localKeys; // just a local cache
	int frame = 0;
	private Date date = Calendar.getInstance().getTime();

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
				if (currentNote[column] + 2 < columns[column].length) {
					int next = columns[column][currentNote[column] + 2];
					if (next - start < 30)
						dur = (next - start) / 2;
					else
						dur = 20;
				}
			}

			if (diff >= 5) {
				// player.log += "\nFrame "+frame+": Note from " + start + " to " + (start +
				// dur) + " is " + diff + "ms later, controlling player";
				if (!localKeys[column]) {
					int newtime = time - diff + 1;
					// player.log += "\nFrame "+frame+": Note at " + start + " with duration " + dur
					// + " will seek player to " + newtime;
					if (newtime < finalnewtime)
						finalnewtime = newtime;
				} else if (diff - dur > 1) {
					int newtime = time - (diff - dur) + 1;
					// player.log += "\nFrame "+frame+": Tail at " + start + "+" + dur + " (" +
					// (start + dur) + ") will seek player to " + newtime;
					if (newtime < finalnewtime)
						finalnewtime = newtime;
				}
			}
		}
		if (finalnewtime != time) {
			// player.log += "\nFrame "+frame+": Player seeked from " + time + " to " +
			// finalnewtime;
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
				if (currentNote[column] + 2 < columns[column].length) {
					int next = columns[column][currentNote[column] + 2];
					if (next - start < 30)
						dur = (next - start) / 2;
					else
						dur = 20; // 1 frame (for 50 fps)
				}
			}

			if (diff >= -1) {
				if (!localKeys[column]) {
					// player.log += "\nFrame "+frame+": Note at " + start + " is " + diff + "ms
					// off, pressing the button, player is at " + time;
					// key is not holded yet
					localKeys[column] = true; // caching hold
					player.ToggleColumnInputState(column, true); // forwarding
				} else {
					if (diff - dur >= -1) {
						// player.log += "\nFrame "+frame+": Note from " + start + " to " + (start +
						// dur) + " ended " + (diff - dur)
						// + "ms ago, releasing the button, player is at " + time;
						// it's time to release the key
						localKeys[column] = false; // caching
						player.ToggleColumnInputState(column, false); // forwarding
						currentNote[column] += 2; // tracking next note
					}
				}
			}

		}

		frame++;
		return time;
	}

	public void Reset() {
		frame = 0;
		if (columns != null && columnsCount > 0) {
			currentNote = new int[columnsCount];
			localKeys = new boolean[columnsCount];

		}
	}

	public String GetName() {
		return "AUTO";
	}

	public String GetPlayerName() {
		return "nmania";
	}

	public Date PlayedAt() {
		return date;
	}

}
