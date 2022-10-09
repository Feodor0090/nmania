package nmania.replays;

import nmania.IScore;

/**
 * Replay that can provide info about it.
 * 
 * @author Feodor0090
 */
public interface IExtendedReplay extends IReplayProvider, IScore {
	/**
	 * Gets mode of this replay.
	 * @return Always must be "VSRG".
	 */
	public String GetMode();
	
}
