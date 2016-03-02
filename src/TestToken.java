import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TestToken {

	public static void main(String[] args) throws Exception {
		
		InputStream sentIO = TestToken.class.getResourceAsStream("en-sent.bin");
		InputStream tokenIO = TestToken.class.getResourceAsStream("en-token.bin");
		InputStream posIO = TestToken.class.getResourceAsStream("en-pos-maxent.bin");
		
		SentenceDetectorME _sentenceDetector = new SentenceDetectorME(new SentenceModel(sentIO));
		Tokenizer _tokenizer = new TokenizerME(new TokenizerModel(tokenIO));
		POSTaggerME _tagger = new POSTaggerME(new POSModel(posIO));
	
		String text = "(born 5 January 1988)";
		String[] tokens = _tokenizer.tokenize(text);
		String[] tags = _tagger.tag(tokens);
		
		for (int i=0; i<tokens.length; i++) {
			System.out.print(String.format("%s\t", tokens[i]));
		}
		System.out.println();
		for (int i=0; i<tags.length; i++) {
			System.out.print(String.format("%s\t", tags[i]));
		}
		
		sentIO.close();
		tokenIO.close();
		posIO.close();
		
	}

}
