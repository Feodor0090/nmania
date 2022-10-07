package nmania.replays;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Runtime data of replay. Node of a linked list.
 * 
 * @author Feodor0090
 *
 */
public final class ReplayChunk implements IRawReplay {
	/**
	 * First node of this linked list. If this node is first, this must be a
	 * cyclical reference. Must not be null.
	 */
	public ReplayChunk firstChunk;
	/**
	 * Next node. May be null if there are no more nodes.
	 */
	public ReplayChunk nextChunk;
	/**
	 * Inlined data. Odd elements are timestamps, non-odd are input states
	 * (time-input-time-input...). Must always containt {@link #FRAMES_IN_CHUNK}
	 * frames (and *2 elements). Real count of frames is stored in
	 * {@link #framesCount}.
	 */
	public int[] data;
	/**
	 * Count of frames in this chunk.
	 */
	public int framesCount;
	public static final int FRAMES_IN_CHUNK = 512;

	public final static ReplayChunk CreateEmpty() {
		ReplayChunk c = new ReplayChunk();
		c.firstChunk = c;
		c.data = new int[FRAMES_IN_CHUNK * 2];
		return c;
	}

	public final static ReplayChunk Chain(ReplayChunk prev) {
		ReplayChunk c = new ReplayChunk();
		c.firstChunk = prev.firstChunk;
		prev.nextChunk = c;
		c.data = new int[FRAMES_IN_CHUNK * 2];
		return c;
	}

	public ReplayChunk DecodeData() {
		return firstChunk;
	}

	public final static byte[] Encode(ReplayChunk r) throws IOException {
		byte[] comma = ",".getBytes("UTF-8");
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		r = r.firstChunk;
		int nextFrame = 0;
		int lastTime = 0;
		boolean writeComma = false;
		while (true) {
			if (nextFrame >= r.framesCount) {
				if (r.nextChunk == null)
					break;
				r = r.nextChunk;
			}
			if (writeComma)
				buf.write(comma);
			int time = r.data[nextFrame * 2];
			int keys = r.data[nextFrame * 2 + 1];
			int delta = time - lastTime;
			lastTime = time;
			nextFrame++;
			String frame = delta + "|" + keys + "|0|0";
			buf.write(frame.getBytes("UTF-8"));
			writeComma = true;
		}
		byte[] arr = buf.toByteArray();
		buf.close();
		return arr;
	}
}
