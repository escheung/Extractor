import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

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
/*	
	public static String IS_A = "is_a";
	public static String PERSON = "person";
	public static String ORGANIZATION = "organization";
	public static String LOCATION = "location";
*/
	public Engine (Map<String, InputStream> streams) throws Exception {	
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
		//ts.addPredicate(Triple.IS_A);
		parsePredicateFile(streams.get("input.predicates"));
		

	}
	public void parsePredicateFile(InputStream stream) throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine())!=null) {
			
			String[] pSlot = line.split("\\t");
			System.out.println(Arrays.toString(pSlot));
			System.out.println("---");
			ts.addPredicate(pSlot[0], pSlot[1], pSlot[2]);
		}
		
	}
	
	public void parseDocument(String docText, int docID) {
		
		docText = this.preprocess(docText);	// process document text.
		String[] sentence = this.splitIntoSentences(docText);	// use model to split 
		String anchorTerm = "";	//Anchor term for document; usually first noun found.
//		int predicate_IS_A = ts.addPredicate("is_a"); 
		for (int sid=0; sid < sentence.length; sid++) {	// for each sentence
			if (sentence[sid] == null) continue;	// skip if line is empty
			if (sentence[sid].isEmpty()) continue;	// skip if line is empty
			String cleaned = this.delStuffBtwCommas(sentence[sid]);
			cleaned = this.delStuffBtwSingleQuote(cleaned);
			
			// Tokenize line
			String[] token = this.tokenize(cleaned);
			// Apply POS tag
			String[] tag = this.tagging(token);
			
			// Parse sentences
			if (sid == 0) {
				// Parse first sentence differently.				
				


				
			}
			
			// Parse other sentences			
			parseForPerson(token, docID);

			parseForOrganization(token, docID);
			
			parseForLocation(token, docID);
			
		}
		
	}
	
	public void parseFirstSentence(TripleStore ts) {
		
	}
	
	public void parseForPerson(String[] token, int did) {
		// Use NER to find the Person.
		for (String aPerson: this.findPerson(token)) {
			int s = ts.addEntity(aPerson);
			int p = ts.getPredicateByLiteral(Triple.IS_A);
			int o = ts.getEntityByLiteral(Triple.PERSON);
			ts.addTriple(s, p, o, did);
		}
	}
	public void parseForOrganization(String[] token, int did) {
		// Use NER to find the Organization
		for (String anOrg: this.findOrganization(token)) {
			int s = ts.addEntity(anOrg);
			int p = ts.getPredicateByLiteral(Triple.IS_A);
			int o = ts.getEntityByLiteral(Triple.ORGANIZATION);
			ts.addTriple(s, p, o, did);
		}
	}
	public void parseForLocation(String[] token, int did) {
		// Use NER to find the locations
		for (String aLocation: this.findLocation(token)) {
			int s = ts.addEntity(aLocation);
			int p = ts.getPredicateByLiteral(Triple.IS_A);
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
	
	public void printTriples() {
		
		System.out.print(ts.generateTriplesOutput());
	}
}
