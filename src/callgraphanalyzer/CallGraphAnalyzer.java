package callgraphanalyzer;

import models.CallGraph;
import db.DbConnection;

public class CallGraphAnalyzer {
	
	private DbConnection db = DbConnection.getInstance();
	private CallGraph callGraphBefore = new CallGraph();
	private CallGraph callGraphAfter  = new CallGraph();
	
	private String commitBeforeId;
	private String commitAfterId;
	private String dbName;
	
	private void initialize(String dbName, String commitBeforeId, String commitAfterId)
	{
		try
		{
			this.commitBeforeId = commitBeforeId;
			this.commitAfterId  = commitAfterId;
			this.dbName 		= dbName;
			
			// Connect to db, the DB must exist
			db.connect(dbName);
		}
		catch(Exception e)
		{
			System.out.println("Can not connect to db");
		}
	}
	
	/**
	 * readDB
	 * Initialize two callgraphs based on the two commits
	 * @param db
	 * @param commitBeforeId
	 * @param commitAfterId
	 * 
	 */
	public boolean readDB(String db, String commitBeforeId, String commitAfterId)
	{
		try
		{
			initialize(db, commitBeforeId, commitAfterId);
			
			//Parse commitBefore
			initializeCallGraph(commitBeforeId, this.callGraphBefore);
			
			//Parse commitAfter
			initializeCallGraph(commitAfterId, this.callGraphAfter);
			
			this.db.close();
			return true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Initialize callgraph
	 * @param commitBeforeId
	 * @param callgraph
	 */
	private void initializeCallGraph(String commitBeforeId, CallGraph callgraph)
	{
		// Clear callgraph
		
		// Read all commits from DB, inset to callgraph
		
	}
		

}
