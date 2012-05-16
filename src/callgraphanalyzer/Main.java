package callgraphanalyzer;

import models.CallGraph;
import parser.Parser;
import parser.Resolver;
import db.CallGraphDb;
import db.DbConnection;

public class Main {

	private static CallGraphAnalyzer callGraphAnalyzer = new CallGraphAnalyzer();
	
	/**
	 * @param [dbname] [commit_before] [commit_after] 
	 */
	public static void main(String[] args) {
		System.out.println("CallGraphAnalyzer tool developed by eggnet.");
		CallGraphDb db = new CallGraphDb();
		CallGraphAnalyzer cga = new CallGraphAnalyzer();
		try {
			if (args.length < 4 )
			{
				System.out.println("Retry: callGraphAnalyzer [dbname] [branchname] [commit_before] [commit_after]");
				throw new ArrayIndexOutOfBoundsException();
			}
			else
			{
				try 
				{
					db.connect(args[0]);
					db.setBranchName(args[1]);
					Comparator compare = new Comparator(db, args[2], args[3], cga);					
					// TODO @braden link this with the ownership project and call it from here.
					compare.CompareCommits();
					cga.generateRelationships(compare.getCompareResult());
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				} 
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Usage callGraphAnalyzer <input postgres sql>");
		}	

	}

}

