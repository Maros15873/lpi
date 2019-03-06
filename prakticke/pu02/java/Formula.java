import java.util.*;

public abstract class Formula {

	public String nazov = "";
	public Formula[] pole = {};
	
	abstract public Formula[] subf();
	abstract public Set<String> vars();
	
	abstract public String toString();
	abstract public boolean isSatisfied(Map<String,Boolean> valuation);
	abstract public boolean equals(Formula other);
	abstract public int deg();
	abstract public Formula substitute(Formula what, Formula replacement);
	
	abstract public Formula copy();
	
	public static void main(String[] args) {

	}
	
}

class Variable extends Formula{
	
	Variable(String nazov){
		this.nazov = nazov;
	}
	
	public String name() {
		return this.nazov;
	}

	@Override
	public String toString() {
		return this.nazov;
	}

	@Override
	public boolean isSatisfied(Map<String,Boolean> valuation) {
		return valuation.get(this.nazov);
	}

	@Override
	public boolean equals(Formula other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int deg() {
		return 0;
	}

	@Override
	public Formula substitute(Formula what, Formula replacement) {
		if (this.equals(what)) {
			return replacement.copy();
		}
		return this.copy();
	}

	@Override
	public Formula[] subf() {
		Formula[] pole = {};
		return pole;
	}

	@Override
	public Set<String> vars() {
		Set<String> m = new HashSet<String>();
		m.add(this.nazov);
		return m;
	}

	@Override
	public Formula copy() {
		Variable v = new Variable(this.nazov);
		return v;
	}
	
}

class Negation extends Formula{
	
	Formula povodna;
	
	Negation(Formula originalFormula){
		this.nazov = "-" + originalFormula.toString();
		this.povodna = originalFormula;
	}
	
	Formula originalFormula() {
		return povodna;
	}

	@Override
	public String toString() {
		return this.nazov;
	}

	@Override
	public boolean isSatisfied(Map<String,Boolean> valuation) {
		return !(povodna.isSatisfied(valuation));
	}

	@Override
	public boolean equals(Formula other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int deg() {
		return 1 + originalFormula().deg();
	}

	@Override
	public Formula substitute(Formula what, Formula replacement) {
		if (this.equals(what)) {
			return replacement.copy();
		}
		Negation n = new Negation(povodna.substitute(what, replacement));
		return n;
	}

	@Override
	public Formula[] subf() {
		Formula[] pole = {povodna};
		return pole;
	}

	@Override
	public Set<String> vars() {
		Set<String> m = new HashSet<String>();
		m.addAll(povodna.vars());
		return m;
	}

	@Override
	public Formula copy() {
		Negation n = new Negation(this.povodna);
		return n;
	}
	
}

class Disjunction extends Formula{
	
	Disjunction(Formula[] prvky){
		pole = prvky;
		for (int i = 0; i < prvky.length; i++) {
			this.nazov += prvky[i];
			if (i + 1 != prvky.length) this.nazov += "|";
		}
	}

	@Override
	public String toString() {
		return "("+this.nazov+")";
	}

	@Override
	public boolean isSatisfied(Map<String,Boolean> valuation) {
		for (int i = 0; i < pole.length; i++) {
			if (pole[i].isSatisfied(valuation)) return true;
		}
		return false;
	}

	@Override
	public boolean equals(Formula other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int deg() {
		int d = 1;
		for (int i = 0; i < pole.length; i++) {
			d += pole[i].deg();
		}
		return d;
	}

	@Override
	public Formula substitute(Formula what, Formula replacement) {
		if (this.equals(what)) {
			return replacement.copy();
		}
		Formula[] kopia = new Formula[pole.length];
		for (int i = 0; i < pole.length; i++) {
			if (pole[i].equals(what)) {
				kopia[i] = replacement.copy();
			}
			else {
				kopia[i] = pole[i].substitute(what, replacement);
			}
		}
		Disjunction d = new Disjunction(kopia);
		return d;
	}

	@Override
	public Formula[] subf() {
		return this.pole;
	}

	@Override
	public Set<String> vars() {
		Set<String> m = new HashSet<String>();
		for (int i = 0; i < pole.length; i++) {
			m.addAll(pole[i].vars());
		}
		return m;
	}

	@Override
	public Formula copy() {
		Disjunction d = new Disjunction(pole);
		return d;
	}
	
}

class Conjunction extends Formula{

	Conjunction(Formula[] prvky){
		pole = prvky;
		for (int i = 0; i < prvky.length; i++) {
			this.nazov += prvky[i];
			if (i + 1 != prvky.length) this.nazov += "&";
		}
	}

	@Override
	public String toString() {
		return "("+this.nazov+")";
	}

	@Override
	public boolean isSatisfied(Map<String,Boolean> valuation) {
		for (int i = 0; i < pole.length; i++) {
			if (pole[i].isSatisfied(valuation) == false) return false;
		}
		return true;
	}

	@Override
	public boolean equals(Formula other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int deg() {
		int d = 1;
		for (int i = 0; i < pole.length; i++) {
			d += pole[i].deg();
		}
		return d;
	}

	@Override
	public Formula substitute(Formula what, Formula replacement) {
		if (this.equals(what)) {
			return replacement.copy();
		}
		Formula[] kopia = new Formula[pole.length];
		for (int i = 0; i < pole.length; i++) {
			if (pole[i].equals(what)) {
				kopia[i] = replacement.copy();
			}
			else {
				kopia[i] = pole[i].substitute(what, replacement);
			}
		}
		Conjunction c = new Conjunction(kopia);
		return c;
	}

	@Override
	public Formula[] subf() {
		return this.pole;
	}

	@Override
	public Set<String> vars() {
		Set<String> m = new HashSet<String>();
		for (int i = 0; i < pole.length; i++) {
			m.addAll(pole[i].vars());
		}
		return m;
	}

	@Override
	public Formula copy() {
		Conjunction c = new Conjunction(pole);
		return c;
	}
	
}

abstract class BinaryFormula extends Formula{
	
	Formula right, left;
	
	BinaryFormula(Formula leftSide, Formula rightSide){
		right = rightSide;
		left = leftSide;
	}
	
	Formula leftSide() {
		return left;
	}
	
	Formula rightSide() {
		return right;
	}
	
}

class Implication extends BinaryFormula{

	Implication(Formula leftSide, Formula rightSide) {
		super(leftSide, rightSide);
	}

	@Override
	public String toString() {
		return "("+this.left+"->"+this.right+")";
	}

	@Override
	public boolean isSatisfied(Map<String,Boolean> valuation) {
		if (left.isSatisfied(valuation) == false) return true;
		if (right.isSatisfied(valuation) == true) return true;
		return false;
	}

	@Override
	public boolean equals(Formula other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int deg() {
		return 1 + left.deg() + right.deg();
	}

	@Override
	public Formula substitute(Formula what, Formula replacement) {
		if (this.equals(what)) {
			return replacement.copy();
		}
		Formula l = left.copy();
		Formula r = right.copy();
		Implication i = new Implication(l.substitute(what, replacement), r.substitute(what, replacement));
		return i;
	}

	@Override
	public Formula[] subf() {
		Formula[] pole = {left, right};
		return pole;
	}

	@Override
	public Set<String> vars() {
		Set<String> m = new HashSet<String>();
		m.addAll(left.vars());
		m.addAll(right.vars());
		return m;
	}

	@Override
	public Formula copy() {
		Implication i = new Implication(left, right);
		return i;
	}
	
}

class Equivalence extends BinaryFormula{

	Equivalence(Formula leftSide, Formula rightSide){
		super(leftSide, rightSide); 
	}

	@Override
	public String toString() {
		return "("+this.left+"<->"+this.right+")";
	}

	@Override
	public boolean isSatisfied(Map<String,Boolean> valuation) {
		if (left.isSatisfied(valuation) == false && right.isSatisfied(valuation) == false) return true;
		if (left.isSatisfied(valuation) == true && right.isSatisfied(valuation) == true) return true;
		return false;
	}

	@Override
	public boolean equals(Formula other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int deg() {
		return 1 + left.deg() + right.deg();
	}

	@Override
	public Formula substitute(Formula what, Formula replacement) {
		if (this.equals(what)) {
			return replacement.copy();
		}
		Formula l = left.copy();
		Formula r = right.copy();
		Equivalence e = new Equivalence(l.substitute(what, replacement), r.substitute(what, replacement));
		return e;
	}

	@Override
	public Formula[] subf() {
		Formula[] pole = {left, right};
		return pole;
	}

	@Override
	public Set<String> vars() {
		Set<String> m = new HashSet<String>();
		m.addAll(left.vars());
		m.addAll(right.vars());
		return m;
	}

	@Override
	public Formula copy() {
		Equivalence e = new Equivalence(left, right);
		return e;
	}	
}
