package nmania.ui.ng;

public class AboutScreen extends Alert {

	public AboutScreen() {
		super("About nmania", "Cute and fast \n VSRG for J2ME, \n osu!mania clone! \n "
				+ "Made by sym_ansel & Shinovon. \n Source code: github.com/Feodor0090/nmania "
				+ "\n TG chat: t.me/nnmidletschat \n Visit nnp.nnchan.ru for other cool apps! \n "
				+ "Visit our board: nnchan.ru/rg/");
	}
	
	public boolean ShowLogo() {
		return true;
	}

}
