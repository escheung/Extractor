
//import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

//import opennlp.tools.sentdetect.*;

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
		BufferedReader br = new BufferedReader(new InputStreamReader(_sourceIO));
		
		int doc_id = 0;
		String docText = null;
		Vector<Document> docs = new Vector<Document>();
		
		Engine engine = new Engine(_modelTokenIO, _modelPosIO);
		
		
		while ((docText = br.readLine())!=null) {
			// Split each "document" into sentences.
			String[] sentText = engine.parseDocument(docText,Engine.LINE_SEPERATOR);
			Vector<Sentence> sents = new Vector<Sentence>();
			// Tokenize each line
			for (int sent_id=0; sent_id < sentText.length; sent_id++) {

				String[] words = engine.tokenize(sentText[sent_id]);
				
				
				
				
				// POS Tag each line
				String[] tags = engine.tagPOS(words);
				
				
			}
			
			
			// Create document instance and add to vector of Documents. 
			//docs.add(new Document(doc_id,bigLine));
			doc_id++;	// Increment Doc index.
		}
		
		
		fileIO.close();
		_modelTokenIO.close();
		_modelPosIO.close();
		_sourceIO.close();
		
	}

/*
	public static void breakSentense() {
		InputStream in = null;
		try {
			in = new FileInputStream("en-sent.bin");
			SentenceModel model = new SentenceModel(in);
			SentenceDetectorME detector = new SentenceDetectorME(model);
			String sentences[] = detector.sentDetect("Hello! This is a sentense.");
			
			for (int i=0; i<sentences.length; i++) {
				System.out.println(sentences[i]);
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					
				}
			}			
		}

	}
	*/
	

}
