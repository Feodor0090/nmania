package nmania; // ?dbg

import java.io.OutputStream; // ?dbg

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class GL { // ?dbg

	private static FileConnection fc;// ?dbg
	private static OutputStream stream;// ?dbg

	public static void Create() {// ?dbg
		if (stream != null)// ?dbg
			return;// ?dbg
		try {// ?dbg
			fc = (FileConnection) Connector// ?dbg
					.open("file:///" + Settings.workingFolder + "log-" + System.currentTimeMillis() + ".log");// ?dbg
			if (!fc.exists())// ?dbg
				fc.create();// ?dbg
			else// ?dbg
				fc.truncate(0);// ?dbg
			stream = fc.openOutputStream();// ?dbg
			Log("Starting log for nmania session...");// ?dbg
			Log("Running " + Nmania.version + " on " + System.getProperty("microedition.platform"));// ?dbg
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
		Log("(stats) Memory: T=" + total + " F=" + free + " U=" + (total - free) + "; Threads: "
				+ Thread.activeCount());// ?dbg
	}// ?dbg
} // ?dbg
