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
	
		String text = "Papa Babacar 'Baba' Diawara (born 5 January 1988) is a Senegalese footballer who plays for Sevilla FC in Spain, as a striker.\nBorn in Dakar, Diawara started his career with Senegal Premier League club ASC Jeanne d'Arc, moving to Europe aged just 19 to sign for C.S. Marítimo, in Madeira, Portugal. Having risen to prominence in the club's B team, he made his first team debut in late 2007–08, appearing in the second half of a 1–1 home draw against C.F. Estrela da Amadora.\nPromoted to the main squad the following summer, Diawara scored 10 goals in the 2008–09 season, in 25 top flight games. His form attracted the attention of several clubs, including fellow league side Sporting Clube de Portugal, Greek team Olympiacos F.C. and Scotland's Heart of Midlothian, with the latter however being put off by Marítimo's £2.5 million (€2.75 million) valuation of the player.\nDiawara bettered his individual totals to 11 goals in 2010–11, with Marítimo finishing in 9th position. Scottish Premier League club Celtic came close to signing him in August 2011, but the deal collapsed on the last day of the transfer window due to visa issues.\nDiawara scored 10 goals in only 15 games in the first half of the";
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
