package nmania;

public final class PlayerThread extends Thread {
	
	public PlayerThread(Player pl) {
		player = pl;
	}
	
	final Player player;
	
	public void run() {
		player.Refill();
		player.track.Play();
		while(true) {
			player.Update();
			player.Redraw();
		}
	}
}
