package nmania;

import java.util.Date;

public interface IScore {
	int GetPerfects();
	int GetGreats();
	int GetGoods();
	int GetOks();
	int GetMehs();
	int GetMisses();
	int GetTicks();
	int GetAccuracy();
	long GetScore();
	long GetCombo();
	boolean IsFC();
	Date PlayedAt();
	String GetPlayerName();
}
