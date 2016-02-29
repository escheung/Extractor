//import java.util.Arrays;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

// TripleStore is an object that store entities and the "triple" relationships between them.
// It currently DOES NOT support duplicate entity names.

public class TripleStore {

	private Vector<Integer> _Subject;	// Subject of a Triple; linked to Lie
	private Vector<Integer> _Predicate;	// Predicate of a Triple
	private Vector<Integer> _Object;	// Object of a Triple
	private Vector<Integer> _Origin;	// Origin document of the Triple
	
	private Vector<String> _EntityLiteral;	// literal of an entity.
	
	private Vector<String> _PredicateLiteral;	// literal of predicate.
	private Vector<String> _PredicateDesc;		// description of predicate.
	private Vector<Integer> _PredicateMapTo;	// predicate map to another.

	
	public TripleStore () {
		// Constructor
		_Subject = new Vector<Integer>();	
		_Predicate = new Vector<Integer>();
		_Object = new Vector<Integer>();
		_Origin = new Vector<Integer>();
		_EntityLiteral = new Vector<String>();
		_PredicateLiteral = new Vector<String>();
		_PredicateDesc = new Vector<String>();
		_PredicateMapTo = new Vector<Integer>();

	}
	
	public void addTriples(Vector<Triple> triples, int origin) {
		if (triples==null) return;	// do nothing and return;
		Iterator<Triple> it = triples.iterator();
		while (it.hasNext()) {
			addTriple(it.next(), origin);
		}
	}
	
	public void addTriple(Triple triple, int origin) {
		
		assert sanityCheck(): "addTriple(): Failed sanity check!";
		if (triple == null) return;
		this.addTriple(triple.getSubject(), triple.getPredicate(), triple.getObject(), origin);
		
	}
	
	public void addTriple(String s, String p, String o, int origin) {
		int sid = addEntity(s);
		int pid = getPredicateByLiteral(p);
		int oid = addEntity(o);
		System.out.println(String.format("Adding triple:%s/%d %s/%d %s/%d", s,sid,p,pid,o,oid));
		this.addTriple(sid, pid, oid, origin);
	}
	
	public void addTriple(int sid, int pid, int oid, int origin) {
		if (sid < 0 || pid < 0 || oid < 0) return;	// not valid input.
		_Subject.add(sid);
		_Predicate.add(pid);
		_Object.add(oid);
		_Origin.add(origin);
	}
	
	public int getSize() {
		assert sanityCheck(): "getSize(): Failed sanity check!";
		return _Subject.size();
	}
	
	public boolean subjectHasType(String subject, String object) {
		// return true if subject is defined in triple store as a 'person'.
		if (subject==null || object==null) return false;
		if (subject.isEmpty() || object.isEmpty()) return false;
		int sid = this.getEntityByLiteral(subject);
		int pid = this.getPredicateByLiteral(FSM.IS_A);
		int oid = this.getEntityByLiteral(object);
		
		for (int i=0; i<this._Subject.size(); i++) {
			if (_Subject.get(i)==sid && _Predicate.get(i)==pid && _Object.get(i)==oid) {
				return true;
			}
		}
		return false;
	}
	
	public int addEntity(String literal) {
		// Add entity literal; do nothing if already exist, otherwise add literal and return ID.
		if (literal == null) return -1;
		if (literal.isEmpty()) return -1;
		if (_EntityLiteral.indexOf(literal)<0) {	// Check if entity already exists.
			_EntityLiteral.add(literal);
		};
		return _EntityLiteral.lastIndexOf(literal);
	}
	
	public int addPredicate(String literal, String desc, String mapTo) {
		// Add predicate literal; do nothing if already exist,
		if (literal == null || desc == null || mapTo == null) return -1;
		if (literal.isEmpty() || desc.isEmpty() || mapTo.isEmpty()) return -1;
		if (_PredicateLiteral.indexOf(literal)<0) {	// check if predicate already exists.
			_PredicateLiteral.add(literal);
			_PredicateDesc.add(desc);
			_PredicateMapTo.add(_PredicateLiteral.indexOf(mapTo));	// the predicate being mapped to must exist!
		};
		return _PredicateLiteral.lastIndexOf(literal);
	}
	
	public String getEntityById(int id) {
		return _EntityLiteral.get(id);
	}
	
	public int getEntityByLiteral(String literal) {
		return _EntityLiteral.indexOf(literal);
	}
	
	public String getPredicateById(int id) {
		return _PredicateLiteral.get(id);
	}
	
	public int getPredicateByLiteral(String literal) {
		return _PredicateLiteral.indexOf(literal);
	}
	
	public boolean sanityCheck() {
		// Sanity check of triple's size is consistent.
		return ( 
			_Subject.size() == _Predicate.size() && 
			_Predicate.size() == _Object.size() &&
			_Object.size() == _Origin.size());
	}
	
	public Vector<Integer> getListOfOrigin(int pid) {
		// generate a list of document source based on given predicate id.
		Vector<Integer> src = new Vector<Integer>();
		for (int i=0; i < this._Predicate.size(); i++) {
			if (this._Predicate.get(i)==pid) {	
				src.add(this._Origin.get(i));
			}
		}
		return src;
	}
	
	public String generateTriplesOutput() {
		assert(sanityCheck());
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < this._Predicate.size(); i++) {
			sb.append(String.format("%s\t%s\t%s\t%d\n", 
					this.getEntityById(_Subject.get(i)), 
					this.getPredicateById(_Predicate.get(i)),
					this.getEntityById(_Object.get(i)),
					_Origin.get(i)));
		}
		return sb.toString();
	}
	
	public String generatePredicateFrequency() {
		assert(sanityCheck());
		StringBuilder sb = new StringBuilder();
		
		// get unique mappings of parent predicates
		Set<Integer> uniqueMapTo = new LinkedHashSet<Integer>();
		uniqueMapTo.addAll(this._PredicateMapTo);	// unique ordered set of mapped to predicate 
		
		for (Integer mapId : uniqueMapTo) {
		    System.out.println(String.format("Relationship: %s", this._PredicateLiteral.get(mapId)));
			// find child predicates
		    for (int i=0; i < _PredicateLiteral.size(); i++) {
		    	if (this._PredicateMapTo.get(i) == mapId) {
		    		Vector<Integer> origins = this.getListOfOrigin(i);
		    		System.out.println(String.format("Pid:%d\tDesc:%s\tFreq:%d", i,this._PredicateDesc.get(i),origins.size()));
		    		System.out.println(Arrays.toString(origins.toArray()));
		    	}
		    	
		    }
		}
		
		return sb.toString();
	}
	
	public void printPredicates() {
		
		for (int i=0; i < this._PredicateLiteral.size(); i++) {
			
			System.out.println(
					String.format("%d|%s\t%d", 
							i,
							this._PredicateLiteral.get(i)
							,this._PredicateMapTo.get(i)
					));
			
		}
		
	}
}
