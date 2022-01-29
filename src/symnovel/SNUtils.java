package symnovel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.TextBox;

import org.json.me.JSONArray;
import org.json.me.JSONException;

public final class SNUtils {

	public static void alertBlocking(String title, String text, AlertType type, final Displayable d)
			throws InterruptedException {
		final Alert a = new Alert(title, text, null, type);
		a.setTimeout(Alert.FOREVER);
		final Object gate = new Object();
		final Command ok = new Command("Close", Command.OK, 0);
		a.addCommand(ok);
		a.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				if (arg0 == ok) {
					if (d != null)
						Display.getDisplay(SymNovel.app).setCurrent(d);
					synchronized (gate) {
						gate.notifyAll();
					}
				}
			}
		});
		Display.getDisplay(SymNovel.app).setCurrent(a);
		SymNovel.log("Alert \"" + title + "\" shown.");
		synchronized (gate) {
			gate.wait();
			Thread.sleep(50);
		}
	}

	/**
	 * Shows an async alert, that activates previous screen on dismiss.
	 * 
	 * @param title Title of the alert.
	 * @param text  Text of the alert.
	 * @param type  Alert type.
	 */
	public static void alertAsync(String title, String text, AlertType type) {
		final Alert a = new Alert(title, text, null, type);
		a.setTimeout(Alert.FOREVER);
		Display.getDisplay(SymNovel.app).setCurrent(a);
		SymNovel.log("Async alert \"" + title + "\" shown.");
	}

	public static boolean askYN(String title, String text) throws InterruptedException {
		return ask(title, text, false, "Yes", "No", Display.getDisplay(SymNovel.app).getCurrent());
	}

	public static boolean ask(String title, String text, boolean warn, String ok, String cancel, final Displayable next)
			throws InterruptedException {
		if (next == null)
			throw new IllegalArgumentException();
		final Alert a = new Alert(title, text, null, warn ? AlertType.WARNING : AlertType.INFO);
		a.setTimeout(Alert.FOREVER);
		final Object gate = new Object();
		final BoolContainer b = new BoolContainer();
		final Command okCmd = new Command(ok, Command.OK, 0);
		final Command cancelCmd = new Command(cancel, Command.CANCEL, 0);
		a.addCommand(okCmd);
		a.addCommand(cancelCmd);
		a.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				if (arg0 == okCmd || arg0 == cancelCmd) {
					Display.getDisplay(SymNovel.app).setCurrent(next);
					b.value = (arg0 == okCmd);
					synchronized (gate) {
						gate.notifyAll();
					}
				}
			}
		});
		Display.getDisplay(SymNovel.app).setCurrent(a);
		SymNovel.log("Ask \"" + title + "\" shown.");

		synchronized (gate) {
			gate.wait();
			Thread.sleep(50);
		}
		return b.value;
	}

	/**
	 * Inputs text via LCDUI textbox.
	 * 
	 * @param title    Title of the box.
	 * @param curr     Initial content of the box.
	 * @param max      Maximum length of the text.
	 * @param password Are we entering password?
	 * @return <b>Null</b> if input was canceled, string if anything was typed.
	 * @throws InterruptedException If waiting was interrupted.
	 */
	public static String inputString(final String title, final String curr, final int max, final boolean password)
			throws InterruptedException {
		final Object gate = new Object();
		final String[] s = new String[] { null };
		final Command okCmd = new Command("OK", Command.OK, 0);
		final Command cancelCmd = new Command("Cancel", Command.CANCEL, 0);
		final Displayable screen = Display.getDisplay(SymNovel.app).getCurrent();
		final TextBox textBox = new TextBox("", "", (max > 0) ? max : 1024, password ? 65536 : 0);
		textBox.setTitle(title);
		textBox.setString(curr);
		textBox.addCommand(okCmd);
		textBox.addCommand(cancelCmd);
		textBox.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				if (arg0 == okCmd || arg0 == cancelCmd) {
					Display.getDisplay(SymNovel.app).setCurrent(screen);
					s[0] = (arg0 == okCmd) ? textBox.getString() : null;
					synchronized (gate) {
						gate.notifyAll();
					}
				}
			}
		});
		Display.getDisplay(SymNovel.app).setCurrent(textBox);
		SymNovel.log("TextBox \"" + title + "\" shown.");

		synchronized (gate) {
			gate.wait();
			Thread.sleep(50);
		}

		return s[0];
	}

	public static String readJARRes(String name, int cap) throws IOException {
		char[] chars = new char[cap];
		InputStream stream = SymNovel.app.getClass().getResourceAsStream(name);
		if (stream == null)
			throw new IOException();
		InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
		int c = isr.read(chars);
		System.out.println("ReadJARRes: c=" + c);
		isr.close();
		return String.valueOf(chars, 0, c);
	}

	public static String[] split2(String str, char c) {
		if (str == null)
			return new String[] { "", "" };
		try {
			int pos = str.indexOf(c);
			if (pos == -1) {
				return new String[] { str, "" };
			}
			if (pos == str.length() - 1) {
				return new String[] { str.substring(0, pos), "" };
			}
			return new String[] { str.substring(0, pos), str.substring(pos + 1) };
		} catch (IndexOutOfBoundsException e) {
			return new String[] { str, "" };
		}
	}

	public static String[] split(String str, char c, int count) {
		String[] a = new String[count];
		int lle = 0;
		int i;
		for (i = 0; i < count; i++) {
			int nle = str.indexOf(c, lle);
			if (nle == -1 || i == count - 1) {
				a[i] = str.substring(lle, str.length());
				for (i++; i < count; i++) {
					a[i] = "".intern();
				}
				break;
			}

			a[i] = str.substring(lle, nle);
			lle = nle + 1;
		}
		return a;
	}

	public static String[] splitFull(String str, char c) {
		Vector v = new Vector(32, 16);
		int lle = 0;
		while (true) {
			int nle = str.indexOf(c, lle);
			if (nle == -1) {
				v.addElement(str.substring(lle, str.length()));
				break;
			}

			v.addElement(str.substring(lle, nle));
			lle = nle + 1;
		}
		String[] a = new String[v.size()];
		v.copyInto(a);
		v.removeAllElements();
		v.trimToSize();
		v = null;
		return a;
	}

	public static int[] strArr2intArr(String[] a) {
		int l = a.length;
		int[] aa = new int[l];
		for (int i = 0; i < l; i++) {
			String s = a[i];
			aa[i] = (s == null ? 0 : Integer.parseInt(s));
		}
		return aa;
	}

	public static byte[] intArr2byteArr(int[] a) {
		int l = a.length;
		byte[] aa = new byte[l];
		for (int i = 0; i < l; i++) {
			aa[i] = (byte) a[i];
		}
		return aa;
	}

	public static String fixedNumber(int n, int l, char s) {
		String ns = String.valueOf(n);
		int nsl = ns.length();
		if (nsl == l)
			return ns;
		if (nsl < l) {
			StringBuffer sb = new StringBuffer(l);
			for (int i = 0; i < l - nsl; i++)
				sb.append(s);
			sb.append(ns);
			return sb.toString();
		}
		return ns.substring(nsl - l);
	}

	public static float sin(float a) {
		return (float) Math.sin(a * Math.PI / 180f);
	}

	public static float cos(float a) {
		return (float) Math.cos(a * Math.PI / 180f);
	}

	/**
	 * Returns a power of 2.
	 * 
	 * @param p Power.
	 * @return 2**p
	 */
	public static int pow2(int p) {
		if (p < 0)
			return 0;
		return 1 << p;
	}

	public static String replace(String str, String from, String to) {

		final StringBuffer sb = new StringBuffer();
		int j = str.indexOf(from);
		int k = 0;

		if (j == -1)
			return str;

		for (int i = from.length(); j != -1; j = str.indexOf(from, k)) {
			sb.append(str.substring(k, j)).append(to);
			k = j + i;
		}

		sb.append(str.substring(k, str.length()));
		return sb.toString();
	}

	/**
	 * Replaces specified range in source string with another string. For example,
	 * to replace "345" to "QWE" in "01234567", pass 3 and 5 as start and end
	 * indexes and "QWE" as "to"-string.
	 * 
	 * @param str Source string.
	 * @param si  Start index.
	 * @param ei  End index.
	 * @param to  String to replace specified range.
	 * @return Processed string.
	 */
	public static String replace(String str, int si, int ei, String to) {
		final StringBuffer sb = new StringBuffer();
		sb.append(str.substring(0, si));
		sb.append(to);
		sb.append(str.substring(ei + 1));
		return sb.toString();
	}

	public static int toARGB(String text) {
		try {
			int l = text.length();
			// hex
			if (text.startsWith("0x")) {
				// 0xRGB
				if (l == 5) {
					int r = hex2int(text.charAt(2));
					int g = hex2int(text.charAt(3));
					int b = hex2int(text.charAt(4));
					return (255 << 24) | ((r * 16 + r) << 16) | ((g * 16 + g) << 8) | (b * 16 + b);
				}
				// 0xARGB
				if (l == 6) {
					int a = hex2int(text.charAt(2));
					int r = hex2int(text.charAt(3));
					int g = hex2int(text.charAt(4));
					int b = hex2int(text.charAt(5));
					return ((a * 16 + a) << 24) | ((r * 16 + r) << 16) | ((g * 16 + g) << 8) | (b * 16 + b);
				}
				// 0xRRGGBB
				if (l == 8) {
					return (255 << 24) | ((hex2int(text.charAt(2)) * 16 + hex2int(text.charAt(3))) << 16)
							| ((hex2int(text.charAt(4)) * 16 + hex2int(text.charAt(5))) << 8)
							| (hex2int(text.charAt(6)) * 16 + hex2int(text.charAt(7)));
				}
				// 0xAARRGGBB
				if (l == 10) {
					return ((hex2int(text.charAt(2)) * 16 + hex2int(text.charAt(3))) << 24)
							| ((hex2int(text.charAt(4)) * 16 + hex2int(text.charAt(5))) << 16)
							| ((hex2int(text.charAt(6)) * 16 + hex2int(text.charAt(7))) << 8)
							| (hex2int(text.charAt(8)) * 16 + hex2int(text.charAt(9)));
				}
			}
			// code
			if (text.charAt(l - 1) >= '0' && text.charAt(l - 1) <= '9') {
				return Integer.parseInt(text);
			}
			// words
			/*
			 * text = text.toLowerCase(); if(text.equals("white")) return toARGB("0xFFF");
			 * if(text.equals("trwhite")) return toARGB("0x0FFF"); if(text.equals("white"))
			 * return toARGB("0xFFF");
			 */
			// won't support
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public static int hex2int(char c) {
		if (c >= '0' && c <= '9')
			return (c - '0');
		if (c >= 'a' && c <= 'f')
			return (c - 'a' + 10);
		if (c >= 'A' && c <= 'F')
			return (c - 'A' + 10);
		throw new NumberFormatException();
	}

	public static String[] json2array(JSONArray j) {
		if (j == null)
			return null;
		if (j.length() == 0)
			return new String[0];

		String[] a = new String[j.length()];
		for (int i = 0; i < a.length; i++) {
			a[i] = j.optString(i);
		}
		return a;
	}

	public static int[] json2intArray(JSONArray j) {
		if (j == null)
			return null;
		if (j.length() == 0)
			return new int[0];

		int[] a = new int[j.length()];
		for (int i = 0; i < a.length; i++) {
			a[i] = j.optInt(i);
		}
		return a;
	}

	public static float[] json2floatArray(JSONArray j) {
		if (j == null)
			return null;
		if (j.length() == 0)
			return new float[0];

		float[] a = new float[j.length()];
		for (int i = 0; i < a.length; i++) {
			try {
				a[i] = (float) j.getDouble(i);
			} catch (JSONException e) {
				a[i] = j.optInt(i);
			}
		}
		return a;
	}

	/**
	 * Breaks the text to lines, using specified font and maximum line width.
	 * 
	 * @deprecated You should use {@link symnovel.ui.blocks.Text text block} to
	 *             place a block of breakable text on screen.
	 * @param text     The text to break.
	 * @param font     Font that will be used.
	 * @param maxWidth Maximum line width.
	 * @return Array of lines.
	 */
	public static String[] breakText(String text, Font font, int maxWidth) {
		// empty str check
		if (text == null || text.length() == 0 || (text.length() == 1 && text.charAt(0) == ' ')) {
			return new String[0];
		}

		// if fits
		if (font.stringWidth(text) <= maxWidth) {
			return new String[] { text };
		}

		// buffers
		Vector v = new Vector(5, 3);
		char[] chars = text.toCharArray();

		// cycle
		int i1 = 0;
		for (int i2 = 0; i2 < text.length(); i2++) {

			if (chars[i2] == '\r' && (i2 + 1 >= chars.length || chars[i2 + 1] == '\n')) {
				v.addElement(text.substring(i1, i2));
				i2 = i1 = i2 + 2;
			} else if (chars[i2] == '\n') {
				v.addElement(text.substring(i1, i2));
				i2 = i1 = i2 + 1;
			} else {
				if (text.length() - i2 <= 1) {
					v.addElement(text.substring(i1, text.length()));
					break;
				} else if (font.substringWidth(text, i1, i2 - i1) >= maxWidth) {
					boolean f = false;
					for (int j = i2; j > i1; j--) {
						char c = text.charAt(j);
						if (c == ' ' || c == '-') {
							f = true;
							v.addElement(text.substring(i1, j + 1));
							i2 = i1 = j + 1;
							break;
						}
					}
					if (!f) {
						i2 = i2 - 2;
						v.addElement(text.substring(i1, i2));
						i2 = i1 = i2 + 1;
					}
				}
			}
		}
		String[] r = new String[v.size()];
		v.copyInto(r);
		v.removeAllElements();
		v.trimToSize();
		return r;
	}

	public static void flags2ba(int f, boolean[] t) {
		for (int i = 0; i < t.length; i++) {
			t[i] = ((f >> i) & 1) == 1;
		}
	}

	public static int ba2flags(boolean[] a) {
		int r = 0;
		for (int i = 0; i < a.length; i++) {
			r = r | ((a[i] ? 1 : 0) << i);
		}
		return r;
	}

	/**
	 * Contains all data, obtained by
	 * {@link SNUtils#balanceBrackets(String, char, char, int)} method.
	 */
	public static class BracketBalanceQuery {
		/**
		 * First bracket index.
		 */
		public int fbIndex;
		/**
		 * Last bracket index.
		 */
		public int lbIndex;
		/**
		 * Count of nested brackets.
		 */
		public int nestingCount;
		/**
		 * Most nested first bracket index.
		 */
		public int mnfbIndex;
		/**
		 * Most nested last bracket index.
		 */
		public int mnlbIndex;
		/**
		 * Source string.
		 */
		public String source;
		/**
		 * Opening bracket.
		 */
		public char brCh1;
		/**
		 * Closing bracket.
		 */
		public char brCh2;
		/**
		 * Are there other opening brackets further down the string?
		 */
		public boolean hasNextBr;

		/**
		 * Gets everything in the 0-level brackets.
		 * 
		 * @return String part between brackets.
		 */
		public String getBalanced() {
			return source.substring(fbIndex + 1, lbIndex);
		}

		/**
		 * Gets everything in the most nested brackets.
		 * 
		 * @return String part between brackets.
		 */
		public String getMNBalanced() {
			return source.substring(mnfbIndex + 1, mnlbIndex);
		}
	}

	/**
	 * Checks the string about balanced brackets and returns obtained info.
	 * 
	 * @param s    The string.
	 * @param br1  Opening bracket character, for example, "{".
	 * @param br2  Closing bracket character, for example, "}".
	 * @param from Starting index.
	 * @return Obtained info. <b>Null</b> if there are no brackets at all.
	 */
	public static BracketBalanceQuery balanceBrackets(String s, char br1, char br2, int from) {
		int fbi = s.indexOf(br1, from);
		if (fbi == -1)
			return null;

		BracketBalanceQuery bbq = new BracketBalanceQuery();
		bbq.source = s;
		bbq.brCh1 = br1;
		bbq.brCh2 = br2;

		bbq.fbIndex = fbi;
		int mnc = 0;
		int nc = 0;
		int mni1 = 0;
		int mni2 = 0;

		for (int i = fbi + 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == br1) {
				nc++;
				if (nc >= mnc) {
					mnc = nc;
					mni1 = i;
				}
			}
			if (c == br2) {
				if (nc == mnc) {
					mni2 = i;
				}
				nc--;
			}
			if (nc == -1) {
				bbq.lbIndex = i;
				bbq.hasNextBr = (s.indexOf(br1, i + 1) != -1);
				break;
			}
		}
		bbq.nestingCount = mnc;
		if (mnc == 0) {
			bbq.mnfbIndex = fbi;
			bbq.mnlbIndex = bbq.lbIndex;
		} else {
			bbq.mnfbIndex = mni1;
			bbq.mnlbIndex = mni2;
		}

		return bbq;
	}

	public static void log(Object[] o, boolean inline) {
		System.out.println();
		for (int i = 0; i < o.length; i++) {
			// logsStorage.addElement(o[i].toString());
			if (inline) {
				System.out.print(o[i].toString() + "; ");
				continue;
			}
			System.out.println(o[i].toString());

		}
	}

	public static void log(int[] o, boolean inline) {
		System.out.println();
		for (int i = 0; i < o.length; i++) {
			if (inline) {
				System.out.print(o[i] + "; ");
				continue;
			}
			System.out.println(o[i]);
		}
		System.out.println();
	}

	public static void log(short[] o, boolean inline) {
		System.out.println();
		for (int i = 0; i < o.length; i++) {
			if (inline) {
				System.out.print(o[i] + "; ");
				continue;
			}
			System.out.println(o[i]);
		}
		System.out.println();
	}

	public static void log(byte[] o, boolean inline) {
		System.out.println();
		for (int i = 0; i < o.length; i++) {
			if (inline) {
				System.out.print(o[i] + "; ");
				continue;
			}
			System.out.println(o[i]);
		}
		System.out.println();
	}

	public static void sort(IComparable[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = 0; j < i; j++) {
				if (a[j].compareTo(a[j + 1]) > 0) {
					IComparable t = a[j];
					a[j] = a[j + 1];
					a[j + 1] = t;
				}
			}
		}
	}
}
