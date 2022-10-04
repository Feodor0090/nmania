package nmania;

import java.util.Date;

public interface IScore {
	int GetPerfects();
	int GetGreats();
	int GetGoods();
	int GetOks();
	int GetMehs();
	int GetMissed();
	int GetTicks();
	String GetAccuracy();
	long GetScore();
	long GetCombo();
	boolean IsFC();
	Date PlayedAt();
	String GetPlayerName();
}
