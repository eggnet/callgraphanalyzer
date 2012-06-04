package callgraphanalyzer;

import java.util.LinkedList;
import java.util.List;

import models.Commit;

import db.CallGraphDb;

public class NetworkBuilder
{
	private Comparator 			compare;
	private CallGraphAnalyzer 	cga;
	private CallGraphDb 		db;
	
	private String				startCommit;
	private String				endCommit;

	public NetworkBuilder(CallGraphDb db, String startCommit, String endCommit)
	{
		super();
		this.db = db;
		this.startCommit = startCommit;
		this.endCommit = endCommit;
	}
	
	public void buildAllNetworks() {
		traverseMasterCommitStream(startCommit);
	}
	
	private void traverseMasterCommitStream(String parent) {
		while(parent != null) {
			if(parent.equals(endCommit))
				return;
			
			// Get commit family here
			List<Commit> children = db.getCommitChildren(parent);
			
			if(children.size() == 1) {
				if(!isMergeCommit(children.get(0).getCommit_id())) {
					buildNetwork(parent, children.get(0).getCommit_id());
				}
				parent = children.get(0).getCommit_id();
			}
			else if(children.size() > 1) {
				String newParent = null;
				for(Commit child: children) {
					if(!isMergeCommit(child.getCommit_id())) {
						buildNetwork(parent, child.getCommit_id());
						newParent = recurseCommitSubStream(child.getCommit_id());
					}
					else {
						newParent = child.getCommit_id();
					}
				}
				parent = newParent;
			}
		}
	}
	
	private String recurseCommitSubStream(String parent) {
		while(parent != null) {
			if(parent.equals(endCommit))
				return null;

			// Get commit family here
			List<Commit> children = db.getCommitChildren(parent);
			
			if(children.size() == 1) {
				if(!isMergeCommit(children.get(0).getCommit_id())) {
					buildNetwork(parent, children.get(0).getCommit_id());
				}
				parent = children.get(0).getCommit_id();
				if(isMergeCommit(parent))
					return parent;
			}
			
			else if(children.size() > 1) {
				String newParent = null;
				for(Commit child: children) {
					if(!isMergeCommit(child.getCommit_id())) {
						buildNetwork(parent, child.getCommit_id());
						newParent = recurseCommitSubStream(child.getCommit_id());
					}
					else {
						newParent = child.getCommit_id();
					}
				}
				parent = newParent;
				if(isMergeCommit(parent))
					return parent;
			}
		}
		
		return null;
	}
	
	private boolean isMergeCommit(String commitID) {
		List<Commit> parents = db.getCommitParents(commitID);
		return parents.size() > 1;
	}
	
	private void buildNetwork(String initial, String change) {
		System.out.println("Generating network for: " + initial + " - " + change);
		compare = new Comparator(db, initial, change);
		cga = new CallGraphAnalyzer();
		
		System.out.println("Comparing Commits...");
		compare.CompareCommits();
		cga.init(compare);
		
		System.out.println("Generating the relationships...");
		cga.generateRelationships();
		cga.exportRelations();
		System.out.println();
	}
}
