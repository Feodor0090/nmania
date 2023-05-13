package nmania;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Image;

import md5.MD5;
import nmania.beatmaps.IRawBeatmap;
import nmania.beatmaps.InvalidBeatmapTypeException;
import nmania.beatmaps.RawBeatmapConverter;

/**
 * Class with methods for beatmaps reading.
 * 
 * @author Feodor0090
 *
 */
public final class BeatmapManager {

	/**
	 * Loads the manager for specific URL.
	 * 
	 * @param wd Absolute path to folder with file:/// and trailing slash.
	 */
	public BeatmapManager(String wd) {
		directory = wd;
	}

	/**
	 * Absolute path to folder with charts with file:/// and trailing slash.
	 */
	public final String directory;
	private FileConnection fc;

	public void Init() throws IOException {
		fc = (FileConnection) Connector.open(directory, Connector.READ_WRITE);
		if (!fc.exists()) {
			fc.mkdir();
		}
	}

	public Enumeration list() throws IOException {
		return fc.list();
	}

	/**
	 * Takes chart directory name and creates its model.
	 * 
	 * @param dir Chart directory name.
	 * @return Null if there were no beatmaps, the model otherwise.
	 * @throws IOException                 Error during reading.
	 * @throws InvalidBeatmapTypeException Error during beatmsp conversion.
	 */
	public BeatmapSet FromBMSDirectory(String dir) throws IOException, InvalidBeatmapTypeException {
		FileConnection bmsFc = null;
		try {
			bmsFc = (FileConnection) Connector.open(directory + dir, Connector.READ);
			BeatmapSet bms = new BeatmapSet(directory, dir, bakeEnum(bmsFc.list()));
			String fm = null;
			String all = ""; // ?dbg
			{
				Enumeration bmsFiles = bmsFc.list();
				while (bmsFiles.hasMoreElements()) {
					String f = bmsFiles.nextElement().toString();
					all += " " + f; // ?dbg
					if (RawBeatmapConverter.CanReadFile(f)) {
						try {
							fm = getStringFromFS(directory + dir + f);
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (fm == null) {
				GL.Log("(bm) Attempt to read " + dir + " chart was made, but no beatmaps here.");
				GL.Log("(bm) Full path: " + directory + dir);
				GL.Log("(bm) Files here: " + all);
				return null;
			}
			IRawBeatmap bm = RawBeatmapConverter.FromText(fm);
			bms.Fill(bm);
			bmsFc.close();
			return bms;
		} finally {
			if (bmsFc != null)
				bmsFc.close();
		}
	}

	public static final String getStringFromFS(String path) {
		DataInputStream dis = null;
		FileConnection fcon = null;
		ByteArrayOutputStream o = null;
		try {
			o = new ByteArrayOutputStream();
			fcon = (FileConnection) Connector.open(path, Connector.READ);
			if (!fcon.exists())
				return null;
			dis = fcon.openDataInputStream();

			byte[] b = new byte[16384];

			int c;
			while ((c = dis.read(b)) != -1) {
				// var10 += (long) var7;
				o.write(b, 0, c);
				o.flush();
			}

			return new String(o.toByteArray(), "UTF-8");

		} catch (RuntimeException e) {
			return null;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (fcon != null)
					fcon.close();
			} catch (IOException e) {
			}
			try {
				if (dis != null)
					dis.close();
			} catch (IOException e) {
			}
			try {
				if (o != null)
					o.close();
			} catch (IOException e) {
			}
		}
	}

	public static final String getStringFromJAR(String path) {
		try {
			StringBuffer sb = new StringBuffer();
			char[] chars = new char[1024];
			InputStream stream = Nmania.class.getResourceAsStream(path);
			if (stream == null)
				return null;
			InputStreamReader isr;
			isr = new InputStreamReader(stream, "UTF-8");
			while (true) {
				int c = isr.read(chars);
				if (c == -1)
					break;
				sb.append(chars, 0, c);
			}
			isr.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Image getImgFromFS(String path) {
		InputStream is = null;
		FileConnection fcon = null;
		try {
			fcon = (FileConnection) Connector.open(path, Connector.READ);
			if (!fcon.exists())
				return null;
			is = fcon.openInputStream();

			Image img = Image.createImage(is);
			try {
				fcon.close();
				fcon = null;
			} catch (Exception e) {
			}
			return img;
		} catch (RuntimeException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		} finally {
			try {
				if (fcon != null)
					fcon.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Reads file's hash.
	 * 
	 * @param path Path to file. Must be global and contain file:///.
	 * @return String with MD5.
	 */
	public static String getMD5FromFs(String path) {
		InputStream is = null;
		FileConnection fcon = null;
		try {
			fcon = (FileConnection) Connector.open(path, Connector.READ);
			if (!fcon.exists()) {
				GL.Log("Requested MD5 of non-existing " + path);
				return null;
			}
			is = fcon.openInputStream();

			MD5 md5 = new MD5();
			md5.read(is);
			try {
				fcon.close();
				fcon = null;
			} catch (Exception e) {
			}
			return md5.digestString();
		} catch (Exception e) {
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		} finally {
			try {
				if (fcon != null)
					fcon.close();
			} catch (IOException e) {
			}
		}
	}

	private final static String[] bakeEnum(Enumeration e) {
		Vector v = new Vector();
		while (e.hasMoreElements())
			v.addElement(e.nextElement().toString());

		String[] a = new String[v.size()];
		v.copyInto(a);
		return a;
	}
}
