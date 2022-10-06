package md5;

import java.security.MessageDigest;

public class MessageDigestLayer {
	
	private MessageDigest md;

	public MessageDigestLayer(MessageDigest md) {
		this.md = md;
	}

	public static MessageDigestLayer getInstance(String algorithm) throws Exception {
		return new MessageDigestLayer(MessageDigest.getInstance(algorithm));
	}
	
	public void update(byte[] input, int offset, int len) {
		md.update(input, offset, len);
	}

	public int digest(byte[] buf, int offset, int len) throws Exception {
		return md.digest(buf, offset, len);
	}
	
	public void reset() {
		md.reset();
	}

}
