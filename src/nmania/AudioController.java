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
		this(map.ToGlobalPath(map.audio));
	}

	public AudioController(BeatmapSet set) throws IOException, MediaException {
		this(set.ToGlobalPath(set.audio));
	}

	public AudioController(String file) throws MediaException, IOException {
		Player p = TryInit(file, null);
		if (p == null)
			p = TryInit(file, "mp3");
		if (p == null)
			p = TryInit(file, "aac");
		if (p == null)
			p = TryInit(file, "amr");
		if (p == null)
			p = TryInit(file, "wav");
		if (p == null)
			throw new IOException("Could not load any files on this MRL");
		player = p;
		offset = Settings.gameplayOffset;
	}

	private final Player TryInit(String mrl, String ext) throws MediaException {
		if (ext != null) {
			mrl = mrl.substring(0, mrl.lastIndexOf('.') + 1);
			mrl += ext;
			GL.Log("(misc) Falling to " + mrl);
		}
		try {
			Player p;
			p = mrl.startsWith("file://") ? Manager.createPlayer(mrl)
					: Manager.createPlayer(getClass().getResourceAsStream(mrl), "audio/mpeg");
			p.realize();
			p.prefetch();
			return p;
		} catch (MediaException e) {
			if (e.toString().indexOf("not") != -1 && e.toString().indexOf("allowed") != -1)
				throw e;
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private final Player player;
	private final int offset;
	private boolean alive = true;

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
		alive = false;
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

	public boolean IsAlive() {
		return alive;
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
