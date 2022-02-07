package nmania;

import javax.microedition.lcdui.Image;

public class RichSkin {
	public RichSkin(String folder) {
		
	}
	
	public Image key1;
	public Image key2;
	public Image key3;
	public Image note1;
	public Image note2;
	public Image note3;
	public Image shadow1;
	public Image shadow2;
	public Image shadow3;
	public final Image[] digits = new Image[12]; //0123456789,%r
	public final Image[] judgments = new Image[6];
	
	public final void Check() throws IllegalStateException {
		
	}
	public int GetColumnWidth() {
		return key1.getWidth();
	}

	public int GetNoteHeight() {
		return note1.getHeight();
	}

	public int GetKeyboardHeight() {
		return key1.getHeight();
	}
}
