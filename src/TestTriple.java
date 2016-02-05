import java.util.Arrays;

public class TestTriple {

	public static void main(String[] args) throws Exception {

		TripleStore sem = new TripleStore();
		
		sem.addEntity("Character",0);
		sem.addEntity("Thing1",0);
		sem.addEntity("Thing2",0);
		sem.addEntity("CatInAHat",0);
		
		sem.sanityCheck();
		
		sem.addTriple("Character",Triple.IS_A,"Thing1",0);
		sem.addTriple("Character",Triple.IS_A,"Thing2",0);
		sem.addTriple("Character",Triple.IS_A,"CatInAHat",0);
		
		System.out.println("Printing...");
		sem.sanityCheck();
		System.out.println(sem.toString());
		
		
	}
	

}
