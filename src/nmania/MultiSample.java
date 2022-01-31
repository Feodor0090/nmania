package nmania;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public final class MultiSample {

	public MultiSample(boolean local, String file, String type, int count) throws IOException, MediaException {
		poolSize = count;
		pool = new Player[count];
		for (int i = 0; i < count; i++) {
			Player player;
			if (local) {
				player = Manager.createPlayer(getClass().getResourceAsStream(file), type);
			} else {
				throw new IllegalArgumentException("Remote samples are not supported yet!");
			}
			player.realize();
			player.prefetch();
			pool[i] = player;
		}
	}

	private final Player[] pool;
	private final int poolSize;
	private int next;

	public final void Play() {
		try {
			pool[next].setMediaTime(0);
			pool[next].start();
		} catch (MediaException e) {
		}
		next++;
		if (next >= poolSize)
			next = 0;
	}

	public final void Dispose() {
		for (int i = 0; i < poolSize; i++) {
			pool[i].deallocate();
			pool[i].close();
			pool[i] = null;
		}
	}
}
