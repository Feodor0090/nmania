package nmania.replays.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.json.me.JSONObject;

import nmania.IScore;
import nmania.ModsState;
import nmania.ScoreController;
import nmania.replays.IExtendedReplay;
import nmania.replays.ReplayChunk;
import nmania.replays.ReplayWriterStream;

public class NmaniaReplay implements IExtendedReplay {

	public String beatmapHash;
	public String playerName;
	public int count300;
	public int count100;
	public int count50;
	public int countGekis;
	public int countKatus;
	public int countMisses;
	public int totalScore;
	public int maxCombo;
	public boolean perfectCombo;
	public int modsUsed;
	public long timestamp;
	public String replay;

	public JSONObject EncodeScoreData() {
		JSONObject o = new JSONObject();
		o.put("miss", countMisses);
		o.put("meh", count50);
		o.put("ok", count100);
		o.put("good", countKatus);
		o.put("great", count300);
		o.put("perfect", countGekis);
		o.put("score", totalScore);
		o.put("combo", maxCombo);
		o.put("fc", perfectCombo);
		return o;
	}

	public void SetReplay(ReplayChunk r) {
		r = r.firstChunk;
		StringBuffer sb = new StringBuffer();
		ReplayWriterStream rws = new ReplayWriterStream(r);
		try {
			while (true) {
				int b = rws.read();
				if (b == -1)
					break;
				sb.append((char) b);
			}
		} catch (Exception e) {
		}
		replay = sb.toString();
	}

	public JSONObject Encode() {
		JSONObject o = new JSONObject();
		o.put("version", 1);
		o.put("score", EncodeScoreData());
		o.put("name", playerName);
		o.put("hash", beatmapHash);
		o.put("time", timestamp);
		o.put("mods", modsUsed);
		o.put("replay", replay);
		return o;
	}

	public void Write(OutputStream outputStream, ReplayChunk r) throws IOException {
		SetReplay(r);
		byte[] data = Encode().toString().getBytes("UTF-8");
		outputStream.write(data);
	}

	public void WriteScoreDataFrom(IScore score) {
		countMisses = score.GetMisses();
		count50 = score.GetMehs();
		count100 = score.GetOks();
		countKatus = score.GetGoods();
		count300 = score.GetGreats();
		countGekis = score.GetPerfects();
		maxCombo = score.GetCombo();
		perfectCombo = score.IsFC();
		totalScore = (int) score.GetScore();
		timestamp = score.PlayedAt().getTime();
		playerName = score.GetPlayerName();
	}

	public String GetMode() {
		return "VSRG";
	}

	public int GetPerfects() {
		return countGekis;
	}

	public int GetGreats() {
		return count300;
	}

	public int GetGoods() {
		return countKatus;
	}

	public int GetOks() {
		return count100;
	}

	public int GetMehs() {
		return count50;
	}

	public int GetMisses() {
		return countMisses;
	}

	public int GetTicks() {
		return 0;
	}

	public int GetAccuracy() {
		int totalHits = GetMisses() + GetMehs() + GetOks() + GetGoods() + GetGreats() + GetPerfects();
		long maxScore = totalHits * ScoreController.scores[5];
		long ourScore = GetMehs() * ScoreController.scores[1] + GetOks() * ScoreController.scores[2]
				+ GetGoods() * ScoreController.scores[3] + GetGreats() * ScoreController.scores[4]
				+ GetPerfects() * ScoreController.scores[5];
		return (int) ((ourScore * 10000L) / maxScore);
	}

	public long GetScore() {
		return totalScore;
	}

	public int GetCombo() {
		return maxCombo;
	}

	public boolean IsFC() {
		return perfectCombo;
	}

	public Date PlayedAt() {
		return new Date(timestamp);
	}

	public String GetPlayerName() {
		return playerName;
	}

	public ModsState GetMods() {
		return new ModsState(modsUsed);
	}

	public void SetMods(ModsState mods) {
		modsUsed = mods.GetMask();
	}
}
