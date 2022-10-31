package nmania.ui.ng;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import nmania.AudioController;
import nmania.BeatmapSet;

public interface IDisplay {
	void Back();

	void Push(Screen s);

	/**
	 * Sets background image.
	 * 
	 * @param bg Any image to use. Null to reset.
	 */
	void SetBg(Image bg);

	Image GetBg();

	void SetAudio(BeatmapSet set);

	AudioController GetAudio();

	Canvas GetDisplayable();

	void PauseRendering();

	void ResumeRendering();

	void Destroy();

	void Throttle(boolean enable);
}
