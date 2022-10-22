package nmania;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

/**
 * Class that's responsible for music playback.
 * 
 * @author Feodor0090
 *
 */
public final class AudioController {

	private int lastTime;

	public AudioController(Beatmap map) throws IOException, MediaException {
		String file = map.ToGlobalPath(map.audio);
		// this supposes that all "builtin" files are mp3
		player = file.startsWith("file://") ? Manager.createPlayer(file)
				: Manager.createPlayer(getClass().getResourceAsStream(file), "audio/mpeg");
		player.realize();
		player.prefetch();
		offset = Settings.gameplayOffset;
	}

	public AudioController(BeatmapSet set) throws IOException, MediaException {
		String file = set.ToGlobalPath(set.audio);
		// this supposes that all "builtin" files are mp3
		player = file.startsWith("file://") ? Manager.createPlayer(file)
				: Manager.createPlayer(getClass().getResourceAsStream(file), "audio/mpeg");
		player.realize();
		player.prefetch();
		offset = Settings.gameplayOffset;
	}

	private final Player player;
	private final int offset;

	public int Now() {
		long mt = player.getMediaTime();
		if (mt < 0)
			return lastTime;
		return lastTime = offset + (int) (mt / 1000);
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

	/**
	 * Disposes the player.
	 */
	public void Stop() {
		try {
			player.stop();
		} catch (MediaException e) {
		}
		player.deallocate();
		player.close();
	}

	/**
	 * Makes the track to play from the beginning.
	 */
	public void Reset() {
		try {
			player.setMediaTime(0);
		} catch (MediaException e) {
		}
	}

	/**
	 * Loops this track.
	 */
	public void Loop() {
		player.setLoopCount(-1);
	}

	public void SetTimingData(float[][] data) {
		if (data == null)
			return;
		if (data.length != 2)
			return;
		points = data[0];
		kiai = data[1];
	}

	float[] points;
	float[] kiai;

	/**
	 * Queries local timings data for kiai time.
	 * 
	 * @return False if it's not or not known.
	 */
	public boolean IsKiai() {
		if (kiai == null)
			return false;
		if (kiai.length < 2)
			return false;
		float now = Now();
		for (int i = 0; i < kiai.length; i++) {
			if (now > kiai[i]) {
				// previous point is active, flipping statement below.
				// odd - active
				// non-odd - inactive
				return i % 2 != 0;
			}
		}
		return false;
	}

	public float Get4BeatDelta() {
		if (points == null)
			return 0f;
		if (points.length == 0)
			return 0f;
		float now = Now();
		int i;
		for (i = 0; i < points.length; i += 2) {
			if (now > points[i])
				break;
		}
		i -= 2;
		if (i < 0)
			i = 0;
		float local = Math.abs(now - points[i]);
		float bl4 = points[i + 1] * 4f;
		float bp = local % bl4;
		return bp / bl4;
	}
}
