import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class Engine {

	public final static String LINE_SEPERATOR = "\\\\n";
	
	private SentenceDetectorME _sentenceDetector;
	private Tokenizer _tokenizer;
	private POSTaggerME _tagger;
	
	private NameFinderME _nameFinderPerson;
	private NameFinderME _nameFinderLocation;
	private NameFinderME _nameFinderOrganization;

	private TripleStore ts;

	public Engine (Map<String, InputStream> streams) throws Exception {	
		// Constructor
		this._sentenceDetector = new SentenceDetectorME(new SentenceModel(streams.get("model.sentence")));
		this._tokenizer = new TokenizerME(new TokenizerModel(streams.get("model.token")));
		this._tagger = new POSTaggerME(new POSModel(streams.get("model.pos")));
		this._nameFinderPerson = new NameFinderME(new TokenNameFinderModel(streams.get("model.ner.person")));
		this._nameFinderLocation = new NameFinderME(new TokenNameFinderModel(streams.get("model.ner.location")));
		this._nameFinderOrganization = new NameFinderME(new TokenNameFinderModel(streams.get("model.ner.org")));
		
		this.ts = new TripleStore();
		
		// add base entities and predicate types
		ts.addEntity(Triple.PERSON);
		ts.addEntity(Triple.ORGANIZATION);
		ts.addEntity(Triple.LOCATION);
		parsePredicateFile(streams.get("input.predicates"));
		
	}
	public void parsePredicateFile(InputStream stream) throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine())!=null) {
			
			String[] pSlot = line.split("\\t");
			ts.addPredicate(pSlot[0], pSlot[1], pSlot[2]);
		}
		
	}
	
	public void parseDocument(String docText, int docID) {
		
		docText = this.preprocess(docText);	// process document text.
		String[] sentence = this.splitIntoSentences(docText);	// use model to split 
		String anchorTerm = "";	//Anchor term for document; usually first noun found.
		for (int sid=0; sid < sentence.length; sid++) {	// for each sentence
			
			if (sentence[sid] == null) continue;	// skip if line is empty
			if (sentence[sid].isEmpty()) continue;	// skip if line is empty
			
			String noCommas = this.delStuffBtwCommas(sentence[sid]);
			String noCommasQuotes = this.delStuffBtwSingleQuote(noCommas);
			// Tokenize line
			String[] token = this.tokenize(sentence[sid]);
			
			// Parse sentences
			if (sid == 0) {
				// Parse first sentence differently.
				// Look for Anchor Term; and add triples to triple store.
				anchorTerm = parseFirstSentence(sentence[sid],docID);
			};
			
			// Parse each sentences
			parseSentence(this.delStuffBtwSingleQuote(sentence[sid]), anchorTerm, docID);
			
			// Use NER classifier to find person, organization, and location.
			parseForPerson(token, docID);
			parseForOrganization(token, docID);
			parseForLocation(token, docID);
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public String parseFirstSentence(String sentence, int did) {
		// parse first sentence; add triples to triple-store and return anchor terms.
		String anchor = "";	// anchor term.
		String noCommas = this.delStuffBtwCommas(sentence);
		String noQuotes = this.delStuffBtwSingleQuote(sentence);
		String noCommasQuotes = this.delStuffBtwSingleQuote(noCommas);
		
		String[] _token = this.tokenize(noCommasQuotes);
		String[] _tag = this.tagging(_token);
		
		//Vector<Triple> triples = FSM.findIsA(_token, _tag);
		Map<String, Object> m = FSM.findIsA(_token, _tag);
		Vector<Triple> triples = (Vector<Triple>)m.get("triples");
		if (triples.size()>0) {
			anchor = (String)m.get("mention");
			ts.addTriples(triples, did);
		
		}
		// Known As
		if (anchor!=null && !anchor.isEmpty()) {
			String[] _token2 = this.tokenize(this.getStuffBtwCommas(sentence));
			String[] _tag2 = this.tagging(_token2);
			Map<String, Object> m2 = FSM.findKnownAs(anchor, _token2, _tag2);
			ts.addTriples((Vector<Triple>)m2.get("triples"), did);
		}
		
		return anchor;
	}
	
	public void parseSentence(String sentence, String anchor, int did) {
		String lastMention = anchor;
		
		String[] _token = this.tokenize(sentence);
		String[] _tag = this.tagging(_token);
		// if the mention is a person.
		//if (ts.subjectHasType(lastMention,Triple.PERSON)) {
		
		Map<String, Object> map;

		if (ts.subjectHasType(anchor,Triple.PERSON)) {
			// played as a position
			map = FSM.findAsAPosition(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
			// played for a club
			map = FSM.findPlaysFor(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
			// started at a club
			map = FSM.findStartedAt(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
			// debuted for a club
			map = FSM.findDebut(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
			// Played against
			map = FSM.findPlayedAgainst(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
			// Born in
			map = FSM.findBornIn(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
			// Lived in
			map = FSM.findLivedIn(anchor, _token, _tag);
			ts.addTriples((Vector<Triple>)map.get("triples"), did);
		}

		// Founded by
		map = FSM.findFoundedBy(lastMention, _token, _tag);
		ts.addTriples((Vector<Triple>)map.get("triples"), did);
		lastMention = (String)map.get("mention");
		// Lived in
		map = FSM.findLivedIn(lastMention, _token, _tag);
		ts.addTriples((Vector<Triple>)map.get("triples"), did);

	}

	
	public void parseForPerson(String[] token, int did) {
		// Use NER to find the Person.
		for (String aPerson: this.findPerson(token)) {
			int s = ts.addEntity(aPerson);
			int p = ts.getPredicateByLiteral(FSM.IS_A);
			int o = ts.getEntityByLiteral(Triple.PERSON);
			ts.addTriple(s, p, o, did);
		}
	}
	public void parseForOrganization(String[] token, int did) {
		// Use NER to find the Organization
		for (String anOrg: this.findOrganization(token)) {
			int s = ts.addEntity(anOrg);
			int p = ts.getPredicateByLiteral(FSM.IS_A);
			int o = ts.getEntityByLiteral(Triple.ORGANIZATION);
			ts.addTriple(s, p, o, did);
		}
	}
	public void parseForLocation(String[] token, int did) {
		// Use NER to find the locations
		for (String aLocation: this.findLocation(token)) {
			int s = ts.addEntity(aLocation);
			int p = ts.getPredicateByLiteral(FSM.IS_A);
			int o = ts.getEntityByLiteral(Triple.LOCATION);
			ts.addTriple(s, p, o, did);
		}
	}
	
/*	
	public String[] splitDocument(String text, String delimiter) {
		String[] lines = text.split(delimiter);
		return lines;
	}
*/	
	public Vector<String> getStuffInBrackets(String text) {
		Vector<String> list = new Vector<String>();
		Matcher m = Pattern.compile("\\((.*?)\\)").matcher(text);
		while (m.find()) {
			list.add(m.group(1));//Fetching Group from String
		}
		return list;
	}
	public String[] splitIntoSentences(String text) {
		String[] sentences = _sentenceDetector.sentDetect(text);
		return sentences;
	}
	
	public String[] tokenize(String line) {
		String[] tokens = _tokenizer.tokenize(line);
		return tokens;
	}
	
	public String[] tagging(String[] tokens) {
		String[] tags = _tagger.tag(tokens);
		return tags;
	}
	
	public String[] findPerson(String[] tokens) {
		Span personSpan[] = _nameFinderPerson.find(tokens);
		_nameFinderPerson.clearAdaptiveData();
		return (Span.spansToStrings(personSpan, tokens));
	}
	
	public String[] findLocation(String[] tokens) {
		Span locSpans[] = _nameFinderLocation.find(tokens);
		_nameFinderLocation.clearAdaptiveData();
		return (Span.spansToStrings(locSpans, tokens));
	}
	
	public String[] findOrganization(String[] tokens) {
		Span orgSpans[] = _nameFinderOrganization.find(tokens);
		_nameFinderOrganization.clearAdaptiveData();
		return (Span.spansToStrings(orgSpans, tokens));
	}
	
	public static String[] applyPOS(String[] tokens, InputStream modelIn) throws IOException {
		
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(tokens);
		
		return tags;
	}
	
	public String preprocess(String text) {
		String line;
		// Replace the LINE_SEPERATOR with " "
		line = text.replaceAll(Engine.LINE_SEPERATOR, " ");
		// Remove items in round brackets;
		line = line.replaceAll("\\(.*?\\)","");
		return line;
	}
	
	public String delStuffBtwSingleQuote(String text) {
		String line = text;
		line = line.replaceAll("'.*?'", "");
		return line;
	}
	
	public String getStuffBtwSingleQuote(String text) {
		int i1 = text.indexOf('\'');
		int i2 = text.indexOf('\'',i1+1);
		if (i1>0 && i2>i1) {
			return text.substring(i1+1,  i2);
		}
		return "";
	}
	
	public String delStuffBtwCommas(String text) {
		String line = text;
		// Remove items between commas;
		line = line.replaceAll(",.*?,","");
		return line;
	}
	
	public String getStuffBtwCommas(String text) {
		int i1 = text.indexOf(',');
		int i2 = text.indexOf(',',i1+1);
		if (i1>0 && i2>i1) {
			return text.substring(i1+1, i2);
		}
		return "";
	}
	
	public String getTriplesSummary() {
		return (ts.generateTriplesOutput());
	}
	public String getPredicateSummary() {
		return (ts.generatePredicateFrequency());
	}
}


