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
		// TODO Auto-generated method stub

		
		InputStream sentIO = TestToken.class.getResourceAsStream("en-sent.bin");
		InputStream tokenIO = TestToken.class.getResourceAsStream("en-token.bin");
		InputStream posIO = TestToken.class.getResourceAsStream("en-pos-maxent.bin");
		
		SentenceDetectorME _sentenceDetector = new SentenceDetectorME(new SentenceModel(sentIO));
		Tokenizer _tokenizer = new TokenizerME(new TokenizerModel(tokenIO));
		POSTaggerME _tagger = new POSTaggerME(new POSModel(posIO));
	
		String text = "Estádio Adelmar da Costa Carvalho, usually known as Estádio Ilha do Retiro or simply Ilha do Retiro, is a sports stadium situated in Recife, in the Brazilian state of Pernambuco, owned by Sport Recife.\nThe stadium's official name is Estádio Adelmar da Costa Carvalho, and it was inaugurated on July 4, 1937. The stadium's common name, Ilha do Retiro, is the name of the neighborhood where it is located. The stadium's official name, Adelmar da Costa Carvalho, is in honor of the Sport Recife president who presided over the first major renovation of the stadium. Blocks of high-rise flats border the ground giving the residents and visitors excellent views of the games in the stadium from their windows and balconies.";
		
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
