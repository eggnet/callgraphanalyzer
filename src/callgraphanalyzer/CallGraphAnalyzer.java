package callgraphanalyzer;

import java.util.List;

import models.CallGraph;
import models.Method;
import callgraphanalyzer.Comparator.CompareResult;
import db.CallGraphDb;

public class CallGraphAnalyzer {

	private CallGraphDb db = new CallGraphDb();
	private CallGraph callGraphBefore = new CallGraph();
	private CallGraph callGraphAfter = new CallGraph();

	private String commitBeforeId;
	private String commitAfterId;
	private String dbName;

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
		// For each method, generate an owner for the method and the % they own.
		compareResult.print();
	}
}
