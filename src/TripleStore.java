import java.util.Vector;

// TripleStore is an object that store entities and the "triple" relationships between them.
// It currently DOES NOT support duplicate entity names.

public class TripleStore {

	private Vector<Integer> _Subject;	// Subject of a Triple
	private Vector<Integer> _Predicate;	// Predicate of a Triple
	private Vector<Integer> _Object;	// Object of a Triple
	private Vector<Integer> _Source;	// Origin document of the Triple
	private Vector<String> _Entity;		// Entity names as Strings
	private int _TripleSize = 0;		// Initial number of Triples.
	
	// Base Predicate ID
	public static int IS_A = 1;
	public static int PART_OF = 2;
	public static int SAME_AS = 3;
	
	public TripleStore () {
		// Constructor
		_Entity = new Vector<String>();
		_Subject = new Vector<Integer>();
		_Predicate = new Vector<Integer>();
		_Object = new Vector<Integer>();
		_Source = new Vector<Integer>();
		_TripleSize = 0;
		
		// Add base entities
		_Entity.add("entity");
		_Entity.add("person");
		_Entity.add("organization");
		_Entity.add("location");
		_Entity.add("date");
		this.addTriple(getEntity("person"),TripleStore.IS_A,getEntity("entity"),-1);
		this.addTriple(getEntity("organization"),TripleStore.IS_A,getEntity("entity"),-1);
		this.addTriple(getEntity("location"),TripleStore.IS_A,getEntity("entity"),-1);
		this.addTriple(getEntity("date"),TripleStore.IS_A,getEntity("entity"),-1);
	}
	
	public int addEntity(String name) {
		// Add new entity if not exists;
		// Return index of the new or existing entity;
		// Return -1 if string is empty or null;
		if (name == null) return -1;
		if (name.isEmpty()) return -1;
		if (_Entity.indexOf(name)<0) {	// Check if name already exists.
			_Entity.add(name);
		};
		return _Entity.indexOf(name);
	}
	
	public void addTriple(String subject_name, int predicate, String object_name, int source) {
		assert sanityCheck():"Failed Sanity Check.";
		int subject = getEntity(subject_name);
		int object = getEntity(object_name);
		
		addTriple(subject,predicate,object,source);
		
	}
	public void addTriple(int subject, int predicate, int object, int source) {
		assert sanityCheck():"Failed Sanity Check.";
		// Add a triple
		if (subject<0 || predicate<0 || object<0) return;
		if (subject<_Entity.size() && object<_Entity.size()) {
			_Subject.add(subject);
			_Predicate.add(predicate);
			_Object.add(object);
			_Source.add(source);
			_TripleSize++;
		};
		
	}
	public int getSize() {
		if (sanityCheck()) return _Subject.size();
		return 0;
	}
	public int getEntity(String name) {
		return (_Entity.indexOf(name));
	}
	public String getEntity(int id) {
		if (id < _Entity.size()) {
			return (_Entity.get(id));
		}
		return "";
	}
	public boolean sanityCheck() {
		// Sanity check of triple's size is consistent.
		System.out.println(String.format("Entity:%d;\nTriple:%d;\nSubject:%d;\nPredicate:%d\nObject:%d\n",_Entity.size(),_TripleSize,_Subject.size(),_Predicate.size(),_Object.size()));
		return (_TripleSize == _Subject.size() && _Subject.size() == _Predicate.size() && _Predicate.size() == _Object.size());
	}
	public String toString() {
		assert sanityCheck():"toString: Failed sanity check.";
		StringBuilder sb = new StringBuilder();
		int i=0;
		while(i < _TripleSize) {
			sb.append(String.format("S:%s P:%s O:%s Src:%d\n", _Entity.get(_Subject.get(i)),_Predicate.get(i),_Entity.get(_Object.get(i)),_Source.get(i)));
			i++;
		}
		
		return sb.toString();
	}
}
