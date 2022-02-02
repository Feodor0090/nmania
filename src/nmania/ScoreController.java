package nmania;

public final class ScoreController {
	public final int[] hits = new int[6];
	public int maxHitScore;
	public int currentHitScore;
	public int maxCombo;
	public int currentCombo;

	private static final int[] scores = new int[] { 0, 50, 100, 200, 300, 305 };

	public final void CountHit(int type) {
		hits[type]++;
		if (type == 0) {
			if (currentCombo > maxCombo)
				maxCombo = currentCombo;
			currentCombo = 0;
		} else
			currentCombo++;
		maxHitScore += scores[5];
		currentHitScore += scores[type];
	}

	public final int GetAccuracy() {
		if (maxHitScore == 0)
			return 10000;
		if(currentHitScore<200000)
			return currentHitScore * 10000 / maxHitScore;
		return (int) ((long)currentHitScore * 10000l / (long)maxHitScore);
	}

	public void Reset() {
		for (int i = 0; i < hits.length; i++) {
			hits[i] = 0;
		}
		maxHitScore = 0;
		currentHitScore = 0;
		maxCombo = 0;
		currentCombo = 0;
	}
}