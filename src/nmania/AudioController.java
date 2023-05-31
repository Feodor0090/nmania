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
public class AudioController {

	public AudioController(Beatmap map, boolean allowFallback) throws IOException {
		this(map.ToGlobalPath(map.audio), allowFallback);
	}

	public AudioController(BeatmapSet set, boolean allowFallback) throws IOException {
		this(set.ToGlobalPath(set.audio), allowFallback);
	}

	public AudioController(String file, boolean allowFallback) throws IOException {
		Player p = TryInit(file, null);
		if (p == null)
			p = TryInit(file, "mp3");
		if (p == null)
			p = TryInit(file, "aac");
		if (p == null)
			p = TryInit(file, "amr");
		if (p == null)
			p = TryInit(file, "wav");
		if (p == null && !allowFallback)
			throw new IOException("Could not load any files on this MRL");
		player = p;
		offset = Settings.gameplayOffset;
		try {
			String jh = System.getProperty("java.home"); // ?sid
			if (jh != null) { // ?sid
				GL.Log("(audio) Running desktop java, JH=" + jh); // ?sid
				// if condition below is hit, we are likely on GH runner.
				// It has no sound device, so using system clock.
				if (jh.indexOf("D:\\a\\nmania\\nmania") != -1) { // ?sid
					fallback = true; // ?sid
				} // ?sid
			} // ?sid
		} catch (RuntimeException e) {
		}
	}

	private final Player TryInit(String mrl, String ext) {
		if (ext != null) {
			if (mrl.endsWith(ext)) {
				// already looked for, skipping...
				return null;
			}
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
		} catch (RuntimeException e) {
		} catch (MediaException e) {
		} catch (IOException e) {
		}
		return null;
	}

	private final Player player;
	private final int offset;
	private boolean alive = true;
	private int lastTime;

	// fallback
	private boolean fallback = false;
	private long startTime = 0, pauseTime = -1;

	public int Now() {
		if (fallback)
			return (int) (System.currentTimeMillis() - startTime);
		long mt = player.getMediaTime();
		if (mt < 0)
			return lastTime;
		return lastTime = offset + (int) (mt / 1000);
	}

	public int Total() {
		return (int) (player.getDuration() / 1000);
	}

	/**
	 * Starts this player.
	 * 
	 * @return False if actual player failed and fallback was enabled.
	 */
	public boolean Play() {
		if (fallback) {
			if (pauseTime == -1) {
				// running from 0
				startTime = System.currentTimeMillis();
				GL.Log("(audio) Fallback clock start from 0.");
			} else {
				startTime += (System.currentTimeMillis() - pauseTime);
				pauseTime = -1;
				GL.Log("(audio) Fallback clock start from pause time.");
			}
			return false;
		}
		try {
			player.start();
			return true;
		} catch (Exception e) {
			GL.Log("(audio) Could not start player: " + e.toString());
			GL.Log("(audio) Falling back to system clock!");
			fallback = true;
			startTime = System.currentTimeMillis();
			return false;
		}
	}

	public boolean Pause() {
		if (fallback) {
			pauseTime = System.currentTimeMillis();
			GL.Log("(audio) Fallback clock was paused at " + (pauseTime - startTime) + "!");
			return true;
		}
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
		} catch (Exception e) {
			GL.Log("(audio) Could not close player: " + e.toString());
		}
		if (player != null) {
			player.deallocate();
			player.close();
		}
		alive = false;
	}

	/**
	 * Makes the track to play from the beginning.
	 */
	public void Reset() {
		if (fallback) {
			pauseTime = -1;
			startTime = System.currentTimeMillis();
			GL.Log("(audio) Fallback clock was reset.");
			return;
		}
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
