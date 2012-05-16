package callgraphanalyzer;

import java.util.List;
import java.util.Set;

import models.CallGraph;
import models.Method;
import models.OwnerChange;
import models.Relation;
import callgraphanalyzer.Comparator.CompareResult;
import db.CallGraphDb;

public class CallGraphAnalyzer {

	private CallGraphDb db = new CallGraphDb();
	private CallGraph callGraphBefore = new CallGraph();
	private CallGraph callGraphAfter = new CallGraph();

	private String commitBeforeId;
	private String commitAfterId;
	private String dbName;
	private Set<Relation> Relations;

	/**
	 * This function will return all the methods that call the given method.
	 * 
	 * @param method
	 * @param callGraph
	 * @return
	 */
	private List<Method> retrieveMethodCalls(String method, CallGraph callGraph) {
		Method m = callGraph.containsMethod(method);
		return m.getCalledBy();
	}

	public void generateRelationships(CompareResult compareResult) {
		// For each modifiedFile, for each generate an owner for the method and the % they own.
		for (String modifiedFile : compareResult.modifiedFileMethodMap.keySet())
		{
			// For each method in the new methods.
			for (Method newMethod : compareResult.modifiedFileMethodMap.get(modifiedFile).newMethods)
			{
				if (compareResult.modifiedFileMethodMap.get(modifiedFile).oldMethods.contains(newMethod))
				{
					// our method was just modified
					
				}
				else
				{
					// our method was added
				}
			}
			for (Method oldMethod : compareResult.modifiedFileMethodMap.get(modifiedFile).oldMethods)
			{
				if (!(compareResult.modifiedFileMethodMap.get(modifiedFile).newMethods.contains(oldMethod)))
				{
					// the method was deleted.
					Relation r = new Relation(); 
					// search the ownership table for a change with same start/stop
					
				}
			}
		}
		compareResult.print();
	}
}
