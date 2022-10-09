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
import nmania.IScore;
import nmania.ScoreController;
import nmania.replays.IExtendedReplay;
import nmania.replays.ReplayChunk;

/**
 * Model of OSR file. Contains all header information and replay blob.
 * 
 * @author Shinovon
 * 
 */
public final class OsuReplay implements IExtendedReplay {

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

	public ReplayChunk GetReplay() {
		try {
			ReplayReaderStream out = new ReplayReaderStream();
			InputStream in = new ByteArrayInputStream(replayData);
			LZMADecoder decoder = new LZMADecoder();
			byte[] properties = new byte[5];
			if (in.read(properties) != 5) {
				throw new IOException("LZMA file has no header!");
			}
	
			if (!decoder.setDecoderProperties(properties)) {
				throw new IOException("Decoder properties cannot be set!");
			}
			long outSize = 0;
			for (int i = 0; i < 8; i++) {
				int v = in.read();
				if (v < 0)
					throw new IOException("Can't read stream size");
				outSize |= ((long) v) << (8 * i);
			}
	
			if (!decoder.code(in, out, outSize)) {
				throw new IOException("Decoding unsuccessful!");
			}
			return out.getChunk();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void write(OutputStream outputStream, ReplayChunk r) throws IOException {
		DataOutputStream dataOut = new DataOutputStream(outputStream);
		OsrWriter writer = new OsrWriter(dataOut);
		writer.writeByte(gameMode);
		writer.writeInt(gameVersion);
		writer.writeString(beatmapHash);
		writer.writeString(playerName);
		writer.writeString(replayHash);
		writer.writeShort(count300);
		writer.writeShort(count100);
		writer.writeShort(count50);
		writer.writeShort(countGekis);
		writer.writeShort(countKatus);
		writer.writeShort(countMisses);
		writer.writeInt(totalScore);
		writer.writeShort(maxCombo);
		writer.writeByte(perfectCombo ? 1 : 0);
		writer.writeInt(modsUsed);

		writer.writeString(lifeBarGraph);
		writer.writeLong(timestamp);
		ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream();
		LZMAEncoder encoder = new LZMAEncoder();
		encoder.setDictionarySize(4194304);
		encoder.setEndMarkerMode(true);
		encoder.setMatchFinder(LZMAEncoder.EMatchFinderTypeBT4);
		encoder.setNumFastBytes(0x20);
		
        encoder.writeCoderProperties(compressedBytes);
        compressedBytes.write(new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 });
		encoder.code(new ReplayWriterStream(r), compressedBytes, -1, -1, null);
		
		writer.writeInt(compressedBytes.size());
		dataOut.write(compressedBytes.toByteArray());
		writer.writeLong(onlineScoreID);
		dataOut.close();
		compressedBytes = null;
		writer = null;
		dataOut = null;
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
