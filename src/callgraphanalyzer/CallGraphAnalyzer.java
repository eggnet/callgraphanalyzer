package callgraphanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.*;
import db.CallGraphDb;
import db.DbConnection;

public class CallGraphAnalyzer {
	
	private CallGraphDb db = new CallGraphDb();
	private CallGraph callGraphBefore = new CallGraph();
	private CallGraph callGraphAfter  = new CallGraph();
	
	private String commitBeforeId;
	private String commitAfterId;
	private String dbName;
	
	/**
	 * This function will return all the methods that call the 
	 * given method.
	 * @param method
	 * @param callGraph
	 * @return
	 */
	private List<Method> retrieveMethodCalls(String method, CallGraph callGraph) {
		Method m = callGraph.containsMethod(method);
		
		return m.getCalledBy();
	}
		
	
	
	
}
