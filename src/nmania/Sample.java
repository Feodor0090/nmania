package nmania;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public final class Sample {

	public Sample(String file, String type) throws IOException, MediaException {
		if (file.startsWith("file://")) {
			player = Manager.createPlayer(file);
		} else {
			if (type == null) {
				String ext = file.substring(file.lastIndexOf('.') + 1, file.length()).toLowerCase();
				if ("mp3".equals(ext))
					ext = "mpeg";
				type = "audio/" + ext;
			}
			player = Manager.createPlayer(getClass().getResourceAsStream(file), type);
		}
		player.realize();
		player.prefetch();
	}

	private final Player player;

	public final void Play() {
		try {
			player.stop();
			player.setMediaTime(0);
			player.start();
		} catch (MediaException e) {
		}
	}

	public final int GetLength() {
		long dur = player.getDuration();
		if (dur == Player.TIME_UNKNOWN)
			return -1;
		return (int) (dur / 1000);
	}

	public final void Dispose() {
		player.deallocate();
		player.close();
	}
}
