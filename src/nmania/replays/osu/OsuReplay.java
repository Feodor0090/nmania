package nmania.replays.osu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import lzma.LZMADecoder;
import lzma.LZMAEncoder;
import lzma.LzmaInputStream;
import lzma.LzmaOutputStream;
import nmania.IScore;
import nmania.ScoreController;
import nmania.replays.IExtendedReplay;
import nmania.replays.ReplayChunk;
import symnovel.SNUtils;

/**
 * Model of OSR file. Contains all header information and replay blob.
 * 
 * @author Shinovon
 * 
 */
public class OsuReplay implements IExtendedReplay {

	public int gameMode;
	public int gameVersion;
	public String beatmapHash;
	public String playerName;
	public String replayHash;
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
	public String lifeBarGraph;
	public long onlineScoreID;

	private byte[] replayData;
	private String replayReadyData = null;

	public void read(InputStream inputStream) throws IOException {
		DataInputStream in = new DataInputStream(inputStream);
		OsrReader r = new OsrReader(in);

		gameMode = r.nextByte();
		gameVersion = r.nextInt();
		beatmapHash = r.nextString();
		playerName = r.nextString();
		replayHash = r.nextString();
		count300 = r.nextShort();
		count100 = r.nextShort();
		count50 = r.nextShort();
		countGekis = r.nextShort();
		countKatus = r.nextShort();
		countMisses = r.nextShort();
		totalScore = r.nextInt();
		maxCombo = r.nextShort();
		perfectCombo = r.nextByte() == 1;
		modsUsed = r.nextInt();

		lifeBarGraph = r.nextString();
		timestamp = r.nextLong();
		int replayLength = r.nextInt();
		replayData = new byte[replayLength];
		in.readFully(replayData, 0, replayLength);
		onlineScoreID = r.nextLong();
		r.close();
	}

	public String decodeReplayData() throws IOException {
		if (replayReadyData != null)
			return replayReadyData;
		InputStream bytes = new ByteArrayInputStream(replayData);
		// Delete compressed data
		replayData = null;
		LzmaInputStream stream = new LzmaInputStream(bytes, new LZMADecoder());
		ByteArrayOutputStream uncompressed = new ByteArrayOutputStream();
		byte[] buf = new byte[512];
		while (true) {
			int read = stream.read(buf, 0, 512);
			if (read == -1)
				break;
			uncompressed.write(buf, 0, read);
		}
		stream.close();
		stream = null;
		bytes.close();
		bytes = null;
		replayReadyData = new String(uncompressed.toByteArray(), "UTF-8");
		uncompressed.close();
		return replayReadyData;
	}

	public ReplayChunk DecodeData() {
		String s;
		try {
			s = decodeReplayData();
			int si = 0;
			int ei = 0;
			int lastKeys = 0;
			int lastTime = 0;
			int nextFrame = 0;
			ReplayChunk chunk = ReplayChunk.CreateEmpty();
			boolean loop = true;
			while (loop) {
				ei = s.indexOf(',', si);
				if (ei == -1) {
					ei = s.length();
					loop = false;
				}
				String frame = s.substring(si, ei);
				si = ei + 1;

				String[] data = SNUtils.split(frame, '|', 4);
				int delta = Integer.parseInt(data[0]);
				if (delta == -12345)
					continue;
				lastTime += delta;
				int keys = Integer.parseInt(data[1]);
				if (keys == lastKeys)
					continue;
				lastKeys = keys;

				if (nextFrame >= ReplayChunk.FRAMES_IN_CHUNK) {
					nextFrame = 0;
					chunk = ReplayChunk.Chain(chunk);
				}
				chunk.data[nextFrame * 2] = lastTime;
				chunk.data[nextFrame * 2 + 1] = keys;
				chunk.framesCount++;
				nextFrame++;
			}
			return chunk.firstChunk;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void write(OutputStream outputStream, InputStream replayDataStream) throws IOException {
		DataOutputStream dataOut = new DataOutputStream(outputStream);
		OsrWriter w = new OsrWriter(dataOut);

		w.writeByte(gameMode);
		w.writeInt(gameVersion);
		w.writeString(beatmapHash);
		w.writeString(playerName);
		w.writeString(replayHash);
		w.writeShort(count300);
		w.writeShort(count100);
		w.writeShort(count50);
		w.writeShort(countGekis);
		w.writeShort(countKatus);
		w.writeShort(countMisses);
		w.writeInt(totalScore);
		w.writeShort(maxCombo);
		w.writeByte(perfectCombo ? 1 : 0);
		w.writeInt(modsUsed);

		w.writeString(lifeBarGraph);
		w.writeLong(timestamp);

		ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream();
		LzmaOutputStream lzmaStream = new LzmaOutputStream(compressedBytes, new LZMAEncoder());
		byte[] buf = new byte[256];
		int i;
		while ((i = replayDataStream.read(buf)) != -1) {
			lzmaStream.write(buf, 0, i);
		}
		lzmaStream.close();
		w.writeInt(compressedBytes.size());
		dataOut.write(compressedBytes.toByteArray());
		compressedBytes.close();
		w.writeLong(onlineScoreID);
		dataOut.close();
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
		if (gameMode == 0)
			return "OSU";
		if (gameMode == 1)
			return "TAIKO";
		if (gameMode == 3)
			return "VSRG";
		return "invalid";
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

}
