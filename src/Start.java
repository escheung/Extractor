import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class Start {

	
	private static Map<String, InputStream> _Streams;
	private static Properties _config = new Properties();
	
	
	public static void main(String[] args) throws Exception {
	
		
		
		initialize();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(_Streams.get("input.source")));
		
		int doc_id = 0;	// Document index/counter
		String docText = null;
//		Vector<Document> documents = new Vector<Document>();  // A vector Documents

		// Create Parsing Engine
		Engine engine = new Engine(_Streams);
		// Create Triple Store
//		TripleStore ts = new TripleStore();
		
		// Foreach Document in file;
		while ((docText = br.readLine())!=null) {
			// ask engine parse to document
			engine.parseDocument(docText, doc_id);

/*			
			
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
					// Use FSM to find basic Is-A pattern.
					Vector<Triple> triples = Sentence.fsm_Is_A(words, tags);
					if (triples.size()>0) {
						Triple t = triples.get(0);
						if (t != null)anchor = t.getSubject();
						ts.addTriples(triples, doc_id);
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
					
				}
				
				// Finding football teams in "Plays/ed for" pattern
				ts.addTriples(Sentence.fsm_Plays_For(words, tags),doc_id);
				
				// Finding football positions in "As A" pattern
				ts.addTriples(Sentence.fsm_As_A(anchor, words, tags), doc_id);

				// finding city in "City Of" pattern
				ts.addTriples(Sentence.fsm_City_Of(words, tags),doc_id);
				
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
			documents.add(doc);
			
*/
			
			doc_id++;	// Increment Doc index.
		}
		
		PrintWriter writer1 = new PrintWriter(_config.getProperty("file.out.relationships"),"UTF-8");
		writer1.print(engine.getTriplesSummary());
		writer1.close();
		
		PrintWriter writer2 = new PrintWriter(_config.getProperty("file.out.predicates"),"UTF-8");
		writer2.print(engine.getPredicateSummary());
		writer2.close();
		
		
		
/*		
		// ############
		
		PrintWriter writer1 = new PrintWriter(_config.getProperty("file.out.is_a"),"UTF-8");
		writer1.print(ts.outputIsA());
		writer1.close();

		PrintWriter writer2 = new PrintWriter(_config.getProperty("file.out.same_as"),"UTF-8"); 
		writer2.print(ts.outputSameAs());
		writer2.close();
		
		// #############
*/		
		cleanup();
	}

	
	private static void initialize() throws Exception{
		
		_Streams = new HashMap<String, InputStream>();
		// Grab configuration file.
		InputStream fileIO = Start.class.getResourceAsStream("config.properties");
		// Read Configuration File into Properties object.
		
		_config.load(fileIO);
		
		InputStream _sourceIO = Start.class.getResourceAsStream(_config.getProperty("file.data"));
		InputStream _modelSentIO = Start.class.getResourceAsStream(_config.getProperty("file.model.sentence"));
		InputStream _modelTokenIO = Start.class.getResourceAsStream(_config.getProperty("file.model.token"));
		InputStream _modelPosIO = Start.class.getResourceAsStream(_config.getProperty("file.model.pos"));
		InputStream _modelNerPersonIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.person"));
		InputStream _modelNerLocationIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.location"));
		InputStream _modelNerOrganizationIO = Start.class.getResourceAsStream(_config.getProperty("file.model.ner.organization"));
		InputStream _predicatesIO = Start.class.getResourceAsStream(_config.getProperty("file.predicates"));
		
		_Streams.put("input.source", _sourceIO);
		_Streams.put("input.predicates", _predicatesIO);
		_Streams.put("model.sentence", _modelSentIO);
		_Streams.put("model.token", _modelTokenIO);
		_Streams.put("model.pos", _modelPosIO);
		_Streams.put("model.ner.person", _modelNerPersonIO);
		_Streams.put("model.ner.location", _modelNerLocationIO);
		_Streams.put("model.ner.org", _modelNerOrganizationIO);

		fileIO.close();
		
	}
	
	private static void cleanup() throws Exception {
		
		_Streams.get("input.source").close();
		_Streams.get("input.predicates").close();
		_Streams.get("model.sentence").close();
		_Streams.get("model.token").close();
		_Streams.get("model.pos").close();
		_Streams.get("model.ner.person").close();
		_Streams.get("model.ner.location").close();
		_Streams.get("model.ner.org").close();
		
	}
}
