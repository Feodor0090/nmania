package nmania.replays;

/**
 * Object of replay, not yet ready to be played. This is used only for reading/playing.
 * 
 * @author Feodor0090, Shinovon
 *
 */
public interface IRawReplay {
	public ReplayChunk DecodeData();
}
