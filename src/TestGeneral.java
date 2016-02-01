import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

public class TestGeneral {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// Grab configuration file.
		InputStream fileIO = Start.class.getResourceAsStream("config.properties");
		// Read Configuration File into Properties object.
		Properties _config = new Properties();
		_config.load(fileIO);
			
		InputStream _sourceIO = Start.class.getResourceAsStream(_config.getProperty("file.data"));
		InputStream _modelSentIO = Start.class.getResourceAsStream(_config.getProperty("file.model.sentence"));
		InputStream _modelTokenIO = Start.class.getResourceAsStream(_config.getProperty("file.model.token"));
		InputStream _modelPosIO = Start.class.getResourceAsStream(_config.getProperty("file.model.pos"));
	
		InputStream _modelChunkerIO = Start.class.getResourceAsStream(_config.getProperty("file.model.chunker"));
		InputStream _modelParserIO = Start.class.getResourceAsStream(_config.getProperty("file.model.parser"));
		InputStream _modelNerPersonIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.person"));
		InputStream _modelNerLocationIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.location"));
		InputStream _modelNerOrganizationIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.organization"));
		InputStream _modelNerDateIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.date"));
		BufferedReader br = new BufferedReader(new InputStreamReader(_sourceIO));
		
		int doc_id = 0;	// Document index/counter
		int SentenceToProcess = 1;	// The number of sentence to process in each Document.
		String docText = null;
		Vector<Document> documents = new Vector<Document>();  // A vector Documents
		Engine engine = new Engine(_modelSentIO, _modelTokenIO, _modelPosIO, _modelChunkerIO, _modelParserIO,_modelNerPersonIO, _modelNerLocationIO, _modelNerOrganizationIO, _modelNerDateIO);
		//String text = "Giacomo Bonaventura (born 22 August 1989 in San Severino Marche) is an Italian football (soccer) midfielder.";
		String text = "Studio Angelo Franchi is a football ground.";
		text = Document.preprocess(text);
		String[] words = engine.tokenize(text);
		String[] tags = engine.tagging(words);
		Sentence sent = new Sentence(0,text,words,tags);
		
		Triple triple = Sentence.fsm_Is_A(words,tags);
		if (triple!=null) {
			System.out.println(triple.getSubject() +"is a" + triple.getObject());
		} else {
			System.out.println("null?");
		}
		
		TripleStore ts = new TripleStore();
		int sub = ts.addEntity(triple.getSubject());
		int obj = ts.addEntity(triple.getObject());
		ts.addTriple(sub,Triple.IS_A,obj,0);
		
		fileIO.close();
		_modelSentIO.close();
		_modelTokenIO.close();
		_modelPosIO.close();
		_modelChunkerIO.close();
		_modelNerPersonIO.close();
		_modelNerLocationIO.close();
		_modelNerOrganizationIO.close();
		_modelNerDateIO.close();
		_sourceIO.close();
	}

}
