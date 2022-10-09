package nmania.replays.osu;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper for reading data from OSR file.
 * 
 * @author Shinovon
 * 
 */
class OsrReader {
	private InputStream in;

	public OsrReader(DataInputStream dataStream) {
		this.in = dataStream;
	}

	public byte nextByte() throws IOException {
		int read;
		if ((read = in.read()) == -1) {
			throw new IOException("EOF");
		}
		return (byte) read;
	}

	private byte[] getBytes(int l) throws IOException {
		byte[] b = new byte[l];
		int i = in.read(b);
		if (i < 0 || i < b.length) {
			throw new IOException("EOF");
		}
		return b;
	}

	public short nextShort() throws IOException {
		byte[] b = getBytes(2);
		return (short)((b[1] << 8) | (b[0] & 0xff));
	}

	public int nextInt() throws IOException {
		byte[] b = getBytes(4);
		return (((b[3]) << 24) |
				((b[2] & 0xff) << 16) |
				((b[1] & 0xff) << 8) |
				((b[0] & 0xff)));
	}

	public long nextLong() throws IOException {
		byte[] b = getBytes(8);
		return ((((long) b[7]) << 56) |
				(((long) b[6] & 0xff) << 48) |
				(((long) b[5] & 0xff) << 40) |
				(((long) b[4] & 0xff) << 32) |
				(((long) b[3] & 0xff) << 24) |
				(((long) b[2] & 0xff) << 16) |
				(((long) b[1] & 0xff) << 8) |
				(((long) b[0] & 0xff)));
	}

	public Uleb128 nextULEB128() throws IOException {
		return Uleb128.fromByteStream(in);
	}

	public String nextString() throws IOException {
		int i = nextByte();
		if(i == 0x0b) {
			int len = (int)Uleb128.fromByteStream(in).asLong();
			if(len == 0) {
				return "";
			}
			return new String(getBytes(len), "UTF-8");
		} else if(i == 0x00) {
			return null;
		}
		throw new IOException("Invalid string");
	}

	public void close() throws IOException {
		in.close();
	}
}