import java.util.Arrays;

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
		StringBuilder sb = new StringBuilder();
		sb.append('\n'+_Text+'\n');
		sb.append("Words\n");
		sb.append(Arrays.toString(_Words));
		sb.append("\nTags\n");
		sb.append(Arrays.toString(_Tags));
		return sb.toString();
	}
	
	public int getID() {
		return _ID;
	}
	
	public static Triple fsm_Is_A(String[] words, String[] tags) {
		Triple triple = null;
		boolean success = false;
		String subject = "";
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		while (index < words.length) {
			switch (state) {
				case 0:
					if (tags[index].matches("^NN.*")) {
						subject = subject.concat(words[index]);
						state = 1;	// found subject/anchor term.
					};
					break;
				case 1:
					if (tags[index].matches("^NN.*")) {
						subject = subject.concat(" " + words[index]);
						state = 1;	// found another NN* word, stay in state 1.
					} else if (words[index].matches("^(is|are)$")) {
						state = 2;	// found is/are; move to state 2.
					} else {
						state = 7;	// does not follow IS-A pattern; go to end state.
					}
					break;
				case 2:
					if (tags[index].matches("^RB.*")) {	// adverb
						state = 3;	// found an adverb; go to state 3;
					} else if (words[index].matches("^(a|an|the)$")) {
						state = 4;	// found a/an/the; move to state 4.
					} else {
						state = 7;	// does not follow IS-A pattern; go to end state.
					}
					break;
				case 3:
					if (words[index].matches("^(a|an|the)$")) {
						state = 4;	// found a/an/the; move to state 4.
					} else {
						state = 7;	// go to end state.
					}
					break;
				case 4:
					if (tags[index].matches("^JJ.*")) {
						state = 5;	// found adjective; move to state 5.
					} else if (tags[index].matches("^NN.*")) {
						object = object.concat(words[index]);
						state = 6;
					} else {
						state = 7;	// go to end state.
					}
					break;
				case 5:
					if (tags[index].matches("^NN.*")) {
						object = object.concat(words[index]);
						state = 6;
					} else {
						state = 7;	// go to end state.
					}
					break;
				case 6:
					if (tags[index].matches("^NN.*")) {
						object = object.concat(" "+words[index]);
						state = 6;
					} else {
						state = 7;	// go to end state.
					}
					break;
				case 7:
					// End of FSM
					break;
				default: 
					break;
			}
			success = !subject.isEmpty() && !object.isEmpty();
//			System.out.println(String.format("%d:%s:%s:(word:%s|tag:%s)", state,subject,object,words[index],tags[index]));
			index++;	// increment index;
		}
		
		System.out.println(String.format("Success:%b; %s is-a %s",success,subject,object));
		if (success) triple = new Triple(subject,Triple.IS_A,object);
		return triple;
	}
}
