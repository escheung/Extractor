import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;


public class TestParser {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		InputStream modelIn = new FileInputStream("resources/en-parser-chunking.bin");
		
		ParserModel model = new ParserModel(modelIn);
		
		Parser parser = ParserFactory.create(model);
		
		String sentence = "The quick brown fox , which is an animal, jumps over the lazy dog.";
		Parse topParsers[] = ParserTool.parseLine(sentence, parser, 3);
		
		for (Parse p: topParsers) {
			System.out.println("--");
			p.show();
		}
		
		
		
		
		
		modelIn.close();
		
	}

}
