package nmania;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public final class Sample {
	
	public Sample(boolean local, String file, String type) throws IOException, MediaException {
		if(local) {
			player = Manager.createPlayer(getClass().getResourceAsStream(file), type);
		} else {
			throw new IllegalArgumentException("Remote samples are not supported yet!");
		}
		player.realize();
		player.prefetch();
	}
	
	private final Player player;
	
	public final void Play() {
		try {
			player.stop();
			player.setMediaTime(0);
			player.start();
		} catch (MediaException e) {
		}
	}
	
	public final void Dispose() {
		player.deallocate();
		player.close();
	}
}
