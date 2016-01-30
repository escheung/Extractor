import java.io.IOException;
import java.io.InputStream;

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
		
		Parse topParses[] = ParserTool.parseLine(sentence, _parser, 1);
		for (Parse p: topParses) {
			p.show();
		}
		
	}
	public void chunkifyAsSpan(String[] words, String[] tags) {
		
		Span[] chunks = _chunker.chunkAsSpans(words, tags);
		System.out.println("chunks:");
		for (Span ns : chunks) {
			System.out.println(ns.toString());
		}
	}
	
	public void findPerson(String[] tokens) {
		Span nameSpansPerson[] = _nameFinderPerson.find(tokens);
		_nameFinderPerson.clearAdaptiveData();
		System.out.println("######");
		for (Span ns : nameSpansPerson) {
			System.out.println(ns.toString());
		}
	}
	
	public void findLocation(String[] tokens) {
		Span nameSpansLocation[] = _nameFinderLocation.find(tokens);
		_nameFinderLocation.clearAdaptiveData();
		for (Span ns : nameSpansLocation) {
			System.out.println(ns.toString());
		}
	}
	
	public void findOrganization(String[] tokens) {
		Span nameSpansOrganization[] = _nameFinderOrganization.find(tokens);
		_nameFinderOrganization.clearAdaptiveData();
		for (Span ns : nameSpansOrganization) {
			System.out.println(ns.toString());
		}
	}
	
	public void findDate(String[] tokens) {
		Span nameSpansDate[] = _nameFinderDate.find(tokens);
		_nameFinderDate.clearAdaptiveData();
		for (Span ns : nameSpansDate) {
			System.out.println(ns.toString());
		}
	}
	
	
	public static String[] applyPOS(String[] tokens, InputStream modelIn) throws IOException {
		
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(tokens);
		
		return tags;
	}
	
}
