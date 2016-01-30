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
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class Engine {

	public final static String LINE_SEPERATOR = "\\\\n";
	
	private Tokenizer _tokenizer;
	private POSTaggerME _tagger;
	private ChunkerME _chunker;
	private Parser _parser;
	
	private NameFinderME _nameFinderPerson;
	private NameFinderME _nameFinderLocation;
	private NameFinderME _nameFinderOrganization;
	private NameFinderME _nameFinderDate;

	public Engine (InputStream tokenIO, InputStream posIO, InputStream chunkerIO, InputStream parserIO, InputStream nerPerIO, InputStream nerLocIO, InputStream nerOrgIO, InputStream nerDateIO) throws Exception{
		
		this._tokenizer = new TokenizerME(new TokenizerModel(tokenIO));
		this._tagger = new POSTaggerME(new POSModel(posIO));
		this._chunker = new ChunkerME(new ChunkerModel(chunkerIO));
		this._parser = ParserFactory.create(new ParserModel(parserIO));
		this._nameFinderPerson = new NameFinderME(new TokenNameFinderModel(nerPerIO));
		this._nameFinderLocation = new NameFinderME(new TokenNameFinderModel(nerLocIO));
		this._nameFinderOrganization = new NameFinderME(new TokenNameFinderModel(nerOrgIO));
		this._nameFinderDate = new NameFinderME(new TokenNameFinderModel(nerDateIO));
	}
	
	public String[] splitDocument(String text, String delimiter) {
		String[] lines = text.split(delimiter);
		return lines;
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
		String chunks[] = _chunker.chunk(words, tags);
		return chunks;
	}
	public void parseChunking(String sentence) {
		if (sentence == null) return;
		if (sentence.isEmpty()) return;
		Parse topParses[] = ParserTool.parseLine(sentence, _parser, 1);
	/*	
		for (Parse p: topParses) {
			p.show();
		}
	*/
	}
	public void chunkifyAsSpan(String[] words, String[] tags) {
		Span[] chunks = _chunker.chunkAsSpans(words, tags);
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
	
	public String[] findDate(String[] tokens) {
		Span dateSpans[] = _nameFinderDate.find(tokens);
		_nameFinderDate.clearAdaptiveData();
		return (Span.spansToStrings(dateSpans, tokens));
	}
	
	public static String[] applyPOS(String[] tokens, InputStream modelIn) throws IOException {
		
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(tokens);
		
		return tags;
	}
	
}
