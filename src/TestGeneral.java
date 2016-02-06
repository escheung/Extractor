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

		BufferedReader br = new BufferedReader(new InputStreamReader(_sourceIO));
		
		//Engine engine = new Engine(_modelSentIO, _modelTokenIO, _modelPosIO, _modelChunkerIO, _modelParserIO,_modelNerPersonIO, _modelNerLocationIO, _modelNerOrganizationIO);
		Engine engine = new Engine(_modelSentIO, _modelTokenIO, _modelPosIO, _modelNerPersonIO, _modelNerLocationIO, _modelNerOrganizationIO);
		//String text = "Giacomo Bonaventura (born 22 August 1989 in San Severino Marche) is an Italian professional football (soccer) midfielder.";
		//String text = "Pedro Filipe Teodósio Mendes (born 1 October 1990 in Neuchâtel, Switzerland) is a Portuguese footballer who plays for Real Madrid Castilla on loan from Sporting Clube de Portugal, as a central defender.\nMendes reached Sporting Clube de Portugal's youth system in 2003, aged 12. In his first two senior seasons, he played with Real Sport Clube (third division) and Servette FC (Switzerland, second level), in both cases on loan, helping the latter club to promotion.\nIn the 2011 summer, still owned by Sporting, Mendes joined Real Madrid, being assigned to the B team. On 7 December 2011, he made his first-team debut, replacing Álvaro Arbeloa midway through the second half of a 3–0 away win against AFC Ajax for the season's UEFA Champions League.";
		//String text = "Studio Angelo Franchi is a football ground.";
		//String text = "EstÃ¡dio Adelmar da Costa Carvalho, also known as EstÃ¡dio Ilha do Retiro and Ilha do Retiro, is a sports stadium situated in Recife, in the Brazilian state of Pernambuco, owned by Sport Recife.\nThe stadium's official name is EstÃ¡dio Adelmar da Costa Carvalho, and it was inaugurated on July 4, 1937. The stadium's common name, Ilha do Retiro, is the name of the neighborhood where it is located. The stadium's official name, Adelmar da Costa Carvalho, is in honor of the Sport Recife president who presided over the first major renovation of the stadium. Blocks of high-rise flats border the ground giving the residents and visitors excellent views of the games in the stadium from their windows and balconies.";
		//String text = "AntÃ´nio Augusto Ribeiro Reis Jr., commonly known as Juninho or Juninho Pernambucano, is a retired Brazilian footballer. Renowned for his bending free kicks, he is widely considered to be the greatest free-kick specialist of all time.\nHe led Olympique Lyonnais to seven consecutive Ligue 1 titles before leaving the club in 2009, having scored 100 goals in 350 official games for Lyon.\nFrom his international debut in 1999, Juninho played 40 games for the Brazilian national team and scored six goals. He played at the 2001 Copa AmÃ©rica and retired from international football after the 2006 World Cup.\nSince 2013 Juninho has been a football commentator with Brazilian sports network Rede Globo.";
		//String text = "Louis Noé Pamarot (born 14 April 1979), more commonly known as Noé Pamarot, is a French footballer who plays as a central defender for Spanish side Granada.";
		//String text = "Sport Club do Recife is a Brazilian sports club, located in the city of Recife, in the state of Pernambuco.";
		//String text = "Rafael TolÃ³i (born 10 October 1990) is a Brazilian football defender who plays for GoiÃ¡s in the Brazilian SÃ©rie B. He represents Brazil at under-20 level. He was born in GlÃ³ria d'Oeste.\nHe is part of GoiÃ¡s squad since 2008, and helped his club win the Campeonato Goiano in 2009. As of November 21, 2009, Rafael TolÃ³i played 17 SÃ©rie B games for GoiÃ¡s, and scored two goals.\nCampeonato Goiano: 2009, 2012";
		//String text = "Jérémy Ménez (French pronunciation: [ʒe.re.mi me.nɛz] ; born 7 May 1987) is a French international footballer who currently plays for French club Paris Saint-Germain in Ligue 1. He plays many positions in the attacking midfield, usually as a winger and a playmaker. Ménez has been described as a ambidextrous technically skilled playmaker with undeniable pace.\nMénez began his career spending time with various clubs in the Île-de-France region such as the Centre de Formation de Paris and CSF Brétigny. In 2001, he secured a move to Sochaux and spent four years in the club's youth academy. In March 2004, Ménez became the youngest professional football player in the history of Ligue 1 after signing a professional contract and made his professional debut in the 2004–05 season. With Sochaux, he played European football for the first time after participating in the 2005–05 edition of the UEFA Cup. After two seasons at the club, he joined Monaco. At Monaco, Ménez developed into a play-making midfielder under the tutelage of Brazilian manager Ricardo Gomes. After two successful seasons in Monaco, he signed for Serie A club Roma on a four-year contract. With Roma, Ménez featured in the UEFA";
		String text = "Gastón Alexis Silva Perdomo (born March 5, 1994 in Salto) is a Uruguayan footballer who plays as a centre back for Defensor Sporting.\nSilva started his career playing with Defensor Sporting in 2011. He made his debut on 8 November 2011 against Montevideo Wanderers F.C..\nDuring 2011, Silva played with the Uruguayan national under-17 football team at the 2011 FIFA U-17 World Cup in Mexico. Previously, he played the 2011 South American Under-17 Football Championship in Ecuador.\nIn 2012, Silva played with the Uruguayan national under-20 football team.\nIn 2011, he was named to participate in the Uruguay national football team under-22 squad for the 2011 Pan American Games.";
		text = Document.preprocess(text);
		//text = Sentence.delStuffBtwCommas(text);
		//text = Sentence.getStuffBtwCommas(text);
		
		String[] words = engine.tokenize(text);
		String[] tags = engine.tagging(words);
		
		System.out.println(Arrays.toString(words));
		System.out.println(Arrays.toString(tags));
		
		
		
		
		run_FSM_As_A(words,tags);
	//	run_FSM_Is_A(words, tags);
	//	run_FSM_Plays_For(words, tags);
		
		fileIO.close();
		_modelSentIO.close();
		_modelTokenIO.close();
		_modelPosIO.close();
		_modelChunkerIO.close();
		
		_modelNerPersonIO.close();
		_modelNerLocationIO.close();
		_modelNerOrganizationIO.close();
		
		_sourceIO.close();
	}
	
	private static void run_FSM_As_A(String[] words, String[] tags) {
		Vector<Triple> triples = Sentence.fsm_As_A("Eric",words, tags);
		Iterator<Triple> it = triples.iterator();
		System.out.println("FSM: As A - Size:"+triples.size());
		while (it.hasNext()) {
			Triple t = it.next();
			System.out.println(t.getSubject() + " is-a " + t.getObject());
		}
	}

	private static void run_FSM_Is_A(String[] words, String[] tags) {
		
		Vector<Triple> triples =Sentence.fsm_Is_A(words,tags);
		Iterator<Triple> it = triples.iterator();
		System.out.println("FSM: Is A");
		while (it.hasNext()) {
			Triple t = it.next();
			System.out.println(t.getSubject() + " is-a " + t.getObject());
		}
		
	}
	
	private static void run_FSM_Plays_For(String[] words, String[] tags) {
		
		Vector<Triple> triples = Sentence.fsm_Plays_For(words, tags);
		Iterator<Triple> it = triples.iterator();
		System.out.println("FSM: Plays For");
		while (it.hasNext()) {
			Triple t = it.next();
			System.out.println(t.getSubject() + " is-a " + t.getObject());
		}
		
	}
	
	
	
}
