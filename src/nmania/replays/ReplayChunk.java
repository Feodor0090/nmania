package nmania.replays;

/**
 * Runtime data of replay. Node of a linked list.
 * 
 * @author Feodor0090
 *
 */
public final class ReplayChunk implements IReplayProvider {

	/**
	 * Allocates new chunk. Must not be called directly.
	 */
	private ReplayChunk() {
		data = new int[FRAMES_IN_CHUNK * 2];
	}

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
	 * (time-input-time-input...). Must always contain {@link #FRAMES_IN_CHUNK}
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
		return c;
	}

	public final static ReplayChunk Chain(ReplayChunk prev) {
		ReplayChunk c = new ReplayChunk();
		c.firstChunk = prev.firstChunk;
		prev.nextChunk = c;
		return c;
	}

	public ReplayChunk GetReplay() {
		return firstChunk;
	}

	public static int CountTotalFrames(ReplayChunk c) {
		c = c.firstChunk;
		int l = 0;
		while (c != null) {
			l += c.framesCount;
			c = c.nextChunk;
		}
		return l;
	}
}
