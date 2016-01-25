import java.util.Arrays;

public class Sentence {

	private String _Text;
	private int _ID;
//	private String[] _Tags;
//	private String[] _Words;
	private String[] _Chunks;
	
	public Sentence (int id, String text, String[] chunks) {
		// Constructor;
		_Text = text;
		_ID = id;
		_Chunks = chunks.clone();
		//_Words = words.clone();
		//_Tags = tags.clone();
		
	}
	
	public String toString() {
		return Arrays.toString(_Chunks);
		//return new String(_ID+"|"+_Text);
		
	}
	
	public int getID() {
		return _ID;
	}
	
	
}
