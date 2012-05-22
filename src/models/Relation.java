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

	public Relation(User personOne, User personTwo, boolean isSelf, String fileId, String callee, String caller,
			int weight)
	{
		super();
		PersonOne = personOne;
		PersonTwo = personTwo;
		IsSelf = isSelf;
		FileId = fileId;
		Callee = callee;
		Caller = caller;
		Weight = weight;
	}

	public int getWeight()
	{
		return Weight;
	}

	public void setWeight(int weight)
	{
		Weight = weight;
	}

	public User getPersonOne()
	{
		return PersonOne;
	}

	public void setPersonOne(User personOne)
	{
		PersonOne = personOne;
	}

	public User getPersonTwo()
	{
		return PersonTwo;
	}

	public void setPersonTwo(User personTwo)
	{
		PersonTwo = personTwo;
	}
	
	public void print() {
		System.out.println("("+PersonOne.getUserEmail()+", "+PersonTwo.getUserEmail()+", Weight: " + Weight + " Callee: " + Callee + ", Caller: " + Caller + ")");
	}

	public boolean isIsSelf()
	{
		return IsSelf;
	}

	public void setIsSelf(boolean isSelf)
	{
		IsSelf = isSelf;
	}

	public String getFileId()
	{
		return FileId;
	}

	public void setFileId(String fileId)
	{
		FileId = fileId;
	}

	public String getCallee()
	{
		return Callee;
	}

	public void setCallee(String method)
	{
		Callee = method;
	}
	
	public String getCaller() 
	{
		return Caller;
	}
	
	public void setCaller(String method)
	{
		Caller = method;
	}
}
