package nmania;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public final class NmaniaApp extends MIDlet {

	public static NmaniaApp inst;
	
	public NmaniaApp() {
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		inst = this;

	}

}
