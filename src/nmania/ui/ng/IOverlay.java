package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public interface IOverlay {
	String GetTitle();
	boolean CanDismiss();
	void Paint(Graphics g, int w, int h);
	void OnKey(IDisplay d, int k);
	void OnEnter();
	void OnExit();
}
