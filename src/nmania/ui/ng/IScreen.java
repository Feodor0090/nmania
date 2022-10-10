package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public interface IScreen {
	String GetTitle();
	String GetOption();
	void OnOptionActivate(IDisplay d);
	void Paint(Graphics g, int w, int h);
	void OnKey(IDisplay d, int k);
	void OnEnter(IDisplay d);
	void OnExit(IDisplay d);
	void OnPause(IDisplay d);
	void OnResume(IDisplay d);
}
