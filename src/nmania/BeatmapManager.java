package nmania;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class BeatmapManager {

	public BeatmapManager(String wd) {
		directory = wd;
	}

	public final String directory;
	FileConnection fc;

	public void Init() throws IOException {
		fc = (FileConnection) Connector.open(directory, Connector.READ);
	}

	public Enumeration list() throws IOException {
		return fc.list();
	}

	public BeatmapSet FromBMSDirectory(String dir) throws IOException {
		FileConnection bmsFc = (FileConnection) Connector.open(directory + dir, Connector.READ);
		BeatmapSet bms = new BeatmapSet();
		bms.wdPath = directory;
		bms.folderName = dir;
		String fm;
		{
			String[] maps = bakeEnum(bmsFc.list("*.osu", false));
			if (maps.length == 0)
				maps = bakeEnum(bmsFc.list("*.nmbm", false));
			fm = getStringFromFS(maps[0]);
		}
		if (fm.startsWith("osu file format")) {
			// osu! beatmap
			int metadataI = fm.indexOf("[Metadata]");
			int eventsI = fm.indexOf("[Events]");

			int titleI = fm.indexOf("\nTitle:", metadataI);
			int artistI = fm.indexOf("\nArtist:", metadataI);
			int creatorI = fm.indexOf("\nCreator:", metadataI);
			int imageI = fm.indexOf("\n0,", eventsI);
			
			bms.title = fm.substring(titleI, fm.indexOf('\n', titleI));
		} else {
			// nmania json beatmap
		}

		bms.files = bakeEnum(bmsFc.list());
		bmsFc.close();
		return null;
	}

	public static final String getStringFromFS(String path) {
		DataInputStream dis = null;
		FileConnection fcon = null;
		ByteArrayOutputStream o = null;
		try {
			o = new ByteArrayOutputStream();
			fcon = (FileConnection) Connector.open(path);
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

	private final static String[] bakeEnum(Enumeration e) {
		Vector v = new Vector();
		while (e.hasMoreElements())
			v.addElement(e.nextElement().toString());

		String[] a = new String[v.size()];
		v.copyInto(a);
		return a;
	}
}
