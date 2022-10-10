package nmania.ui.ng;

public interface IListSelectHandler {
	void OnSelect(ListItem item, ListScreen screen, IDisplay display);
	void OnSide(int direction, ListItem item, ListScreen screen, IDisplay display);
}
