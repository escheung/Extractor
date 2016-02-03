import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;

public class TestToken {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		InputStream sourceIO = Start.class.getResourceAsStream("test.txt");
		InputStream sentIO = Start.class.getResourceAsStream("en-sent.bin");
		InputStream tokenIO = Start.class.getResourceAsStream("en-token.bin");
		InputStream posIO = Start.class.getResourceAsStream("en-pos-maxent.bin");
		
		SentenceDetectorME _sentenceDetector;
		Tokenizer _tokenizer;
		POSTaggerME _tagger;
		ChunkerME _chunker;
		Parser _parser;
		
		NameFinderME _nameFinderPerson;
		NameFinderME _nameFinderLocation;
		NameFinderME _nameFinderOrganization;
		NameFinderME _nameFinderDate;
		
		
		
		
		sourceIO.close();
		sentIO.close();
		tokenIO.close();
		posIO.close();
		
	}

}
