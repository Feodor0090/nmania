package zip;

import java.io.*;

class RandomAccessFile extends ByteArrayInputStream {
	private int idx;

	public RandomAccessFile(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}

	public synchronized int read() {
		if (idx > 0 && pos == count) {
			idx = 0;
			++pos;
			return 0;
		}
		return super.read();
	}

	public synchronized int read(byte[] b, int off, int len) {
		int i = super.read(b, off, len);
		if (idx > 0 && i < len) {
			idx = 0;
			if (pos < count) {
				b[off + i++] = this.buf[pos++];
			} else if (pos == count) {
				if (i == -1) {
					i = 0;
				}
				b[off + i++] = 0;
				++pos;
			}
		}
		return i;
	}

	void seek(int pos) {
		this.pos = pos;
	}

	void readFully(byte[] b) throws EOFException {
		if (read(b, 0, b.length) != b.length) {
			throw new EOFException();
		}
	}

	synchronized int readShort() throws EOFException {
		int b0 = read();
		int b1 = read();
		if (b1 == -1) {
			throw new EOFException();
		}
		return (b0 & 0xFF) | (b1 & 0xFF) << 8;
	}

	synchronized int readInt() throws EOFException {
		int b0 = read();
		int b1 = read();
		int b2 = read();
		int b3 = read();
		if (b3 == -1) {
			throw new EOFException();
		}
		return (b0 & 0xFF) | (b1 & 0xFF) << 8 | ((b2 & 0xFF) | (b3 & 0xFF) << 8) << 16;
	}

	synchronized String readUTF(int len) throws EOFException {
		if (len > count - pos) {
			throw new EOFException();
		}
		byte[] b = new byte[len];
		readFully(b);
		try {
			return new String(b, 0, len, "UTF-8");
		} catch (Exception ex2) {
			try {
				return new String(b);
			} catch (Exception ex) {
				throw new Error(ex.toString());
			}
		}
	}
}