package nmania;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class GL { // ?dbg

	private static FileConnection fc;// ?dbg
	private static OutputStream stream;// ?dbg

	public static void Create(boolean force) {// ?dbg
		if (stream != null)// ?dbg
		{ // ?dbg
			if (!force) // ?dbg
				return; // ?dbg
			try { // ?dbg
				stream.close(); // ?dbg
				fc.close(); // ?dbg
			} catch (IOException e) { // ?dbg
			} // ?dbg
		} // ?dbg
		try {// ?dbg
			fc = (FileConnection) Connector// ?dbg
					.open("file:///" + Settings.workingFolder + "log-" + System.currentTimeMillis() + ".log");// ?dbg
			if (!fc.exists())// ?dbg
				fc.create();// ?dbg
			else// ?dbg
				fc.truncate(0);// ?dbg
			stream = fc.openOutputStream();// ?dbg
			Log("(app) Starting log for nmania session...");// ?dbg
			Log("(app) Running " + Nmania.version + " on " + System.getProperty("microedition.platform"));// ?dbg
		} catch (Exception e) {// ?dbg
		} // ?dbg
	}// ?dbg

	public static void Log(String s) { // ?dbg
		try {// ?dbg
			System.out.println(s); // ?dbg
			stream.write(s.getBytes());// ?dbg
			stream.write('\n');// ?dbg
			stream.flush();// ?dbg
		} catch (Exception e) {// ?dbg
		} // ?dbg
	}// ?dbg

	public static void LogStats() {// ?dbg
		long total = Runtime.getRuntime().totalMemory() / 1024;// ?dbg
		long free = Runtime.getRuntime().freeMemory() / 1024;// ?dbg
		Log("(app) Memory: T=" + total + " F=" + free + " U=" + (total - free) + "; Threads: " // ?dbg
				+ Thread.activeCount());// ?dbg
	}// ?dbg
} // ?dbg
