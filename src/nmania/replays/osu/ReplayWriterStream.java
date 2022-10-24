package nmania.replays.osu;

import java.io.IOException;
import java.io.InputStream;

import nmania.replays.ReplayChunk;

/**
 * 
 * @author Shinovon
 *
 */
class ReplayWriterStream extends InputStream {

	ReplayChunk r;

	int nextFrame = 0;
	int lastTime = 0;
	byte[] chunkEnd = { '|', '0', '|', '0' };
	boolean writeComma = false;

	byte[] chunk = new byte[96];

	int pos = 0;
	int len = 0;

	ReplayWriterStream(ReplayChunk r) {
		this.r = r.firstChunk;
	}

	public int read() throws IOException {
		if (pos >= len) {
			if (!nextChunk())
				return -1;
			pos = 0;
		}
		return (int) (chunk[pos++] & 0xFF);
	}

	private boolean nextChunk() {
		if (nextFrame >= r.framesCount) {
			if (r.nextChunk == null)
				return false;
			r = r.nextChunk;
			nextFrame = 0;
		}
		int _pos = 0;
		if (writeComma)
			chunk[_pos++] = ',';
		else
			writeComma = true;
		int time = r.data[nextFrame * 2];
		int keys = r.data[nextFrame * 2 + 1];
		int delta = time - lastTime;
		lastTime = time;
		nextFrame++;
		byte[] d = Integer.toString(delta).getBytes();
		System.arraycopy(d, 0, chunk, _pos, d.length);
		_pos += d.length;
		chunk[_pos++] = '|';
		byte[] k = Integer.toString(keys).getBytes();
		System.arraycopy(k, 0, chunk, _pos, k.length);
		_pos += k.length;
		System.arraycopy(chunkEnd, 0, chunk, _pos, 4);
		len = _pos + 4;
		return true;
	}
};