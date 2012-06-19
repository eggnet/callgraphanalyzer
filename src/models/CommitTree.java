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

	public Node getRoot()
	{
		return root;
	}

	public void setRoot(Node root)
	{
		this.root = root;
	}
}
