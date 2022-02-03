package nmania;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public final class MultiSample {

	public MultiSample(boolean local, String file, String type, int count) throws IOException, MediaException {
		poolSize = count;
		pool = new Player[count];
		streams = new ByteArrayInputStream[count];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InputStream data;
		if (local) {
			data = getClass().getResourceAsStream(file);
		} else {
			throw new IllegalArgumentException("Remote samples are not supported yet!");
		}
		byte[] buf = new byte[2048];
		int r;
		while ((r = data.read(buf)) != -1) {
			os.write(buf, 0, r);
		}
		raw = os.toByteArray();
		for (int i = 0; i < count; i++) {
			streams[i] = new ByteArrayInputStream(raw);
			Player player;
			player = Manager.createPlayer(streams[i], type);
			player.realize();
			player.prefetch();
			pool[i] = player;
		}
	}

	private byte[] raw;
	private final ByteArrayInputStream[] streams;
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
			try {
				streams[i].close();
			} catch (IOException e) {
			}
			streams[i] = null;
		}
		raw = null;
	}
}
