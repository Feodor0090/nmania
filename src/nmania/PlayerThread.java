package nmania;

public final class PlayerThread extends Thread {

	public PlayerThread(Player pl) {
		player = pl;
	}

	final Player player;

	public void run() {
		player.Refill();
		if (!player.track.Play())
			throw new RuntimeException("Failed to start music!");
		while (true) {
			player.Update();
			player.Redraw();
		}
	}
}
