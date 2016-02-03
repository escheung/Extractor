import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
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
		//String text = "Studio Angelo Franchi is a football ground.";
		
		String text = "EstÃ¡dio Adelmar da Costa Carvalho, also known as EstÃ¡dio Ilha do Retiro and Ilha do Retiro, is a sports stadium situated in Recife, in the Brazilian state of Pernambuco, owned by Sport Recife.\nThe stadium's official name is EstÃ¡dio Adelmar da Costa Carvalho, and it was inaugurated on July 4, 1937. The stadium's common name, Ilha do Retiro, is the name of the neighborhood where it is located. The stadium's official name, Adelmar da Costa Carvalho, is in honor of the Sport Recife president who presided over the first major renovation of the stadium. Blocks of high-rise flats border the ground giving the residents and visitors excellent views of the games in the stadium from their windows and balconies.";
		//String text = "AntÃ´nio Augusto Ribeiro Reis Jr., commonly known as Juninho or Juninho Pernambucano, is a retired Brazilian footballer. Renowned for his bending free kicks, he is widely considered to be the greatest free-kick specialist of all time.\nHe led Olympique Lyonnais to seven consecutive Ligue 1 titles before leaving the club in 2009, having scored 100 goals in 350 official games for Lyon.\nFrom his international debut in 1999, Juninho played 40 games for the Brazilian national team and scored six goals. He played at the 2001 Copa AmÃ©rica and retired from international football after the 2006 World Cup.\nSince 2013 Juninho has been a football commentator with Brazilian sports network Rede Globo.";
		//String text = "Louis Noé Pamarot (born 14 April 1979), more commonly known as Noé Pamarot, is a French footballer who plays as a central defender for Spanish side Granada.";
		//String text = "Sport Club do Recife is a Brazilian sports club, located in the city of Recife, in the state of Pernambuco.";
		
		int i1 = text.indexOf(',');
		int i2 = text.indexOf(',',i1+1);
		System.out.println(text.substring(i1+1, i2));
		
		
		text = Document.preprocess(text);
		//text = Sentence.delStuffBtwCommas(text);
		text = Sentence.getStuffBtwCommas(text);
		
		String[] words = engine.tokenize(text);
		String[] tags = engine.tagging(words);
		Sentence sent = new Sentence(0,text,words,tags);
		
		System.out.println(Arrays.toString(words));
		System.out.println(Arrays.toString(tags));
		
		Vector<Triple> triples = Sentence.fsm_Kown_As("ABC", words, tags);
		Iterator<Triple> it = triples.iterator();
		while (it.hasNext()) {
			Triple t = it.next();
			System.out.println(t.getSubject() + " is-a " + t.getObject());
		}
		/*
		Triple triple = Sentence.fsm_Is_A(words,tags);
		if (triple != null) {
			System.out.println(triple.getSubject() + " is-a " + triple.getObject());
		} else {
			System.out.println("Unable to find triple.");
		}
		*/
		
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
