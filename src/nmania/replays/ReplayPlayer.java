package nmania.replays;

import nmania.IInputOverrider;
import nmania.IScoreData;
import nmania.Player;

public class ReplayPlayer implements IInputOverrider, IRawReplay {

	public ReplayPlayer(ReplayChunk replay, IScoreData name) {
		this.replay = replay;
		playerName = name.GetPlayerName();
	}

	private String playerName;
	private ReplayChunk replay;
	private int nextFrame = 0;
	private int state = 0;

	public void Reset() {
		nextFrame = 0;
		replay = replay.firstChunk;
		state = 0;
	}

	public int UpdatePlayer(Player player, int time) {
		if (replay == null) {
			// replay ended
			return time;
		}

		int frameTime = replay.data[nextFrame * 2];
		if (time < frameTime)
			return time;

		int nextState = replay.data[nextFrame * 2 + 1];

		for (int i = 0; i < 10; i++) {
			int prev = (state >> i) & 1;
			int next = (nextState >> i) & 1;
			if (next != prev)
				player.ToggleColumnInputState(i, next == 1);
		}

		state = nextState;
		nextFrame++;
		if (nextFrame == ReplayChunk.FRAMES_IN_CHUNK) {
			nextFrame = 0;
			replay = replay.nextChunk;
		}
		return frameTime;
	}

	public String GetName() {
		return "REPLAY";
	}

	public String GetPlayerName() {
		return playerName;
	}

	public ReplayChunk DecodeData() {
		return replay.firstChunk;
	}

}
