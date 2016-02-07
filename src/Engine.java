import java.io.IOException;
import java.io.InputStream;
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

	public Engine (InputStream sentIO, InputStream tokenIO, InputStream posIO, InputStream nerPerIO, InputStream nerLocIO, InputStream nerOrgIO) throws Exception{		
		this._sentenceDetector = new SentenceDetectorME(new SentenceModel(sentIO));
		this._tokenizer = new TokenizerME(new TokenizerModel(tokenIO));
		this._tagger = new POSTaggerME(new POSModel(posIO));
		this._nameFinderPerson = new NameFinderME(new TokenNameFinderModel(nerPerIO));
		this._nameFinderLocation = new NameFinderME(new TokenNameFinderModel(nerLocIO));
		this._nameFinderOrganization = new NameFinderME(new TokenNameFinderModel(nerOrgIO));

	}
	
	public String[] splitDocument(String text, String delimiter) {
		String[] lines = text.split(delimiter);
		return lines;
	}
	
	public String[] splitSentence(String text) {
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
	
}
