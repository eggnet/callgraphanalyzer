package models;

public class Relation
{
	private User	PersonOne;
	private User	PersonTwo;
	private boolean IsSelf;
	private String  FileId;
	private String  Callee;
	private String  Caller;
	private int		Weight;

	public Relation()
	{
	}

	public int getWeight()
	{
		return Weight;
	}

	public Relation setWeight(int weight)
	{
		Weight = weight;
		return this;
	}

	public User getPersonOne()
	{
		return PersonOne;
	}

	public Relation setPersonOne(User personOne)
	{
		PersonOne = personOne;
		return this;
	}

	public User getPersonTwo()
	{
		return PersonTwo;
	}

	public Relation setPersonTwo(User personTwo)
	{
		PersonTwo = personTwo;
		return this;
	}
	
	public void print() {
		System.out.println("("+PersonOne.getUserEmail()+", "+PersonTwo.getUserEmail()+", Weight: " + Weight + " Callee: " + Callee + ", Caller: " + Caller + ")");
	}

	public boolean isIsSelf()
	{
		return IsSelf;
	}

	public Relation setIsSelf(boolean isSelf)
	{
		IsSelf = isSelf;
		return this;
	}

	public String getFileId()
	{
		return FileId;
	}

	public Relation setFileId(String fileId)
	{
		FileId = fileId;
		return this;
	}

	public String getCallee()
	{
		return Callee;
	}

	public Relation setCallee(String method)
	{
		Callee = method;
		return this;
	}
	
	public String getCaller() 
	{
		return Caller;
	}
	
	public Relation setCaller(String method)
	{
		Caller = method;
		return this;
	}
}
