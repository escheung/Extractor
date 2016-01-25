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
	
	public Document (int id, Vector<Sentence> sents) {
		_ID = id;
		_Sentences = (Vector<Sentence>) sents.clone();	// a shallow copy.
	}
	
	public void process() {

	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Document ID:%d: Sentences:%d", this._ID, _Sentences.size()));
		return sb.toString();
	}
	public int getID() {
		return _ID;
	}
	public int getSentencesSize() {
		return _Sentences.size();
	}
	
	
}
