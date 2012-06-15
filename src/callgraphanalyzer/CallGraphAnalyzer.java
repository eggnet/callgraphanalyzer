package callgraphanalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.CallGraph;
import models.CallGraph.MethodPercentage;
import models.CommitFamily;
import models.WeightedChange;
import models.Change;
import models.Method;
import models.Relation;
import models.User;
import callgraphanalyzer.Comparator.CompareResult;
import callgraphanalyzer.CallGraphResources;
import db.CallGraphDb;

public class CallGraphAnalyzer
{
	private CallGraphDb		db;
	private Comparator		comparator;

	private Set<Relation>	Relations;

	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public Set<Relation> getRelations() {
		return Relations;
	}

	public void setRelations(Set<Relation> relations) {
		Relations = relations;
	}

	public CallGraphAnalyzer()
	{
		this.Relations = new HashSet<Relation>();
	}

	public void init(Comparator comp)
	{
		this.comparator = comp;
		this.db = comp.db;
	}

	/**
	 * This function will return all the methods that call the given method.
	 * 
	 * @param method
	 * @param callGraph
	 * @return
	 */
	private List<Method> retrieveMethodCalls(String method, CallGraph callGraph)
	{
		Method m = callGraph.containsMethod(method);
		return m.getCalledBy();
	}

	/**
	 * Generates the network.
	 * @return The Set<Relations> that make up the network. 
	 */
	public void generateRelationships()
	{
		// For each modifiedFile, for each generate an owner for the method and
		// the % they own.
		CompareResult compareResult = comparator.getCompareResult();
		Set<Method> methodCalls;
		for (String modifiedFile : compareResult.modifiedFileMethodMap.keySet())
		{
			System.out.println("FILE NAME :  " + modifiedFile);
			
			// Get user
			User newUser = null;
			User oldUser = null;
			Set<MethodPercentage> newChangedMethods = compareResult.modifiedFileMethodMap.get(modifiedFile).newMethods;
			Set<MethodPercentage> oldChangedMethods = compareResult.modifiedFileMethodMap.get(modifiedFile).oldMethods;
			
			if(!newChangedMethods.isEmpty())
				newUser = db.getUserFromCommit(newChangedMethods.iterator().next().getCommit_id());
			
			if(!oldChangedMethods.isEmpty())
				oldUser = db.getUserFromCommit(oldChangedMethods.iterator().next().getCommit_id());
			
			// For each method in the new methods
			for (MethodPercentage newMethod : newChangedMethods)
			{
				// get all methods this one is called by
				methodCalls = new HashSet<Method>();
				if(newUser != null)
					recurseMethods(newUser, newMethod.getMethod(), 
							newMethod.getPercentage(), 0, methodCalls, comparator.newCommit.getCommit_id());
			}
		}
		for (Relation r : this.Relations)
			r.print();
	}

	/**
	 * Exports our relations for the current comparison to the nodes, edges, and networks tables.
	 */
	public void exportRelations() 
	{
		// delete old network for same 2 commits if it exists, then
		// add a record in the networks table
		int networkId = db.addNetworkRecord(this.comparator.newCommit.getCommit_id(), this.comparator.oldCommit.getCommit_id());
		for (Relation r : this.Relations)
		{
			// Add the two users to our nodes.
			db.addNode(r.getPersonOne().getUserEmail(), networkId);
			db.addNode(r.getPersonTwo().getUserEmail(), networkId);
			// Add the edge.
			db.addEdge(r.getPersonOne().getUserEmail(), r.getPersonTwo().getUserEmail(), r.getWeight(), r.getFuzzy(), networkId);
		}
	}
	
	/**
	 * Recursively checks what methods call each one.  Only going into each method once to avoid 
	 * stack overflow.
	 * @param changingUser
	 * @param currentMethod
	 * @param percentage
	 * @param currentDepth
	 * @param methodCalls
	 */
	public void recurseMethods(User changingUser, Method currentMethod, float percentage, int currentDepth, Set<Method> methodCalls, String commitID)
	{
		if (currentDepth == CallGraphResources.ANALYZER_MAX_DEPTH)
			return;
		
		//Get commit path from this commit to root
		List<CommitFamily> commitPath = db.getCommitPathToRoot(commitID);
		
		for (Method calledMethod : currentMethod.getCalledBy())
		{
			if (methodCalls.contains(calledMethod))
				continue;
			else
				methodCalls.add(calledMethod);
			List<Change> changes = db.getAllOwnersForFileAtCommit(calledMethod.getClazz().getFile().getFileName(), commitID, commitPath);
			Set<WeightedChange> calledChanges = calledMethod.getClazz().getFile().getMethodWeights(changes, calledMethod);
			for (WeightedChange calledMethodChange : calledChanges)
			{
				boolean isSelf = false;
				if (changingUser.getUserEmail().equals(calledMethodChange.getOwnerId()))
					isSelf = true;
				Relation r = new Relation(new User(changingUser.getUserEmail()), 
						new User(calledMethodChange.getOwnerId()),
						isSelf,
						calledMethod.getClazz().getFile().getFileName(),
						currentMethod.getName(),
						calledMethod.getName(),
						percentage*(calledMethodChange.getWeight()/(currentDepth+1)),
						false);
				this.Relations.add(r);
			}
			recurseMethods(changingUser, calledMethod, percentage, currentDepth + 1, methodCalls, commitID);
		}
		methodCalls = new HashSet<Method>();
		for (Method calledMethod : currentMethod.getFuzzyCalledBy())
		{
			if(methodCalls.contains(calledMethod))
				 continue;
			else
				methodCalls.add(calledMethod);
			List<Change> changes = db.getAllOwnersForFileAtCommit(calledMethod.getClazz().getFile().getFileName(), commitID, commitPath);
			Set<WeightedChange> calledChanges = calledMethod.getClazz().getFile().getMethodWeights(changes, calledMethod);
			for (WeightedChange calledMethodChange : calledChanges)
			{
				boolean isSelf = false;
				if (changingUser.getUserEmail().equals(calledMethodChange.getOwnerId()))
					isSelf = true;
				Relation r = new Relation(new User(changingUser.getUserEmail()), 
						new User(calledMethodChange.getOwnerId()),
						isSelf,
						calledMethod.getClazz().getFile().getFileName(),
						currentMethod.getName(),
						calledMethod.getName(),
						percentage*(calledMethodChange.getWeight()/(currentDepth+1)),
						true);
				this.Relations.add(r);
			}
			recurseMethods(changingUser, calledMethod, percentage, currentDepth + 1, methodCalls, commitID);
		}
	}
}
