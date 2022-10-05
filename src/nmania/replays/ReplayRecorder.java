package nmania.replays;

import java.util.Date;

import nmania.IScoreData;
import nmania.ScoreController;

public final class ReplayRecorder implements IRawReplay, IScoreData {

	private final ScoreController score;

	public ReplayRecorder(ScoreController score) {
		this.score = score;
		chunk = ReplayChunk.CreateEmpty();
	}

	private ReplayChunk chunk;
	private int nextFrame = 0;
	private int state = 0;

	public void Receive(int time, int key, boolean newstate) {
		if (newstate) {
			state = state | (1 << key);
		} else {
			state = state & (~(1 << key));
		}
		if (nextFrame >= ReplayChunk.FRAMES_IN_CHUNK) {
			nextFrame = 0;
			chunk = ReplayChunk.Chain(chunk);
		}
		chunk.data[nextFrame * 2] = time;
		chunk.data[nextFrame * 2 + 1] = state;
		chunk.framesCount++;
		nextFrame++;
	}

	public ReplayChunk DecodeData() {
		return chunk.firstChunk;
	}

	public String GetPlayerName() {
		return score.playerName;
	}

	public Date PlayedAt() {
		return score.PlayedAt();
	}
}
