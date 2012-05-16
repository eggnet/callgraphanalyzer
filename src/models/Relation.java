package models;

public class Relation {
	public User PersonOne;
	public User PersonTwo;
	OwnerChange inducingChange;
	
	public Relation() { }
	
	public User getPersonOne() {
		return PersonOne;
	}
	public void setPersonOne(User personOne) {
		PersonOne = personOne;
	}
	public User getPersonTwo() {
		return PersonTwo;
	}
	public void setPersonTwo(User personTwo) {
		PersonTwo = personTwo;
	}
	public OwnerChange getInducingChange() {
		return inducingChange;
	}
	public void setInducingChange(OwnerChange inducingChange) {
		this.inducingChange = inducingChange;
	}
}
