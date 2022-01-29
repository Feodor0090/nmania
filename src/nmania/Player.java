package nmania;

import javax.microedition.lcdui.game.GameCanvas;

public class Player extends GameCanvas {

	protected Player(Beatmap map) {
		super(false);
	}
	


	final int columnsCount;
	final int[][] columns;
	final int[] currentNote;
	final boolean[] holdKeys;
	final int[] keyMappings;
	private final static int[] hitWindows = new int[0];
	
	int time;
	
	AudioController track;
	
	
	
	protected final void keyPressed(int k) {
		int column=-1;
		for(int i=0;i<columnsCount;i++)
			if(keyMappings[i]==k)
				column = i;
		if(column==-1) return;
		
	}
	
	final void Update() {
		time = track.Now();
	}
}
