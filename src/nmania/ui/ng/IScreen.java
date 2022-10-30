package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public interface IScreen {
	String GetTitle();

	boolean ShowLogo();

	String GetOption();

	void OnOptionActivate(IDisplay d);

	void Paint(Graphics g, int w, int h);

	/**
	 * Called on keyboard event from display.
	 * 
	 * @param d Display where operation was performed.
	 * @param k Key code.
	 */
	void OnKey(IDisplay d, int k);

	/**
	 * Called on touch event from display.
	 * 
	 * @param d Display where operation was performed.
	 * @param s Event type:
	 *          <ul>
	 *          <li>1 - down
	 *          <li>2 - drag
	 *          <li>3 - up
	 *          </ul>
	 * @param x X coord
	 * @param y Y coord
	 * @param w Display width
	 * @param h Display height
	 */
	void OnTouch(IDisplay d, int s, int x, int y, int w, int h);

	/**
	 * Called on entering the screen.
	 * 
	 * @param d Display where operation was performed.
	 */
	void OnEnter(IDisplay d);

	/**
	 * Called on exiting the screen.
	 * 
	 * @param d Display where operation was performed.
	 * @return Return true to block operation.
	 */
	boolean OnExit(IDisplay d);

	/**
	 * Called when a screen entered on top of this one.
	 * 
	 * @param d Display where operation was performed.
	 */
	void OnPause(IDisplay d);

	/**
	 * Called when screen on top of this one was exited and this screen is active
	 * again.
	 * 
	 * @param d Display where operation was performed.
	 */
	void OnResume(IDisplay d);
}
