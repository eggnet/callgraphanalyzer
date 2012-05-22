package callgraphanalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.CallGraph;
import models.Change;
import models.Method;
import models.Relation;
import models.User;
import callgraphanalyzer.Comparator.CompareResult;
import db.CallGraphDb;

public class CallGraphAnalyzer
{

	private CallGraphDb		db;
	private CallGraph		callGraphBefore;
	private CallGraph		callGraphAfte;
	private Comparator		comparator;

	private String			commitBeforeId;
	private String			commitAfterId;
	private String			dbName;
	private Set<Relation>	Relations;

	public CallGraphAnalyzer()
	{
		this.Relations = new HashSet<Relation>();
	}

	public void init(Comparator comp)
	{
		this.comparator = comp;
		this.db = comp.db;
	}

	public void generateLogicalOwnership()
	{
		// Go through all of the changes and in parallel update each callgraph.
		List<Change> changes = db.getAllOwnerChangesBefore(this.comparator.newCommit.getCommit_id());
		for (Change c : changes)
		{
			// todo update callgraphs
		}
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

	public void generateRelationships()
	{
		// For each modifiedFile, for each generate an owner for the method and
		// the % they own.
		CompareResult compareResult = comparator.getCompareResult();
		for (String modifiedFile : compareResult.modifiedFileMethodMap.keySet())
		{
			// For each method in the new methods.
			for (Method newMethod : compareResult.modifiedFileMethodMap.get(modifiedFile).newMethods)
			{
				// get all methods this one is called by
				Change newMethodChange = db.getLatestOwnerChange(modifiedFile, newMethod.getstartChar(), newMethod
						.getendChar(), comparator.newCommit.getCommit_date());
				recurseMethods(new User().setUserEmail(newMethodChange.getOwnerId()), newMethod,
						CallGraphResources.METHOD_RECURSE_DEPTH, 0);
			}
			for (Method oldMethod : compareResult.modifiedFileMethodMap.get(modifiedFile).oldMethods)
			{
				if (compareResult.modifiedFileMethodMap.get(modifiedFile).newMethods.contains(oldMethod))
					continue;
				// get all methods this one is called by
				Change newMethodChange = db.getLatestOwnerChange(modifiedFile, oldMethod.getstartChar(), oldMethod
						.getendChar(), comparator.oldCommit.getCommit_date());
				recurseMethods(new User().setUserEmail(newMethodChange.getOwnerId()), oldMethod,
						CallGraphResources.METHOD_RECURSE_DEPTH, 0);
			}
		}
		compareResult.print();
		for (Relation r : this.Relations)
			r.print();
	}

	public void recurseMethods(User changingUser, Method currentMethod, int maxDepth, int currentDepth)
	{
		// if (currentDepth == maxDepth)
		// return;
		for (Method calledMethod : currentMethod.getCalledBy())
		{
			Change calledMethodChange = db.getLatestOwnerChange(calledMethod.getClazz().getFile().getFileName(),
					calledMethod.getstartChar(), calledMethod.getendChar(), comparator.newCommit.getCommit_date());
			boolean isSelf = false;
			if (changingUser.getUserEmail().equals(calledMethodChange.getOwnerId()))
				isSelf = true;
			Relation r = new Relation().setPersonOne(new User().setUserEmail(changingUser.getUserEmail()))
					.setPersonTwo(new User().setUserEmail(calledMethodChange.getOwnerId())).setWeight(1)
					// TEMP WEIGHT SETTING
					.setFileId(calledMethod.getClazz().getFile().getFileName()).setCaller(calledMethod.getName())
					.setCallee(currentMethod.getName()).setIsSelf(isSelf);
			this.Relations.add(r);
			recurseMethods(changingUser, calledMethod, maxDepth, currentDepth + 1);
		}
	}
}
