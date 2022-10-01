package nmania;

/**
 * Model for folder with several beatmaps. All the data will be read from a random one.
 * 
 * @author Feodor0090
 *
 */
public final class BeatmapSet {
	public int id;
	public String title;
	public String artist;
	public String mapper;
	public String image;
	public String wdPath;
	public String folderName;
	public String[] files;

	public boolean hasFile(String eqName) {
		for (int i = 0; i < files.length; i++) {
			if (eqName.endsWith(files[i]))
				return true;
		}
		return false;
	}

	/**
	 * Will find the first file in BMS folder, that begins with the given
	 * prefix.<br>
	 * <br>
	 * For example, there are files "hit1.mp3", "fail-section.vaw", "hit1.wav" and
	 * "fail.wav".<br>
	 * "hit1" will find "hit1.mp3", "fail" "fail-section.vaw", "fail."
	 * "fail.wav".<br>
	 * Can be used to load hitsounds.
	 * 
	 * @param prefix Beginning of the file's name.
	 * @return Full file name, null if nothing is found.
	 */
	public String findFile(String prefix) {
		for (int i = 0; i < files.length; i++) {
			if (files[i].startsWith(prefix))
				return files[i];
		}
		return null;
	}
}
