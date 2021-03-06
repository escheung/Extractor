
public class Triple {
	// A class used to contain a Triple.
	private String _Subject;
	private String _Object;
	private String _Predicate;
	
	public static String PERSON = "person";
	public static String ORGANIZATION = "organization";
	public static String LOCATION = "location";
	
	public Triple() {
		_Subject = "";
		_Object = "";
		_Predicate = "";
	}
	
	public Triple(String s, String p, String o) {
		
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
	public String getPredicate() {
		return _Predicate;
	}
	
}
