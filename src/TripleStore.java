import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

// TripleStore is an object that store entities and the "triple" relationships between them.
// It currently DOES NOT support duplicate entity names.

public class TripleStore {

	private Vector<Integer> _Subject;	// Subject of a Triple
	private Vector<Integer> _Predicate;	// Predicate of a Triple
	private Vector<Integer> _Object;	// Object of a Triple
	private Vector<Integer> _Source;	// Origin document of the Triple
	private Vector<String> _Entity;		// Entity names as Strings
	private Vector<Integer> _EntitySource;	// Origin document of the Entity (first appearance)
	private int _TripleSize = 0;		// Initial number of Triples.
	
	// Base Predicate ID
	/*
	public static int IS_A = 1;
	public static int PART_OF = 2;
	public static int SAME_AS = 3;
	*/
	
	public TripleStore () {
		// Constructor
		_Entity = new Vector<String>();
		_EntitySource = new Vector<Integer>();
		_Subject = new Vector<Integer>();
		_Predicate = new Vector<Integer>();
		_Object = new Vector<Integer>();
		_Source = new Vector<Integer>();
		_TripleSize = 0;
		
		// Add base entities
//		addEntity("entity",-1);
		addEntity("person",-1);
		addEntity("organization",-1);
		addEntity("location",-1);
		addEntity("date",-1);

		this.addTriple(getEntity("person"),Triple.IS_A,getEntity("entity"),-1);
		this.addTriple(getEntity("organization"),Triple.IS_A,getEntity("entity"),-1);
		this.addTriple(getEntity("location"),Triple.IS_A,getEntity("entity"),-1);
		this.addTriple(getEntity("date"),Triple.IS_A,getEntity("entity"),-1);
	}
	
	public int addEntity(String name,int source) {
		// Add new entity if not exists;
		// Return index of the new or existing entity;
		// Return -1 if string is empty or null;
		if (name == null) return -1;
		if (name.isEmpty()) return -1;
		if (_Entity.indexOf(name)<0) {	// Check if name already exists.
			_Entity.add(name);
			_EntitySource.add(source);
		};
		return _Entity.indexOf(name);
	}
	
	public void addTriple(Triple triple, int source) {
		assert sanityCheck():"Failed Sanity Check.";
		if (triple == null) return;
		int subject = addEntity(triple.getSubject(),source);
		int object = addEntity(triple.getObject(),source);
		addTriple(subject, triple.getPredicate(), object, source);
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
		if (subject<0 || predicate<0 || object<0) return;		// not valid.
		if (this.exists(subject, predicate, object)) return;	// already exists.
		if (subject<_Entity.size() && object<_Entity.size()) {
			_Subject.add(subject);
			_Predicate.add(predicate);
			_Object.add(object);
			_Source.add(source);
			_TripleSize++;
		};
		
	}
	
	public void addTriples(Vector<Triple> triples, int source) {
		assert sanityCheck():"Failed Sanity Check.";
		if (triples==null) return;	// return and do nothing.
		Iterator<Triple> it = triples.iterator();
		while (it.hasNext()) {
			addTriple(it.next(),source);
		}
	}
	
	public boolean exists(int subject, int predicate, int object) {
		// Check if the proposed triple already exists.
		assert sanityCheck():"exists(): Failed sanity check.";
		int i=0;
		while (i<_TripleSize) {
			if (_Subject.get(i)==subject && _Predicate.get(i)==predicate && _Object.get(i)==object) {
				return true;				
			}
			i++;
		}
		return false;
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
	public String outputIsA() {
		assert sanityCheck():"toString: Failed sanity check.";
		StringBuilder sb = new StringBuilder();
		int i=0;
		while(i < _TripleSize) {
			sb.append(String.format("%s\t%s\t%d\n",
					_Entity.get(_Object.get(i)), 
					_Entity.get(_Subject.get(i)), 
					_Source.get(i)));				
			i++;
		}
		return sb.toString();
	}
	
	public String outputSameAs() {
		assert sanityCheck():"toString: Failed sanity check.";
		StringBuilder sb = new StringBuilder();

		Set<Integer> visited = new HashSet<>();
		
		for (int i=0; i < _TripleSize; i++) {
			if (_Predicate.get(i)!=Triple.SAME_AS) continue;	// skip if triple is not a SAME-AS relationship.
			Set<Integer> current = new HashSet<>();				// init the "current" Set.
			if (visited.contains(_Subject.get(i))) continue;	// skip to next triple if target already visited;
			current.add(_Subject.get(i));	// add target to current list;
			
			// crawl through rest of all triples to find target.
			for (int j=i; j < _TripleSize; j++) {
				if (_Predicate.get(j)!=Triple.SAME_AS) continue;	// skip if triple is not a SAME-AS relationship.
				// if either subject or object are in the set being checked.
				if ( current.contains(_Subject.get(j)) || current.contains(_Object.get(j) )) {	
					current.add(_Subject.get(j));	// add target to current Set;
					current.add(_Object.get(j));		// add equivalent to current Set;	
				}
			}
			
			visited.addAll(current);
			
			Iterator<Integer> it = current.iterator();
			while (it.hasNext()) {
				int eid = it.next();
				sb.append(String.format("%s(%d)",_Entity.get(eid),_EntitySource.get(eid)));
				if (it.hasNext()) {
					sb.append(',');
				} else {
					sb.append('\n');
				}
			}
			
		}
		
		return sb.toString();
	}
	
}
