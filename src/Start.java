
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

public class Start {

	public static void main(String[] args) throws Exception {
		
		// Grab configuration file.
		InputStream fileIO = Start.class.getResourceAsStream("config.properties");
		// Read Configuration File into Properties object.
		Properties _config = new Properties();
		_config.load(fileIO);
			
		InputStream _sourceIO = Start.class.getResourceAsStream(_config.getProperty("file.data"));
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
		String docText = null;
		Vector<Document> documents = new Vector<Document>();
		Engine engine = new Engine(_modelTokenIO, _modelPosIO, _modelChunkerIO, _modelParserIO,_modelNerPersonIO, _modelNerLocationIO, _modelNerOrganizationIO, _modelNerDateIO);
		
		
		while ((docText = br.readLine())!=null) {
			// Split each "document" into sentences.
			String[] sentText = engine.splitDocument(docText,Engine.LINE_SEPERATOR);
			Document doc = new Document(doc_id);

			for (int sent_id=0; sent_id < sentText.length; sent_id++) {
				// Tokenize each line
				String[] words = engine.tokenize(sentText[sent_id]);
				
				// POS Tag each line
				String[] tags = engine.tagging(words);
				
				// Chunking tokens based on tags
				String[] chunks = engine.chunkify(words, tags);
				engine.chunkifyAsSpan(words, tags);
				
				// Parse sentense into tree
				engine.parseChunking(sentText[sent_id]);
				
				// Finding Base entities using NER
				engine.findPerson(words);
				engine.findOrganization(words);
			//	engine.findLocation(words);
			//	engine.findDate(words);
				
				
				// Create Sentence instance
				Sentence sent = new Sentence(sent_id, sentText[sent_id], words, tags, chunks);
				doc.addSentence(sent);
				
			}
			
			// Create document instance and add to vector of Documents. 
			//doc = new Document(doc_id, sentences);
			documents.add(doc);
			doc_id++;	// Increment Doc index.
		}
		
		
		fileIO.close();
		_modelTokenIO.close();
		_modelPosIO.close();
		_modelChunkerIO.close();
		_modelNerPersonIO.close();
		_modelNerLocationIO.close();
		_modelNerOrganizationIO.close();
		_modelNerDateIO.close();
		_sourceIO.close();

		
		// ############
	/*	
		Iterator<Document> dit = documents.iterator();
		while (dit.hasNext()) {
			System.out.println(dit.next().toString());
		}
	*/
		// #############
		
		
	}


}
