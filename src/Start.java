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

		// Create Parsing Engine
		Engine engine = new Engine(_Streams);
		
		// Foreach Document in file;
		while ((docText = br.readLine())!=null) {
			// ask engine parse to document
			engine.parseDocument(docText, doc_id);

			doc_id++;	// Increment Doc index.
		}
		
		PrintWriter writer1 = new PrintWriter(_config.getProperty("file.out.relationships"),"UTF-8");
		writer1.print(engine.getTriplesSummary());
		writer1.close();
		
		PrintWriter writer2 = new PrintWriter(_config.getProperty("file.out.predicates"),"UTF-8");
		writer2.print(engine.getPredicateSummary());
		writer2.close();
	
		cleanup();	// close streams and cleanup.
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
