package md5;

/**
 * @author Shinovon
 */
final class MD5Calc {
	private byte[] digestBits;
	private int[] state;
	private long count;
	private byte[] buffer;
	private int[] transformBuffer;

	public MD5Calc() {
		init();
	}

	private int FF(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		var1 += (var2 & var3 | ~var2 & var4) + var5 + var7;
		return (var1 << var6 | var1 >>> 32 - var6) + var2;
	}

	private int GG(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		var1 += (var2 & var4 | var3 & ~var4) + var5 + var7;
		return (var1 << var6 | var1 >>> 32 - var6) + var2;
	}

	private int HH(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		var1 += (var2 ^ var3 ^ var4) + var5 + var7;
		return (var1 << var6 | var1 >>> 32 - var6) + var2;
	}

	private int II(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		var1 += (var3 ^ (var2 | ~var4)) + var5 + var7;
		return (var1 << var6 | var1 >>> 32 - var6) + var2;
	}

	private void transform(byte[] b, int i) {
		int[] a = transformBuffer;
		int s0 = state[0];
		int s1 = state[1];
		int s2 = state[2];
		int s3 = state[3];

		for (int j = 0; j < 16; ++j) {
			a[j] = b[j * 4 + i] & 255;

			for (int k = 1; k < 4; ++k) {
				a[j] += (b[j * 4 + k + i] & 255) << k * 8;
			}
		}

		s0 = FF(s0, s1, s2, s3, a[0], 7, -680876936);
		s3 = FF(s3, s0, s1, s2, a[1], 12, -389564586);
		s2 = FF(s2, s3, s0, s1, a[2], 17, 606105819);
		s1 = FF(s1, s2, s3, s0, a[3], 22, -1044525330);
		s0 = FF(s0, s1, s2, s3, a[4], 7, -176418897);
		s3 = FF(s3, s0, s1, s2, a[5], 12, 1200080426);
		s2 = FF(s2, s3, s0, s1, a[6], 17, -1473231341);
		s1 = FF(s1, s2, s3, s0, a[7], 22, -45705983);
		s0 = FF(s0, s1, s2, s3, a[8], 7, 1770035416);
		s3 = FF(s3, s0, s1, s2, a[9], 12, -1958414417);
		s2 = FF(s2, s3, s0, s1, a[10], 17, -42063);
		s1 = FF(s1, s2, s3, s0, a[11], 22, -1990404162);
		s0 = FF(s0, s1, s2, s3, a[12], 7, 1804603682);
		s3 = FF(s3, s0, s1, s2, a[13], 12, -40341101);
		s2 = FF(s2, s3, s0, s1, a[14], 17, -1502002290);
		s1 = FF(s1, s2, s3, s0, a[15], 22, 1236535329);
		s0 = GG(s0, s1, s2, s3, a[1], 5, -165796510);
		s3 = GG(s3, s0, s1, s2, a[6], 9, -1069501632);
		s2 = GG(s2, s3, s0, s1, a[11], 14, 643717713);
		s1 = GG(s1, s2, s3, s0, a[0], 20, -373897302);
		s0 = GG(s0, s1, s2, s3, a[5], 5, -701558691);
		s3 = GG(s3, s0, s1, s2, a[10], 9, 38016083);
		s2 = GG(s2, s3, s0, s1, a[15], 14, -660478335);
		s1 = GG(s1, s2, s3, s0, a[4], 20, -405537848);
		s0 = GG(s0, s1, s2, s3, a[9], 5, 568446438);
		s3 = GG(s3, s0, s1, s2, a[14], 9, -1019803690);
		s2 = GG(s2, s3, s0, s1, a[3], 14, -187363961);
		s1 = GG(s1, s2, s3, s0, a[8], 20, 1163531501);
		s0 = GG(s0, s1, s2, s3, a[13], 5, -1444681467);
		s3 = GG(s3, s0, s1, s2, a[2], 9, -51403784);
		s2 = GG(s2, s3, s0, s1, a[7], 14, 1735328473);
		s1 = GG(s1, s2, s3, s0, a[12], 20, -1926607734);
		s0 = HH(s0, s1, s2, s3, a[5], 4, -378558);
		s3 = HH(s3, s0, s1, s2, a[8], 11, -2022574463);
		s2 = HH(s2, s3, s0, s1, a[11], 16, 1839030562);
		s1 = HH(s1, s2, s3, s0, a[14], 23, -35309556);
		s0 = HH(s0, s1, s2, s3, a[1], 4, -1530992060);
		s3 = HH(s3, s0, s1, s2, a[4], 11, 1272893353);
		s2 = HH(s2, s3, s0, s1, a[7], 16, -155497632);
		s1 = HH(s1, s2, s3, s0, a[10], 23, -1094730640);
		s0 = HH(s0, s1, s2, s3, a[13], 4, 681279174);
		s3 = HH(s3, s0, s1, s2, a[0], 11, -358537222);
		s2 = HH(s2, s3, s0, s1, a[3], 16, -722521979);
		s1 = HH(s1, s2, s3, s0, a[6], 23, 76029189);
		s0 = HH(s0, s1, s2, s3, a[9], 4, -640364487);
		s3 = HH(s3, s0, s1, s2, a[12], 11, -421815835);
		s2 = HH(s2, s3, s0, s1, a[15], 16, 530742520);
		s1 = HH(s1, s2, s3, s0, a[2], 23, -995338651);
		s0 = II(s0, s1, s2, s3, a[0], 6, -198630844);
		s3 = II(s3, s0, s1, s2, a[7], 10, 1126891415);
		s2 = II(s2, s3, s0, s1, a[14], 15, -1416354905);
		s1 = II(s1, s2, s3, s0, a[5], 21, -57434055);
		s0 = II(s0, s1, s2, s3, a[12], 6, 1700485571);
		s3 = II(s3, s0, s1, s2, a[3], 10, -1894986606);
		s2 = II(s2, s3, s0, s1, a[10], 15, -1051523);
		s1 = II(s1, s2, s3, s0, a[1], 21, -2054922799);
		s0 = II(s0, s1, s2, s3, a[8], 6, 1873313359);
		s3 = II(s3, s0, s1, s2, a[15], 10, -30611744);
		s2 = II(s2, s3, s0, s1, a[6], 15, -1560198380);
		s1 = II(s1, s2, s3, s0, a[13], 21, 1309151649);
		s0 = II(s0, s1, s2, s3, a[4], 6, -145523070);
		s3 = II(s3, s0, s1, s2, a[11], 10, -1120210379);
		s2 = II(s2, s3, s0, s1, a[2], 15, 718787259);
		s1 = II(s1, s2, s3, s0, a[9], 21, -343485551);
		state[0] += s0;
		state[1] += s1;
		state[2] += s2;
		state[3] += s3;
	}

	private void init() {
		state = new int[4];
		transformBuffer = new int[16];
		buffer = new byte[64];
		digestBits = new byte[16];
		count = 0L;
		state[0] = 1732584193;
		state[1] = -271733879;
		state[2] = -1732584194;
		state[3] = 271733878;
		for (int i = 0; i < digestBits.length; ++i) {
			digestBits[i] = 0;
		}
	}

	public void reset() {
		init();
	}
	
	public void update(byte[] b) {
		update(b, 0, b.length);
	}

	public void update(byte[] b, int var2, int var3) {
		int var4 = var2;

		while (true) {
			while (var3 > 0) {
				int var5 = (int) (count >>> 3 & 63L);
				if (var5 == 0 && var3 > 64) {
					count += 512L;
					transform(b, var4);
					var3 -= 64;
					var4 += 64;
				} else {
					count += 8L;
					buffer[var5] = b[var4];
					if (var5 >= 63) {
						transform(buffer, 0);
					}

					++var4;
					--var3;
				}
			}

			return;
		}
	}

	private void finish() {
		byte[] var1 = new byte[8];

		int var3;
		for (var3 = 0; var3 < 8; ++var3) {
			var1[var3] = (byte) ((int) (count >>> var3 * 8 & 255L));
		}

		int var4 = (int) (count >> 3) & 63;
		int var5 = var4 < 56 ? 56 - var4 : 120 - var4;
		byte[] var2 = new byte[var5];
		var2[0] = -128;
		update(var2, 0, var2.length);
		update(var1, 0, var1.length);

		for (var3 = 0; var3 < 4; ++var3) {
			for (int var6 = 0; var6 < 4; ++var6) {
				digestBits[var3 * 4 + var6] = (byte) (state[var3] >>> var6 * 8 & 255);
			}
		}

	}

	public byte[] digest() {
		finish();
		byte[] b = new byte[16];
		System.arraycopy(digestBits, 0, b, 0, 16);
		init();
		return b;
	}

	public int digest(byte[] b, int var2, int var3) {
		finish();
		System.arraycopy(digestBits, 0, b, var2, 16);
		init();
		return 16;
	}
}
