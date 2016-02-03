import java.util.Vector;


import java.util.Iterator;

public class Document {

	private int _ID;
	private Vector<Sentence> _Sentences;
	
	public Document () {
		// Constructor;
		_ID = 0;
		_Sentences = new Vector<Sentence>();
	}
	
	public Document (int id) {
		// Constructor
		_ID = id;
		_Sentences = new Vector<Sentence>();
	}
	

	public boolean addSentence(Sentence sentence) {
		return _Sentences.add(sentence);
	}
	
	public void process() {

	}

	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Document ID:%d: Sentences:%d\n", this._ID, _Sentences.size()));
		Iterator<Sentence> sit = _Sentences.iterator();
		while (sit.hasNext()){
			sb.append(String.format("%s\n", sit.next().toString()));	
		}
		return sb.toString();
	}
	public int getID() {
		return _ID;
	}
	public int getSentencesSize() {
		return _Sentences.size();
	}
	
	public static String preprocess(String text) {
		String line;
		// Replace the LINE_SEPERATOR with " "
		line = text.replaceAll(Engine.LINE_SEPERATOR, " ");
		// Remove items in round brackets;
		line = line.replaceAll("\\(.*?\\)","");
		return line;
	}
}
