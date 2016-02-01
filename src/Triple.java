
public class Triple {

	private String _Subject;
	private String _Object;
	private int _Predicate;
	
	// Base Predicate ID
	public static int IS_A = 1;
	public static int PART_OF = 2;
	public static int SAME_AS = 3;
	
	public Triple() {
		_Subject = "";
		_Object = "";
		_Predicate = -1;
	}
	
	public Triple(String s, int p, String o) {
		
		_Subject = s;
		_Predicate = p;
		_Object = o;
		
	}
	
	public String getSubject() {
		return _Subject;
	}
	public String getObject() {
		return _Object;
	}
	public int getPredicate() {
		return _Predicate;
	}
	
}
