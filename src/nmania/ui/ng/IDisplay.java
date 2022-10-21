package nmania.ui.ng;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import nmania.Beatmap;

public interface IDisplay {
	void Back();
	void Push(IScreen s);
	/**
	 * Sets background image.
	 * @param bg Any image to use. Null to reset.
	 */
	void SetBg(Image bg);
	Image GetBg();
	void SetAudio(Beatmap bm);
	Displayable GetDisplayable();
	void PauseRendering();
	void ResumeRendering();
	void Destroy();
	void Throttle(boolean enable);
}
