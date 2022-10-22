package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public interface IScreen {
	String GetTitle();
	boolean ShowLogo();
	String GetOption();
	void OnOptionActivate(IDisplay d);
	void Paint(Graphics g, int w, int h);
	void OnKey(IDisplay d, int k);
	void OnEnter(IDisplay d);
	/**
	 * Called on exiting the screen.
	 * @param d Display where operation was performed.
	 * @return Return true to block operation.
	 */
	boolean OnExit(IDisplay d);
	void OnPause(IDisplay d);
	void OnResume(IDisplay d);
}
