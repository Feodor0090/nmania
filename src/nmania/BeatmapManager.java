package nmania;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Image;

import nmania.formats.IRawBeatmap;
import nmania.formats.InvalidBeatmapTypeException;
import nmania.formats.RawBeatmapConverter;

public class BeatmapManager {

	public BeatmapManager(String wd) {
		directory = wd;
	}

	public final String directory;
	FileConnection fc;

	public void Init() throws IOException {
		fc = (FileConnection) Connector.open(directory, Connector.READ_WRITE);
		if (!fc.exists()) {
			fc.mkdir();
		}
	}

	public Enumeration list() throws IOException {
		return fc.list();
	}

	public BeatmapSet FromBMSDirectory(String dir) throws IOException, InvalidBeatmapTypeException {
		FileConnection bmsFc = (FileConnection) Connector.open(directory + dir, Connector.READ);
		BeatmapSet bms = new BeatmapSet();
		bms.wdPath = directory;
		bms.folderName = dir;
		String fm = null;
		{
			Enumeration bmsFiles = bmsFc.list();
			while (bmsFiles.hasMoreElements()) {
				String f = bmsFiles.nextElement().toString();
				if (f.endsWith(".osu") || f.endsWith(".nmbm")) {
					fm = getStringFromFS(directory + dir + f);
					break;
				}
			}
		}
		if (fm == null)
			return null;
		if (fm.startsWith("osu file format")) {
			IRawBeatmap osu = RawBeatmapConverter.FromText(fm);
			// osu! beatmap
			int metadataI = fm.indexOf("[Metadata]");
			int eventsI = fm.indexOf("[Events]");

			int titleI = fm.indexOf("\nTitle:", metadataI) + 7;
			int artistI = fm.indexOf("\nArtist:", metadataI) + 8;
			int creatorI = fm.indexOf("\nCreator:", metadataI) + 9;
			
			bms.title = deCR(fm.substring(titleI, fm.indexOf('\n', titleI)));
			bms.artist = deCR(fm.substring(artistI, fm.indexOf('\n', artistI)));
			bms.mapper = deCR(fm.substring(creatorI, fm.indexOf('\n', creatorI)));
			bms.image = osu.GetImage();
		} else {
			bms.title = "todo";
			bms.artist = "todo";
			bms.mapper = "todo";
			bms.image = "todo";
		}

		bms.files = bakeEnum(bmsFc.list());
		bmsFc.close();
		return bms;
	}

	private final static String deCR(String s) {
		if (s.charAt(s.length() - 1) == '\r')
			return s.substring(0, s.length() - 1);
		return s;
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
