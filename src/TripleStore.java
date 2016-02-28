//import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
	
	public void addTriple(Triple triple) {
		
		
	}
	
	public void addTriple(String s, String p, String o, int origin) {
		int sid = addEntity(s);
	//	int pid = addPredicate(s);
		
	}
	
	public void addTriple(int s, int p, int o, int origin) {
		if (s < 0 || p < 0 || o < 0) return;	// not valid input.
		_Subject.add(s);
		_Predicate.add(p);
		_Object.add(o);
		_Origin.add(origin);
	}
	
	public int getSize() {
		assert sanityCheck(): "getSize(): Failed sanity check!";
		return _Subject.size();
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
		if (literal == null) return -1;
		if (literal.isEmpty()) return -1;
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
	
	public String generateTriplesOutput() {
		assert(sanityCheck());
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < this._Predicate.size(); i++) {
			sb.append(String.format("%s %s %s : %d\n", 
					this.getEntityById(_Subject.get(i)), this.getPredicateById(_Predicate.get(i)),this.getEntityById(_Object.get(i)),_Origin.get(i))); 			
		}
		return sb.toString();
	}
}
