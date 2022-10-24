package nmania.replays;

import java.util.Date;

import nmania.GL;
import nmania.IScoreData;
import nmania.ScoreController;

public final class ReplayRecorder implements IReplayProvider, IScoreData {

	private final ScoreController score;

	public ReplayRecorder(ScoreController score) {
		this.score = score;
		chunk = ReplayChunk.CreateEmpty();
	}

	private ReplayChunk chunk;
	private int nextFrame = 0;
	private int state = 0;

	public void Receive(int time, int key, boolean newstate) {
		int ns;
		if (newstate) {
			ns = state | (1 << key);
		} else {
			ns = state & (~(1 << key));
		}
		if (ns == state)
			return;
		state = ns;
		if (nextFrame >= ReplayChunk.FRAMES_IN_CHUNK) {
			nextFrame = 0;
			chunk = ReplayChunk.Chain(chunk);
			GL.Log("Replay chunk overflow, creating a new one.");
		}
		chunk.data[nextFrame << 1] = time;
		chunk.data[(nextFrame << 1) + 1] = state;
		chunk.framesCount++;
		nextFrame++;
	}

	public void Reset() {
		chunk = ReplayChunk.CreateEmpty();
		nextFrame = 0;
		state = 0;
	}

	public ReplayChunk GetReplay() {
		return chunk.firstChunk;
	}

	public String GetPlayerName() {
		return score.playerName;
	}

	public Date PlayedAt() {
		return score.PlayedAt();
	}
}
