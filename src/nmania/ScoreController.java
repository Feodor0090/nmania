package nmania;

final class ScoreController {
	public final int[] hits = new int[6];
	public int maxHitScore;
	public int currentHitScore;
	public int maxCombo;
	public int currentCombo;

	private static final int[] scores = new int[] { 0, 50, 100, 200, 300, 300 };

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
		return currentHitScore * 10000 / maxHitScore;
	}
}