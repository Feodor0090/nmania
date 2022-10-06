package md5;

import java.io.InputStream;

public class MD5 {
	private static MD5 inst;

	private static void init() {
		inst = new MD5();
	}
	public synchronized static byte[] getMD5(byte[] b) throws Exception {
		if (inst == null) init();
		inst.update(b);
		return inst.digest();
	}
	public static String getMD5String(byte[] b) throws Exception {
		if (inst == null) init();
		inst.update(b);
		return inst.digestString();
	}
	public static String getMD5String(String s) throws Exception {
		if (inst == null) init();
		inst.update(s.getBytes());
		return inst.digestString();
	}
	
	private boolean satsa;
	private MD5Calc calc;
	private MessageDigestLayer md;
	
	public MD5() {
		try {
			Class.forName("java.security.MessageDigest");
			satsa = true;
			md = MessageDigestLayer.getInstance("MD5");
		} catch (Throwable e) {
			calc = new MD5Calc();
		}
	}

	public void reset() {
		if (satsa) {
			md.reset();
		} else {
			calc.reset();
		}
	}

	public void update(byte[] b) throws Exception {
		update(b, 0, b.length);
	}

	public void update(byte[] b, int offset, int length) throws Exception {
		if (satsa) {
			md.update(b, offset, length);
		} else {
			calc.update(b, offset, length);
		}
	}

	public void read(InputStream in) throws Exception {
		read(in, 4096);
	}

	public void read(InputStream in, int bufferSize) throws Exception {
		byte[] buf = new byte[bufferSize];
		int r;
		while((r = in.read(buf)) != -1) {
			update(buf, 0, r);
		}
		in.close();
	}

	public byte[] digest() throws Exception {
		byte[] r = new byte[16];
		if (satsa) {
			md.digest(r, 0, 16);
		} else {
			calc.digest(r, 0, 16);
		}
		return r;
	}

	public String digestString() throws Exception {
		byte[] r = digest();
		StringBuffer sb = new StringBuffer();
		char[] dict = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		for (int i = 0; i < r.length; ++i) {
			sb.append(dict[r[i] >>> 4 & 15]).append(dict[r[i] & 15]);
		}
		return sb.toString();
	}

}
