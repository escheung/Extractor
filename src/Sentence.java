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
	
	public static String delStuffBtwSingleQuote(String text) {
		String line = text;
		line = line.replaceAll("'.*?'", "");
		return line;
	}
	
	public static String getStuffBtwSingleQuote(String text) {
		int i1 = text.indexOf('\'');
		int i2 = text.indexOf('\'',i1+1);
		if (i1>0 && i2>i1) {
			return text.substring(i1+1,  i2);
		}
		return "";
	}
	
	public static String delStuffBtwCommas(String text) {
		String line = text;
		// Remove items between commas;
		line = line.replaceAll(",.*?,","");
		return line;
	}
	
	public static String getStuffBtwCommas(String text) {
		int i1 = text.indexOf(',');
		int i2 = text.indexOf(',',i1+1);
		if (i1>0 && i2>i1) {
			return text.substring(i1+1, i2);
		}
		return "";
	}
	
	public static String getNounOnly(String[] words, String[] tags) {
		// Returns the first consecutive nouns, stops when first non-noun detected.
		StringBuilder noun = new StringBuilder();
		//for (int i=0; i<words.length; i++) {
		int index=0;
		while (index < words.length) {
			if (tags[index].matches("^NN.*")) {
				if (index>0) noun.append(' ');
				noun.append(words[index]);
			} else {
				break;
			}
			index++;
		}
		return noun.toString();
	}
	
	public static Triple fsm_Plays_For(String[] words, String[] tags) {
		// This FSM tries to find the Plays-For pattern.
		Triple triple = null;
		String subject = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].matches("^plays|played$")) {
					state = 1;	// found the word "plays"; go to state 1.
				} else {
					state = 0;	// otherwise; stays here.
				}
				break;
			case 1:
				if (words[index].equalsIgnoreCase("for")) {
					state = 2;	// found the word "for"; go to state 2;
				} else {
					state = 4;
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*|JJ.*$")) {	// found a noun or adjective
					subject = subject.concat(words[index]);	// add noun to object.
					state = 3;	// go to state 3;
				} else {
					state = 4;	// unexpected word. go to state 4;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					subject = subject.concat(" " + words[index]);	// add noun to object.
					state = 3;	// found a noun, add to object;
				} else {
					state = 4;	// no more noun. go to state 4;
				}
				break;
			default:
				break;
			}
			
			index++;
		}
		
		if (!subject.isEmpty()) {
			triple = new Triple(subject,Triple.IS_A,"football team");
		}
		
		return triple;
	}
	
	public static Vector<Triple> fsm_As_A(String subject, String[] words, String[] tags) {
		
		// This FSM tries to find the "As-A" position pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;
		int index = 0;
		boolean found = false;	// flag for finding an object.
		//if (subject == null) return triples;	// return empty triple vector;
		//if (subject.isEmpty()) return triples;	// return empty triple vector;
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].equalsIgnoreCase("as")) {
					// found "as"; go to state 1;
					state = 1;
				} else {
					// stay in state 0;
					state = 0;
				}
				break;
			case 1:
				if (words[index].matches("^(a|an|the)$")) {
					// found "a/an/the"; go to state 2;
					state = 2;
				} else {
					// word not expected; go to end state;
					state = 6;
				}
				break;
			case 2:
				if (tags[index].matches("^JJ.*")) {
					// found adjective; go to state 3;
					state = 3;
				} else if (tags[index].matches("^NN.*")) {
					// found an noun;
					object = object.concat(words[index]);	// add word to object string.
					state = 4;
				} else {
					// word not expected; go to end state;
					state = 6;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					// found noun
					object = object.concat(words[index]);	// add word to object string.
					state = 4;
				} else {
					// unexpected word; go to end state;
					state = 6;
				}
				break;
			case 4:
				if (tags[index].matches("^NN.*")) {
					// found noun
					object = object.concat(" "+words[index]);	// add word to object string.
					state = 4;	// stay in state 4.
				} else if (words[index].matches("^(and|or)$")) {
					found = true;
					state = 5;
				} else {
					found = true;
					state = 6;
				}
				break;
			case 5:
				if (words[index].matches("^(a|an|the)$")) {
					// found "a/an/the"; go to state 2;
					state = 2;
				} else {
					// word not expected; go to end state;
					state = 6;
				}
				break;
				
			default:
				break;
			}
			
			index++;
			// if found = true; store triple.
			if (found || (index>=words.length && !object.isEmpty())) {
				// add triple to vector
				triples.add(new Triple(subject,Triple.IS_A,object));
				object= "";	// reset subject.
				found = false;	// reset flag.
			}
		}
		return triples;
	}
	
	public static Vector<Triple> fsm_Known_As(String subject, String[] words, String[] tags) {
		// This FSM tries to find the "Known-As" pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		boolean found = false;	// flag for finidng an object.
		if (subject == null) return triples;	// return empty triple vector;
		if (subject.isEmpty()) return triples;	// return empty triple vector;
		
		while (index < words.length) {
			switch (state) {
			case 0:	// init/adverb state;
				if (tags[index].matches("^RB.*")) {
					state = 0;	// found adverb ; go to next state;
				} else if (words[index].equals("known")) {
					state = 1;	// found "known"; go to state 8;
				} else {
					state = 7;	// unexpected word; go to end state;
				}
				break;
			case 1:	// found known;
				if (words[index].equals("as")) {
					state = 2;	// found "as"; go to state 2;
				} else {
					state = 7;	// unexpected word; go to end state;
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*")) {
					// found Noun;
					object = object.concat(words[index]);	// add word to object string.
					state = 5;	// go to state 5;
				} else if (words[index].matches("^(a|an|the)$")) {
					// found "a,an,the";
					state = 3;
				} else {
					// unexpected word; go to end state;
					state = 7;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					// found Noun;
					object = object.concat(words[index]);	// add word to object string.
					state = 5;	// go to state 5;
				} else if (tags[index].matches("^JJ.*")) {
					// found adjective.
					state = 4;
				} else {
					state = 7;
				};
				break;
			case 4:
				if (tags[index].matches("^NN.*")) {
					// found Noun;
					object = object.concat(words[index]);	// add word to object string.
					state = 5;
				} else {
					// unexpected word;
					state = 7;
				}
				break;
			case 5:
				if (tags[index].matches("^NN.*") || words[index].equalsIgnoreCase("do")) {
					// found Noun;
					object = object.concat(" "+words[index]);	// add word to object string.
					state = 5;	// stay in state 5;
				} else if (words[index].matches("^(or|and)$")) {
					// found 'or'; flag to store triple.
					found = true;
					state = 6;
				} else {
					// unexpected.
					// flag to store triple with what we have in object.
					found = true;
					state = 7;
				}
				break;
			case 6:
				if (tags[index].matches("^NN.*")) {
					// found another noun;
					object = object.concat(words[index]);	// add word to object string.
					state = 5;	// go to state 5;
				} else if (tags[index].matches("^RB.*")) {
					// found adverb
					state = 6;	// stay in state 6; 
				} else {
					// unexpected.
					state = 7;
				}
				break;
			case 7:
				// end of FSM.
				break;
			default:
				break;
			}
			
			index ++;
			// if found = true; store triple.
			if (found || (index>=words.length && !object.isEmpty())) {
				// add triple to vector
				triples.add(new Triple(subject,Triple.SAME_AS,object));
				object = "";	// reset object.
				found = false;	// reset flag.
			}
		}
		
		return triples;
	}
	


	
	public static Triple fsm_Is_A(String[] words, String[] tags) {
		// This FSM tries to find the IS-A pattern.
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
			index++;	// increment index;
		}
		
		if (success) triple = new Triple(subject,Triple.IS_A,object);
		return triple;
	}


}
