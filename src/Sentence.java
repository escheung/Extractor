
public class Sentence {

	private String _Text;
	private int _ID;
	private String[] _Tags;
	private String[] _Words;
	
	public Sentence (int id, String text, String[] words, String[] tags) {
		// Constructor;
		_Text = text;
		_ID = id;
		_Words = words.clone();
		_Tags = tags.clone();
		
	}
	
	public String toString() {
		return new String(_ID+"|"+_Text);
		
	}
	
	public int getID() {
		return _ID;
	}
	
	
}
