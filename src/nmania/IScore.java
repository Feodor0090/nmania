package nmania;

public interface IScore extends IScoreData {
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
}
