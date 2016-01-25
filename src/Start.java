
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
		BufferedReader br = new BufferedReader(new InputStreamReader(_sourceIO));
		
		int doc_id = 0;
		String docText = null;
		Vector<Document> documents = new Vector<Document>();
		
		Engine engine = new Engine(_modelTokenIO, _modelPosIO, _modelChunkerIO);
		
		
		while ((docText = br.readLine())!=null) {
			// Split each "document" into sentences.
			String[] sentText = engine.parseDocument(docText,Engine.LINE_SEPERATOR);
			Vector<Sentence> sentences = new Vector<Sentence>();
			Document doc;

			for (int sent_id=0; sent_id < sentText.length; sent_id++) {
				// Tokenize each line
				String[] words = engine.tokenize(sentText[sent_id]);
				
				// POS Tag each line
				String[] tags = engine.tagging(words);
				
				// Chunking tokens bad on tags
				String[] chunks = engine.chunkify(words, tags);
				
				// Create Sentence instance
				Sentence sent = new Sentence(sent_id, sentText[sent_id], chunks);
				
				sentences.add(sent);
			}
			
			// Create document instance and add to vector of Documents. 
			doc = new Document(doc_id, sentences);
			documents.add(doc);
			doc_id++;	// Increment Doc index.
		}
		
		
		fileIO.close();
		_modelTokenIO.close();
		_modelPosIO.close();
		_modelChunkerIO.close();
		_sourceIO.close();

		
		// ############
		Iterator<Document> dit = documents.iterator();
		while (dit.hasNext()) {
			System.out.println(dit.next().toString());
		}
		// #############
		
		
	}


}
