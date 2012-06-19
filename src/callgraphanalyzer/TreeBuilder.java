package callgraphanalyzer;

import java.util.ArrayList;
import java.util.List;

import models.CommitFamily;
import models.CommitTree;
import models.Node;

import db.CallGraphDb;

public class TreeBuilder
{
	private CallGraphDb 		db;
	
	private String				startCommit;
	
	private CommitTree			commitTree;

	public TreeBuilder(CallGraphDb db, String startCommit)
	{
		this.db = db;
		this.startCommit = startCommit;
		this.commitTree = new CommitTree(new Node());
		commitTree.getRoot().setCommitID(startCommit);
	}
	
	public CommitTree generateCommitTree() {
		System.out.println("Building the commit tree.");
		List<CommitFamily> commitFamily = loadCommitFamily();
		
		recurseChildren(commitTree.getRoot(), commitFamily);
		
		return this.commitTree;
	}
	
	private void recurseChildren(Node parent, List<CommitFamily> commitFamily) {
		while(parent != null) {
			List<String> children = getChildren(commitFamily, parent.getCommitID());

			if(children.size() == 1) {
				if(!commitTree.contains(children.get(0))) {
					Node child = new Node(children.get(0));
					child.setParent(parent);
					parent.addChild(child);
					parent = child;
				}
				else
					parent = null;
			}

			else if(children.size() > 1) {
				for(String childCommit: children) {
					if(!commitTree.contains(childCommit)) {
						Node child = new Node(childCommit);
						child.setParent(parent);
						parent.addChild(child);
						
						recurseChildren(child, commitFamily);
					}
				}
				parent = null;
			}

			else {
				parent = null;
			}
		}
	}
	
	private List<CommitFamily> loadCommitFamily() {
		return db.getCommitFamilyFromCommit(startCommit);
	}
	
	private List<String> getChildren(List<CommitFamily> family, String commitID) {
		List<String> children = new ArrayList<String>();
		
		for(CommitFamily fam: family) {
			if(fam.getParentId().equals(commitID))
				children.add(fam.getChildId());
		}
		
		return children;
	}
	
	
}
