package nmania.replays.osu;

import java.io.IOException;
import java.io.OutputStream;

import nmania.replays.ReplayChunk;
import symnovel.SNUtils;

/**
 * 
 * @author Shinovon
 *
 */
class ReplayReaderStream extends OutputStream {
		int lastKeys = 0;
		int lastTime = 0;
		int nextFrame = 0;
		ReplayChunk chunk = ReplayChunk.CreateEmpty();
		
		StringBuffer sb = new StringBuffer();
		
		private void addChunk() {
			if(sb.length() == 0) return;
			String frame = sb.toString();
			sb.setLength(0);
			String[] data = SNUtils.split(frame, '|', 4);
			int delta = Integer.parseInt(data[0]);
			if (delta == -12345) return;
			lastTime += delta;
			int keys = (int)Double.parseDouble(data[1]);
			if (keys == lastKeys) return;
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
		
		public void write(int b) throws IOException {
			if(b == ',') {
				addChunk();
				return;
			}
			sb.append((char) b);
		}
		
		public void close() {
			addChunk();
		}
		
		ReplayChunk getChunk() {
			return chunk;
		}
	};