package nmania.ui.ng;

import javax.microedition.lcdui.Graphics;

public abstract class Screen {
	public abstract String GetTitle();

	public boolean ShowLogo() {
		return true;
	}

	public abstract String GetOption();

	public void OnOptionActivate(IDisplay d) {
	}

	public abstract void Paint(Graphics g, int w, int h);

	/**
	 * Called on keyboard event from display.
	 * 
	 * @param d Display where operation was performed.
	 * @param k Key code.
	 */
	public abstract void OnKey(IDisplay d, int k);

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
	public abstract void OnTouch(IDisplay d, int s, int x, int y, int w, int h);

	/**
	 * Called on entering the screen.
	 * 
	 * @param d Display where operation was performed.
	 */
	public void OnEnter(IDisplay d) {
	}

	/**
	 * Called on exiting the screen.
	 * 
	 * @param d Display where operation was performed.
	 * @return Return true to block operation.
	 */
	public boolean OnExit(IDisplay d) {
		return false;
	}

	/**
	 * Called when a screen entered on top of this one.
	 * 
	 * @param d Display where operation was performed.
	 */
	public void OnPause(IDisplay d) {
	}

	/**
	 * Called when screen on top of this one was exited and this screen is active
	 * again.
	 * 
	 * @param d Display where operation was performed.
	 */
	public void OnResume(IDisplay d) {
	}
}