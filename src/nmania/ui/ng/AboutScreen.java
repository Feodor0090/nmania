package nmania.ui.ng;

public class AboutScreen extends Alert {

	public AboutScreen() {
		super("About nmania", "Cute and fast \n VSRG for J2ME, \n osu!mania clone! \n "
				+ "Made by sym_ansel. Special thanks: \n "
				+ "- Shinovon: gave a PC for development, libraries, replays "
				+ "encoding code, proxy, server-side components \n "
				+ "- vipaoL: helped me set up CI pipeline \n "
				+ "- tube42: imagelib \n "
				+ "- Julien Ponge: LZMA implementation for java \n "
				+ "- Symbian World chat: testing & AD \n "
				+ "- Dean Herbert: osu! creator \n "
				+ "- Lantis: awesome J-POP music for coding \n "
				+ "Source code: github.com/Feodor0090/nmania \n "
				+ "TG chat: t.me/nnmidletschat \n Visit nnp.nnchan.ru for other cool apps! \n "
				+ "Visit our board: nnchan.ru/rg/ \n "
				+ "And always remember: don't let Maho Himemiya fool you!");
	}
	
	public boolean ShowLogo() {
		return true;
	}

}
