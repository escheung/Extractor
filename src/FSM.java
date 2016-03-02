//import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FSM {
	// FSM is a static class that implements Finite-State-Machine.
	public static String IS_A = "is a";
	
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
	
	public static Map<String,Object> findBornIn(String subject, String[] words, String[] tags) {
		// This FSM tries to find the Born-In pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].matches("^born|Born$")) {
					state = 1;	// go to state 1.
				} else {
					state = 0;	// otherwise; stays here.
				}
				break;
			case 1:
				if (words[index].matches("^in$")) {
					state = 2;	// go to state 2.
				} else {
					state = 4;	// otherwise; go to end.
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*|JJ.*$")) {
					state = 3;	// go to state 2.
					object = object.concat(words[index]);	// add noun to object.
				} else if (tags[index].matches("^DT.*$")) {
					state = 2;	// stay here.
				} else {
					state = 4;	// go to end.
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*|JJ.*$")) {
					state = 3;
					object = object.concat(" "+words[index]);	// add noun to object.
				} else {
					state = 4;	// the end.
				}
				break;
			case 4:
				break;
			default:
				break;
			}
			index++;
		}
		
		if (!object.isEmpty() && !subject.isEmpty()) {
			triples.add(new Triple(object,FSM.IS_A,"location"));
			triples.add(new Triple(subject,"born in",object));
		};
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",object);
		m.put("triples",triples);
		return m;
	}
	
	public static Map<String,Object> findPlayedAgainst(String subject, String[] words, String[] tags) {
		// This FSM tries to find the Plays-Against pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].matches("^against$")) {
					state = 1;	// go to state 1.
				} else {
					state = 0;	// otherwise; stays here.
				}
				break;
			case 1:
				if (tags[index].matches("^NN.*|JJ.*$")) {
					state = 2;	// go to state 2.
					object = object.concat(words[index]);	// add noun to object.
				} else {
					state = 1;	// stay here.
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*|JJ.*$")) {
					state = 2;
					object = object.concat(" "+words[index]);	// add noun to object.
				} else {
					state = 3;	// the end.
				}
				break;
			case 3:
				break;
			default:
				break;
			}
			index++;
		}
		
		if (!object.isEmpty() && !subject.isEmpty()) {
			triples.add(new Triple(object,FSM.IS_A,"football_team"));
			triples.add(new Triple(object,FSM.IS_A,"organization"));
			triples.add(new Triple(subject,"played against",object));
		}
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",object);
		m.put("triples",triples);
		return m;
	}
	
	public static Map<String,Object> findPlaysFor(String subject, String[] words, String[] tags) {
		// This FSM tries to find the Plays-For pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].matches("^plays|playing|played$")) {
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
					object = object.concat(words[index]);	// add noun to object.
					state = 3;	// go to state 3;
				} else {
					state = 4;	// unexpected word. go to state 4;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					object = object.concat(" " + words[index]);	// add noun to object.
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
		
		if (!object.isEmpty() && !subject.isEmpty()) {
			triples.add(new Triple(object,FSM.IS_A,"football_team"));
			triples.add(new Triple(object,FSM.IS_A,"organization"));
			triples.add(new Triple(subject,"played for",object));
		}
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",object);
		m.put("triples",triples);
		return m;
		//return triples;
	}
	
	public static Map<String,Object> findStartedAt(String subject, String[] words, String[] tags) {
		// This FSM tries to find the Started-At pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].matches("started")) {
					state = 1;	// found the word "plays"; go to state 1.
				} else {
					state = 0;	// otherwise; stays here.
				}
				break;
			case 1:
				if (words[index].matches("^at|with$")) {
					state = 2;	// found the word "at|with"; go to state 2;
				} else {
					state = 1;	// stay here
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*$")) {	// found a noun or adjective
					object = object.concat(words[index]);	// add noun to object.
					state = 3;	// go to state 3;
				} else if (tags[index].matches("^DT.*$")) {	// determiner, such as "the/a".
					state = 2;	// stay here.
				} else {
					state = 4;	// unexpected word. go to state 4;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					object = object.concat(" " + words[index]);	// add noun to object.
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
		
		if (!object.isEmpty() && !subject.isEmpty()) {
			triples.add(new Triple(object,FSM.IS_A,"organization"));
			triples.add(new Triple(subject,"started at",object));
		}
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",object);
		m.put("triples",triples);
		return m;
	}
	
	public static Vector<Triple> findCityOf(String[] words, String[] tags) {
		// This FSM tries to find the "City Of" pattern to discover city names.
		Vector<Triple> triples = new Vector<Triple>();
		String subject = "";
		int state = 0;
		int index = 0;
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].equalsIgnoreCase("city")) {
					// found "city"; go to state 1;
					state = 1;
				} else {
					// nothing yet; stay here.
					state = 0;
				}
				break;
			case 1:
				if (words[index].equalsIgnoreCase("of")) {
					// found "of"; go to state 2;
					state = 2;
				} else {
					// broke pattern; go back to 0;
					state = 0;
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*")) {
					// found noun;
					subject = subject.concat(words[index]);
					state = 3;
				} else {
					// pattern broken; go to end state.
					state = 4;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					// found noun;
					subject = subject.concat(" "+words[index]);
					state = 3;	// stay here.
				} else {
					// pattern broken; go to end state.
					state = 4;
				}
				break;
			case 4:
				// end state;
				break;
			default:
				break;
			}
			
			index++;
			if (index >= words.length && !subject.isEmpty()) {
				// reached end of sentence and found subject.
				triples.add(new Triple(subject,FSM.IS_A,"city"));
				triples.add(new Triple(subject,FSM.IS_A,"location"));
			}
		}
		return triples;
	}

	public static Map<String, Object> findDebut(String subject, String[] words, String[] tags) {
		// This FSM tries to find the "debut at/in"  pattern.
				Vector<Triple> triples = new Vector<Triple>();
				String object = "";
				int state = 0;
				int index = 0;
				boolean found = false;	// flag for finding an object.
				
				while (index < words.length) {
					switch (state) {
					case 0:
						if (words[index].matches("^[D|d]ebut($|ed$)")) {
							state = 1;	// "debuted or debut"
						} else {
							state = 0;	// stay here.
						}
						break;
					case 1:
						if (words[index].equalsIgnoreCase("for")) {
							state = 2;	// "for"
						} else {
							state = 1;	// stay here.
						}
						break;
					case 2:
						if (tags[index].matches("^NN.*")) {
							// noun found
							object = object.concat(words[index]);	// add word to object string.
							found = true;
							state = 3;	// go to 3.
						} else if (tags[index].matches("^DT.*")) {	// if 
							state = 2;	// stay here
						} else {
							state = 4;
						}
						break;
					case 3:
						if (tags[index].matches("^NN.*")) {
							// noun
							object = object.concat(" "+words[index]); // add word to object string
							found = true;
							state = 3;
						} else {
							state = 4;	// end state.
						}
						break;
					case 4:	// end state
						break;	
					default:
						break;
					}
				
					index++;
				}
				
				if (found || (index>=words.length && !object.isEmpty())) {
					// add triple to vector
					triples.add(new Triple(subject,"debuted for",object));
				}
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("mention",object);
				m.put("triples",triples);
				return m;
	}
	
	public static Map<String, Object> findLivedIn(String subject, String[] words, String[] tags) {
		// This FSM tries to find the "lived in"  pattern.
				Vector<Triple> triples = new Vector<Triple>();
				String object = "";
				int state = 0;
				int index = 0;
				boolean found = false;	// flag for finding an object.
				
				while (index < words.length) {
					switch (state) {
					case 0:
						if (words[index].equalsIgnoreCase("lived")) {
							state = 1;	// "lived"
						} else {
							state = 0;	// stay here.
						}
						break;
					case 1:
						if (words[index].equalsIgnoreCase("in")) {
							state = 2;	// "in"
						} else {
							state = 1;	// stay here.
						}
						break;
					case 2:
						if (tags[index].matches("^NN.*")) {
							// noun found
							object = object.concat(words[index]);	// add word to object string.
							found = true;
							state = 3;	// go to 3.
						} else if (tags[index].matches("^DT.*")) {	// if 
							state = 2;	// stay here
						} else {
							state = 4;
						}
						break;
					case 3:
						if (tags[index].matches("^NN.*")) {
							// noun
							object = object.concat(" "+words[index]); // add word to object string
							found = true;
							state = 3;
						} else {
							state = 4;	// end state.
						}
						break;
					case 4:	// end state
						break;	
					default:
						break;
					}
				
					index++;
				}
				
				if (found || (index>=words.length && !object.isEmpty())) {
					// add triple to vector
					triples.add(new Triple(subject,"lived in",object));
				}
				
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("mention",object);
				m.put("triples",triples);
				return m;
		
	}
	
	public static Map<String, Object> findFoundedBy(String subject, String[] words, String[] tags) {
		// This FSM tries to find the "Founded-By"  pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;
		int index = 0;
		boolean found = false;	// flag for finding an object.
		
		while (index < words.length) {
			switch (state) {
			case 0:
				if (words[index].equalsIgnoreCase("founded")) {
					// "founded"
					state = 1;
				} else {
					state = 0;	// stay here.
				}
				break;
			case 1:
				if (words[index].equalsIgnoreCase("by")) {
					// "by"
					state = 2;
				} else {
					state = 1;	// stay here.
				}
				break;
			case 2:
				if (tags[index].matches("^NN.*")) {
					// noun found
					object = object.concat(words[index]);	// add word to object string.
					found = true;
					state = 3;	// go to 3.
				} else if (tags[index].matches("^DT.*")) {
					// stay
					state = 2;
				} else {
					state = 4;
				}
				break;
			case 3:
				if (tags[index].matches("^NN.*")) {
					// noun
					object = object.concat(" "+words[index]); // add word to object string
					found = true;
					state = 3;
				} else {
					state = 4;	// end state.
				}
				break;
			case 4:	// end state
				break;	
			default:
				break;
			}
		
			index++;
		}
		
		if (found || (index>=words.length && !object.isEmpty())) {
			// add triple to vector
			triples.add(new Triple(subject,"founded by",object));
			found = false;	// reset flag.
		}
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",object);
		m.put("triples",triples);
		return m;
	}
	
	public static Map<String, Object> findAsAPosition(String subject, String[] words, String[] tags) {
		// This FSM tries to find the "As-A" position pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;
		int index = 0;
		boolean found = false;	// flag for finding an object.
		
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
				} else if (tags[index].matches("^JJ.*")) {
					// found adjective; go to state 3;
					state = 3;
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
				triples.add(new Triple(subject,"played as",object));
				triples.add(new Triple(object,FSM.IS_A,"position"));
				object= "";	// reset subject.
				found = false;	// reset flag.
			}
		}
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",subject);
		m.put("triples",triples);
		return m;
	}
	
	//public static Vector<Triple> findKnownAs(String subject, String[] words, String[] tags) {
	public static Map<String, Object> findKnownAs(String subject, String[] words, String[] tags) {
		// This FSM tries to find the "Known-As" pattern.
		Vector<Triple> triples = new Vector<Triple>();
		String object = "";
		int state = 0;	// state index.
		int index = 0;	// word index in sentence.
		boolean found = false;	// flag for finidng an object.
		
		while (index < words.length) {
			switch (state) {
			case 0:	// init/adverb state;
				if (tags[index].matches("^RB.*")) {
					state = 0;	// found adverb ; go to next state;
				} else if (words[index].equals("known")) {
					state = 1;	// found "known"; go to state 1
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
				break;	// end of FSM.
			default:
				break;
			}
			
			index ++;
			// if found = true; store triple.
			if (found || (index>=words.length && !object.isEmpty())) {
				// add triple to vector
				triples.add(new Triple(subject,"known as",object));
				object = "";	// reset object.
				found = false;	// reset flag.
			}
		}
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention", subject);
		m.put("triples", triples);
		return m;
		//return triples;
	}

	public static Map<String, Object> findIsA(String[] words, String[] tags) {
		// This FSM tries to find the IS-A pattern.
		Vector<Triple> triples = new Vector<Triple>();
		boolean success = false;
		String subject = "";
		String object = "";
		String keyJJ = "";
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
					if (tags[index].matches("^NN.*") || words[index].equalsIgnoreCase("do") || words[index].equalsIgnoreCase("da")) {
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
					if (tags[index].matches("^JJ.*")) {
						keyJJ = words[index];	// key adjective, possibly nationality of player.
						state = 5;	// found adjective; move to state 5.
					} else if (tags[index].matches("^VB.*")) {
						state = 5; // found verb; go to state 5;
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
					} else if (tags[index].matches("^JJ.*")) {
						// found more adjective; stay in state 5.
						state = 5;
					} else if (tags[index].matches("^VB.*")) {
						// found more verb; stay in state 5.
						state =5;
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
		
		if (success) {
			triples.add(new Triple(subject,FSM.IS_A,object));
			if (object.contains("footballer")) {	// a footballer is a person.
				triples.add(new Triple(subject,FSM.IS_A,"person"));
				//triples.add(new Triple(subject,"played", "football"));
				if (!keyJJ.isEmpty()) {	// a key adjective is found; likely nationality.
					triples.add(new Triple(keyJJ,FSM.IS_A,"nationality"));
				}
			}
		}
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mention",subject);
		m.put("triples", triples);
		return m;
	}

}
