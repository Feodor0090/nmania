package nmania.replays.osu;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Wrapper for writing OSR files.
 * 
 * @author Shinovon
 * 
 */
public class OsrWriter {
	private DataOutputStream out;

	public OsrWriter(DataOutputStream stream) {
		this.out = stream;
	}

	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public void writeByte(int v) throws IOException {
		out.writeByte(v);
	}

	public void writeInt(int v) throws IOException {
		out.write(0xFF & v);
		out.write(0xFF & (v >> 8));
		out.write(0xFF & (v >> 16));
		out.write(0xFF & (v >> 24));
	}

	public void writeLong(long v) throws IOException {
		out.write((int) (0xFF & v));
		out.write((int) (0xFF & (v >> 8)));
		out.write((int) (0xFF & (v >> 16)));
		out.write((int) (0xFF & (v >> 24)));
		out.write((int) (0xFF & (v >> 32)));
		out.write((int) (0xFF & (v >> 40)));
		out.write((int) (0xFF & (v >> 48)));
		out.write((int) (0xFF & (v >> 56)));
	}

	public void writeShort(int v) throws IOException {
		out.write(0xFF & v);
		out.write(0xFF & (v >> 8));
	}
	
	public void writeString(String v) throws IOException {
		if(v == null) {
			writeByte((byte) 0x00);
			return;
		}
		writeByte((byte) 0x0b);
		byte[] b = v.getBytes("UTF-8");
		write(Uleb128.fromLong(b.length).asBytes());
		write(b);
	}

	public void close() throws IOException {
		out.close();
	}

}
