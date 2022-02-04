package nmania;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public final class AudioController {

	public AudioController(Beatmap map) throws IOException, MediaException {
		String file = map.ToGlobalPath(map.audio);
		player = file.startsWith("file://") ? Manager.createPlayer(file)
				: Manager.createPlayer(getClass().getResourceAsStream(file), "audio/mpeg");
		player.realize();
		player.prefetch();
		offset = Settings.gameplayOffset;
	}

	private final Player player;
	private final int offset;

	public int Now() {
		return offset+(int) (player.getMediaTime() / 1000);
	}

	public boolean Play() {
		try {
			player.start();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean Pause() {
		try {
			player.stop();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void Stop() {
		try {
			player.stop();
		} catch (MediaException e) {
		}
		player.deallocate();
		player.close();
	}

	public void Reset() {
		try {
			player.setMediaTime(0);
		} catch (MediaException e) {
		}
	}
}
