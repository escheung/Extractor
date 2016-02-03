import java.util.Arrays;
import java.util.Vector;

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
	
	public static String stripStuffInCommas(String text) {
		String line = text;
		// Remove items between commas;
		line = line.replaceAll(",.*?,","");
		return line;
	}
	
	public static Vector<Triple> fsm_Is_A(String[] words, String[] tags) {
		Vector<Triple> triples = new Vector<Triple>();
		//Triple triple = null;
		//boolean success = false;
		
		String subject = "";
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		boolean found = false;	// flag for finding a triple.
		while (index < words.length) {
			switch (state) {
				case 0:
					if (tags[index].matches("^NN.*")) {
						subject = subject.concat(words[index]);
						state = 1;	// found subject/anchor term.
					};
					break;
				case 1:
					if (tags[index].matches("^NN.*") || words[index].equalsIgnoreCase("do")) {
						// found another NN* word or a "do" , stay in state 1.
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
					if (tags[index].matches("^JJ.*")||tags[index].matches("^VB.*")) {
						state = 5;	// found adjective or verb; move to state 5.
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
					} else if (tags[index].matches("^JJ.*")||tags[index].matches("^VB.*")) {
						// found more adjective or verg; stay in state 5.
						state = 5;
					} else {
						state = 7;	// go to end state.
					}
					break;
				case 6:
					if (tags[index].matches("^NN.*")) {
						object = object.concat(" "+words[index]);
						state = 6;
					} else {
						found = true;
						state = 7;	// go to end state.
					}
					break;
				case 7:
					// End of FSM
					break;
				default: 
					break;
			}


			// if found both subject and object
			if (found && !subject.isEmpty() && !object.isEmpty()) {
				// add new triple
				triples.add(new Triple(subject,Triple.IS_A,object));
				// reset object; keep subject/anchor;
				object = "";
				// reset flag "found"
				found = false;
			}
			
			index++;	// increment index;
		}
		
		
		return triples;
	}
/*	
	public static Triple fsm_Is_A_Backup(String[] words, String[] tags) {
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
					if (tags[index].matches("^NN.*") || words[index].equalsIgnoreCase("do")) {
						// found another NN* word or a "do" , stay in state 1.
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
					if (tags[index].matches("^JJ.*")||tags[index].matches("^VB.*")) {
						state = 5;	// found adjective or verb; move to state 5.
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
					} else if (tags[index].matches("^JJ.*")||tags[index].matches("^VB.*")) {
						// found more adjective or verg; stay in state 5.
						state = 5;
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
*/
}
