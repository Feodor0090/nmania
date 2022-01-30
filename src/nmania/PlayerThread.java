package nmania;

public final class PlayerThread extends Thread {

	public PlayerThread(Player pl) {
		player = pl;
	}

	public static int fps;
	private static int frames;
	private static long lastTime;

	final Player player;

	public void run() {
		lastTime = System.currentTimeMillis();
		player.Refill();
		if (!player.track.Play())
			throw new RuntimeException("Failed to start music!");
		while (true) {
			long t0 = System.currentTimeMillis();
			if (t0 - lastTime > 1000) {
				lastTime += 1000;
				fps = frames;
				frames = 0;
			}
			player.Update();
			player.Redraw();
			frames++;
		}
	}
}
