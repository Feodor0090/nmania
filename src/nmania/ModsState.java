package nmania;

public class ModsState {

	public ModsState() {
		value = 0;
	}

	public ModsState(int mask) {
		value = mask;
	}

	public ModsState(int da, int fa) {
		value = 0;
		SetDA(da);
		SetFA(fa);
	}

	private int value;

	/**
	 * <ul>
	 * <li>-1 - Easy
	 * <li>0 - default
	 * <li>1 - Hard Rock
	 * </ul>
	 */
	public int GetDA() {
		if ((value & 2) != 0)
			return -1;
		if ((value & 16) != 0)
			return 1;
		return 0;
	}

	/**
	 * <ul>
	 * <li>-1 - Easy
	 * <li>0 - default
	 * <li>1 - Hard Rock
	 * </ul>
	 */
	public void SetDA(int state) {
		value = value & (~(2 | 16));
		if (state < 0)
			value = value | 2;
		if (state > 0)
			value = value | 16;
	}

	/**
	 * <ul>
	 * <li>-1 - No Fail
	 * <li>0 - default
	 * <li>1 - Sudden Death
	 * <li>2 - Perfect
	 * </ul>
	 */
	public int GetFA() {
		if ((value & 1) != 0)
			return -1;
		if ((value & 32) != 0)
			return 1;
		if ((value & 16384) != 0)
			return 2;
		return 0;
	}

	/**
	 * <ul>
	 * <li>-1 - No Fail
	 * <li>0 - default
	 * <li>1 - Sudden Death
	 * <li>2 - Perfect
	 * </ul>
	 */
	public void SetFA(int state) {
		value = value & (~(1 | 32 | 16384));
		if (state < 0)
			value = value | 1;
		if (state == 1)
			value = value | 32;
		if (state == 2)
			value = value | 16384;
	}
}
