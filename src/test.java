import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String delim = new String("\\n");
		
		FileReader fr = new FileReader("sample.txt");
		
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		
		
		while ((line = br.readLine())!=null) {
			//System.out.println(line);
			System.out.println("-------");
			BufferedReader br2 = new BufferedReader(new StringReader(line));
			
			System.out.println(line.indexOf(delim));
			int a=0; 
			int b=line.indexOf(delim);
			while (b>=0) {
				System.out.println("a:"+a+" b:"+b);
				line = line.substring(a, b);
				System.out.println(line);
				a=b;
				b = line.indexOf(delim);
			}
			System.out.println("#########");
		
		}
		System.out.println(delim);
		br.close();
	}
	

}
