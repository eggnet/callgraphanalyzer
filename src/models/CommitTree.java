package models;

public class CommitTree
{
	private Node	root;

	public CommitTree()
	{
		super();
	}

	public CommitTree(Node root)
	{
		super();
		this.root = root;
	}
	
	public void printTree() {
		root.printNode(0);
	}
	
	public boolean contains(String commit) {
		return recursiveContains(root, commit);
	}
	
	private boolean recursiveContains(Node parent, String commit) {
		while(parent != null) {
			if(parent.getCommitID().equals(commit))
				return true;
			
			if(parent.getChildren().size() == 1) {
				parent = parent.getChildren().get(0);
			}
			
			else if(parent.getChildren().size() > 1) {
				boolean current = false;
				for(Node child: parent.getChildren()) {
					current = recursiveContains(child, commit);
					
					if(current)
						return current;
				}
				parent = null;
			}
			
			else {
				parent = null;
			}
		}
		
		return false;
	}
	
	public Node get(String commitID) {
		return get(this.root, commitID);
	}
	
	private Node get(Node parent, String commitID) {
		while(parent != null) {
			if(parent.getCommitID().equals(commitID))
				return parent;
			
			if(parent.getChildren().size() == 1) {
				parent = parent.getChildren().get(0);
			}
			
			else if(parent.getChildren().size() > 1) {
				Node current = null;
				for(Node child: parent.getChildren()) {
					current = get(child, commitID);
					
					if(current != null)
						return current;
				}
				parent = null;
			}
			
			else {
				parent = null;
			}
		}
		
		return null;
	}

	public Node getRoot()
	{
		return root;
	}

	public void setRoot(Node root)
	{
		this.root = root;
	}
}
