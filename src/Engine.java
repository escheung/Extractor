import java.io.InputStream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Engine {

	public final static String LINE_SEPERATOR = "\\\\n";
	
	private TokenizerModel _modelToken;
	private Tokenizer _tokenizer;
	private POSModel _modelPOS;
	private POSTaggerME _tagger;
	
	
	public Engine (InputStream tokenIO, InputStream posIO) throws Exception{
		
		this._modelToken = new TokenizerModel(tokenIO);
		this._tokenizer = new TokenizerME(_modelToken);
		this._modelPOS = new POSModel(posIO);
		this._tagger = new POSTaggerME(_modelPOS);
		
	}
	
	public String[] parseDocument(String text, String delimiter) {
		String[] lines = text.split(delimiter);
		return lines;
	}
	
	public String[] tokenize(String line) {
		String tokens[] = _tokenizer.tokenize(line);
		return tokens;
	}
	
	public String[] tagPOS(String[] tokens) {
		String tags[] = _tagger.tag(tokens);
		return tags;
	}
	/*
	public static String[] applyPOS(String[] tokens, InputStream modelIn) throws IOException {
		
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(tokens);
		
		return tags;
	}
	*/
}
