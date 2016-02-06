import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
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
	private ChunkerME _chunker;
	private Parser _parser;
	
	private NameFinderME _nameFinderPerson;
	private NameFinderME _nameFinderLocation;
	private NameFinderME _nameFinderOrganization;

	//public Engine (InputStream sentIO, InputStream tokenIO, InputStream posIO, InputStream chunkerIO, InputStream parserIO, InputStream nerPerIO, InputStream nerLocIO, InputStream nerOrgIO) throws Exception{
	public Engine (InputStream sentIO, InputStream tokenIO, InputStream posIO) throws Exception{		
		this._sentenceDetector = new SentenceDetectorME(new SentenceModel(sentIO));
		this._tokenizer = new TokenizerME(new TokenizerModel(tokenIO));
		this._tagger = new POSTaggerME(new POSModel(posIO));
/*
		this._chunker = new ChunkerME(new ChunkerModel(chunkerIO));
		this._parser = ParserFactory.create(new ParserModel(parserIO));
		this._nameFinderPerson = new NameFinderME(new TokenNameFinderModel(nerPerIO));
		this._nameFinderLocation = new NameFinderME(new TokenNameFinderModel(nerLocIO));
		this._nameFinderOrganization = new NameFinderME(new TokenNameFinderModel(nerOrgIO));
*/
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
	
	public String[] chunkify(String[] words, String[] tags) {
		Span[] chunks = _chunker.chunkAsSpans(words, tags);
		return (Span.spansToStrings(chunks, words));
	}
	
	public void parseToTree(String sentence) {
		Parse topParses[] = ParserTool.parseLine(sentence, _parser, 1);
		
		for (Parse p0:topParses) {
			StringBuffer sb0 = new StringBuffer();
			p0.show(sb0);
			System.out.println(" "+sb0);
			System.out.println("#ofChildren"+p0.getChildCount());
			for (Parse p1:p0.getChildren()) {
				StringBuffer sb1 = new StringBuffer();
				p1.show(sb1);
				System.out.println(" "+sb1);
				System.out.println(" "+p1.getCoveredText());
				System.out.println(" #ofChildren"+p1.getChildCount());
				for (Parse p2:p1.getChildren()) {
					StringBuffer sb2 = new StringBuffer();
					p2.show(sb2);	
					System.out.println("  "+sb2);
					System.out.println("  "+p2.getCoveredText());
					System.out.println("  #ofChildren"+p2.getChildCount());
					
				}	
			}
			
		}
		
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
