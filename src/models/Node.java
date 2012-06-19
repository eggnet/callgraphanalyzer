package models;

import java.util.ArrayList;
import java.util.List;

public class Node
{
	private List<Node> 	children;
	private String		commitID;
	private Node		parent;
	
	public Node()
	{
		super();
		this.children = new ArrayList<Node>();
	}
	
	public Node(String commitID)
	{
		super();
		this.commitID = commitID;
		this.children = new ArrayList<Node>();
	}

	public Node(String commitID, Node parent)
	{
		super();
		this.commitID = commitID;
		this.parent = parent;
		this.children = new ArrayList<Node>();
	}

	public Node(List<Node> children, String commitID, Node parent)
	{
		super();
		this.children = children;
		this.commitID = commitID;
		this.parent = parent;
	}
	
	public void printNode(int x) {
		for(int i = 0; i < x; i++)
			System.out.print(" ");
		System.out.println(commitID);
		for(Node child: children) {
			child.printNode(x+2);
		}
	}

	public List<Node> getChildren()
	{
		return children;
	}
	
	public void addChild(Node child) {
		this.children.add(child);
	}

	public void setChildren(List<Node> children)
	{
		this.children = children;
	}

	public String getCommitID()
	{
		return commitID;
	}

	public void setCommitID(String commitID)
	{
		this.commitID = commitID;
	}

	public Node getParent()
	{
		return parent;
	}

	public void setParent(Node parent)
	{
		this.parent = parent;
	}
}
