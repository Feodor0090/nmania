package nmania;

public final class PlayerThread extends Thread {

	public PlayerThread(Player pl) {
		super("Player update");
		player = pl;
	}

	final Player player;

	public void run() {
		GL.Log("(player) Thread entered!");
		try {
			if (Settings.maxPriority)
				this.setPriority(MAX_PRIORITY);
			player.Refill();
			player.track.Play();
			GL.Log("(player) Loop entered!");
			player.Loop();
		} catch (Exception e) {
			GL.Log("(player) Player thread crashed!");
			GL.Log("(player) " + e.toString());
			e.printStackTrace();
		}
	}
}
