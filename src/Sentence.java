import java.util.Arrays;

public class Sentence {

	private String _Text;
	private int _ID;
	private String[] _Tags;
	private String[] _Words;
	private String[] _Chunks;
	
	public Sentence (int id, String text, String[] words, String[] tags, String[] chunks) {
		// Constructor;
		_Text = text;
		_ID = id;
		_Chunks = chunks.clone();
		_Words = words.clone();
		_Tags = tags.clone();
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_Text);
		sb.append('\n');
		sb.append(Arrays.toString(_Words));
		sb.append('\n');
		sb.append(Arrays.toString(_Tags));
		sb.append('\n');
		sb.append(Arrays.toString(_Chunks));
		
		return sb.toString();
		//return new String(_ID+"|"+_Text);
		
	}
	
	public int getID() {
		return _ID;
	}
	
}
