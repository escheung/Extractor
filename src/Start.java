
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		TripleStore ts = new TripleStore();
		
		// Foreach Document in file;
		while ((docText = br.readLine())!=null) {
			// Preprocess the document text.
			docText = Document.preprocess(docText);
			
			// Split each Document into sentences using the special seperator and sentence model.
			String[] sentText = engine.splitSentence(docText);
			Document doc = new Document(doc_id);
			String anchor = "";	// Anchor term for document; Usually first noun found.
			
			for (int sent_id=0; sent_id < sentText.length; sent_id++) {
				if (sentText[sent_id] == null) continue;
				if (sentText[sent_id].isEmpty()) continue;
				
				String line = Sentence.delStuffBtwCommas(sentText[sent_id]);
				line = Sentence.delStuffBtwSingleQuote(line);
				
				// Tokenize each line
				String[] words = engine.tokenize(line);
				// POS Tag each line
				String[] tags = engine.tagging(words);
				
				if (sent_id==0) {
					// First sentence in document; Look for Anchor Term.
					// Use FSM to find basic Noun IS-A pattern.
					Triple triple = Sentence.fsm_Is_A(words, tags);
					if (triple != null) {
						ts.addTriple(triple, doc_id);
						anchor = triple.getSubject();
					}
					// Process text between first set of commas.
					String btwCommas = Sentence.getStuffBtwCommas(sentText[sent_id]);
					if (anchor!=null && !anchor.isEmpty()) {
						String[] w = engine.tokenize(btwCommas);
						String[] t = engine.tagging(w);
						ts.addTriples(Sentence.fsm_Known_As(anchor,w,t),doc_id);
					}
					String btwSinglQuote = Sentence.getStuffBtwSingleQuote(sentText[sent_id]);
					if (anchor!=null && !anchor.isEmpty()) {
						String[] w = engine.tokenize(btwSinglQuote);
						String[] t = engine.tagging(w);
						String n = Sentence.getNounOnly(w,t);
						if (!n.isEmpty()) {
							ts.addTriple(anchor, Triple.SAME_AS, n, doc_id);
						}
					}
					
					
				} else {
					// Use FSM to detect Pronoun IS-A pattern.
					// TODO: make FSM.
					
				}
				
				// Finding football teams in "Plays/ed for" pattern
				ts.addTriple(Sentence.fsm_Plays_For(words, tags),doc_id);
				
				// Finding football positions in "As A" pattern
				ts.addTriples(Sentence.fsm_As_A(anchor, words, tags), doc_id);
				
				// Finding Base entities using NER
				for (String p: engine.findPerson(words)) {
					ts.addEntity(p,doc_id);	// add entity to list.
					ts.addTriple(p,Triple.IS_A,"person",doc_id);
				}
				
				for (String o: engine.findOrganization(words)) {
					ts.addEntity(o,doc_id);	// add entity to list.
					ts.addTriple(o,Triple.IS_A,"organization",doc_id);
				}
				
				for (String l: engine.findLocation(words)) {
					ts.addEntity(l,doc_id);	// add entity to list.
					ts.addTriple(l,Triple.IS_A,"location",doc_id);
				}
				
				// Create Sentence instance
				doc.addSentence(new Sentence(sent_id, line, words, tags));
				
			}
			
			// Create document instance and add to vector of Documents. 
			//doc = new Document(doc_id, sentences);
			documents.add(doc);
			doc_id++;	// Increment Doc index.
		}
		
		
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

		
		// ############
		
		PrintWriter writer1 = new PrintWriter(_config.getProperty("file.out.is_a"),"UTF-8");
		writer1.print(ts.outputIsA());
		writer1.close();

		PrintWriter writer2 = new PrintWriter(_config.getProperty("file.out.same_as"),"UTF-8"); 
		writer2.print(ts.outputSameAs());
		writer2.close();
		
		// #############
		
		
	}
/*
	private static String preprocess(String text) {
		String line;
		// Replace the LINE_SEPERATOR with " "
		line = text.replaceAll(Engine.LINE_SEPERATOR, " ");
		// Remove items in round brackets;
		line = line.replaceAll("\\(.*?\\)","");
//		line = Sentence.removeRoundBrackets(line);
		
		return line;
	}
*/
}
