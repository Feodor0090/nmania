package nmania.ui.ng;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

public interface IDisplay {
	void Back();
	void Push(IScreen s);
	void CloseOverlay();
	void OpenOverlay(IOverlay o);
	void SetBg(Image bg);
	void SetBg(int color);
	Displayable GetDisplayable();
	void PauseRendering();
}
