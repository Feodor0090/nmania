package nmania;

public final class ScoreController {
	public final int[] hits = new int[6];
	public int realTicks;
	public int resettableTicks;
	public int maxHitScore;
	public int currentHitScore;
	public int maxCombo;
	public int currentCombo;
	public final char[] currentAcc = new char[] { '1', '0', '0', ',', '0', '0', '%' };

	private static final int[] scores = new int[] { 0, 50, 100, 200, 300, 305 };

	public final void CountHit(int type) {
		hits[type]++;
		if (type == 0) {
			if (currentCombo > maxCombo)
				maxCombo = currentCombo;
			currentCombo = 0;
			resettableTicks = 0;
		} else
			currentCombo++;
		maxHitScore += scores[5];
		currentHitScore += scores[type];

		// acc update
		// copy-pasted right here to save method call
		int accRaw = (currentHitScore < 200000) ? (currentHitScore * 10000 / maxHitScore)
				: (int) (currentHitScore * 10000l / maxHitScore);
		currentAcc[5] = (char) (accRaw % 10 + '0');
		accRaw /= 10;
		currentAcc[4] = (char) (accRaw % 10 + '0');
		accRaw /= 10;
		currentAcc[2] = (char) (accRaw % 10 + '0');
		accRaw /= 10;
		currentAcc[1] = (char) (accRaw % 10 + '0');
		accRaw /= 10;
		currentAcc[0] = accRaw == 0 ? ' ' : '1';
	}

	public final void CountTick() {
		realTicks++;
		resettableTicks++;
	}

	public final int GetAccuracy() {
		if (maxHitScore == 0)
			return 10000;
		if (currentHitScore < 200000)
			return currentHitScore * 10000 / maxHitScore;
		return (int) (currentHitScore * 10000l / maxHitScore);
	}

	public final int GetGameplayCombo() {
		return currentCombo + resettableTicks;
	}

	public final void Reset() {
		for (int i = 0; i < hits.length; i++) {
			hits[i] = 0;
		}
		maxHitScore = 0;
		currentHitScore = 0;
		maxCombo = 0;
		currentCombo = 0;
		realTicks = 0;
		resettableTicks = 0;
	}
}