package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public interface IScreen {
	String GetTitle();
	String GetOption();
	void OnOptionActivate();
	void Paint(Graphics g, int w, int h);
	void OnKey(IDisplay d, int k);
	void OnEnter();
	void OnExit();
	void OnPause();
	void OnResume();
}
