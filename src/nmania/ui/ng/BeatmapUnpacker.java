package nmania.ui.ng;

import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import nmania.GL;
import nmania.Settings;
import zip.ZipEntry;
import zip.ZipInputStream;

public class BeatmapUnpacker extends Alert implements Runnable {
	private String file;
	private boolean wip = true;

	public BeatmapUnpacker(String file) {
		super(file, "Please wait until OSZ is extracted... This can't be cancelled.");
		this.file = file;
	}

	public boolean OnExit(IDisplay d) {
		return wip;
	}

	public void OnEnter(IDisplay d) {
		Thread t = new Thread(this, "OSZ unpacker");
		t.start();
	}

	public void run() {
		GL.Log("(zip) Going to unpack " + file);
		GL.LogStats();
		FileConnection fc = null;
		String dir = "file:///" + Settings.workingFolder + file.substring(0, file.length() - 4) + "/";
		try {
			fc = (FileConnection) Connector.open(dir);
			fc.mkdir();
			fc.close();
			fc = (FileConnection) Connector.open("file:///" + Settings.workingFolder + file);
			ZipInputStream zip = new ZipInputStream(fc.openInputStream());
			while (true) {
				ZipEntry zi = zip.getNextEntry();
				if (zi == null)
					break;
				title = "Processing " + zi.getName();
				FileConnection nfc = (FileConnection) Connector.open(dir + zi.getName());
				try {
					if (zi.isDirectory())
						nfc.mkdir();
					else {
						nfc.create();
						OutputStream os = nfc.openOutputStream();
						byte[] buf = new byte[1024];
						int read;
						while ((read = zip.read(buf)) != -1) {
							os.write(buf, 0, read);
						}
						os.flush();
					}
				} finally {
					nfc.close();
				}
			}

			fc.delete();
			wip = false;
			title = "OSZ was unpacked.";
			SetText("Done. You can leave this view.");
		} catch (Throwable e) {
			wip = false;
			title = "Failure: " + e.toString();
			SetText("Could not finish. Please try to unpack manually using external file manager.");
			e.printStackTrace();
			GL.Log("(zip) " + e.toString());
		} finally {
			try {
				fc.close();
			} catch (Exception e) {
			}
		}
	}
}
