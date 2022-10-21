package nmania.ui.ng;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

public interface IDisplay {
	void Back();
	void Push(IScreen s);
	void CloseOverlay();
	void OpenOverlay(IOverlay o);
	/**
	 * Sets background image.
	 * @param bg Any image to use. Null to reset.
	 */
	void SetBg(Image bg);
	Displayable GetDisplayable();
	void PauseRendering();
}
