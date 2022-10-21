package nmania;

public final class PlayerThread extends Thread {

	public PlayerThread(Player pl) {
		super("Player update");
		player = pl;
	}

	final Player player;

	public void run() {
		if (Settings.maxPriority)
			this.setPriority(MAX_PRIORITY);
		player.Refill();
		if (!player.track.Play())
			throw new RuntimeException("Failed to start music!");
		player.Loop();
	}
}
