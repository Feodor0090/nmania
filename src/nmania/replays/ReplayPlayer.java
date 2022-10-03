package nmania.replays;

import nmania.IInputOverrider;
import nmania.Player;

public class ReplayPlayer implements IInputOverrider {

	public ReplayPlayer(ReplayChunk replay) {
		this.replay = replay;
	}

	private ReplayChunk replay;
	private int nextFrame = 0;
	
	public void Reset() {
		nextFrame = 0;
		replay = replay.firstChunk;
	}

	public int UpdatePlayer(Player player, int time) {
		return time;
	}

	public String GetName() {
		return "REPLAY";
	}

}
