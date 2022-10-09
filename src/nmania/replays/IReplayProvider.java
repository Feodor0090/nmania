package nmania.replays;

/**
 * Object of replay, encapsulated/encoded somehow, which allows to get underlying chunks.
 * 
 * @author Feodor0090, Shinovon
 *
 */
public interface IReplayProvider {
	/**
	 * Gets data of this replay as chunks.
	 * 
	 * @return Chunks.
	 */
	public ReplayChunk GetReplay();
}
