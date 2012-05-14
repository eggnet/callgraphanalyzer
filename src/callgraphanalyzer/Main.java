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
		db.connect(args[0]);
		db.setBranchName(args[1]);
				
		Comparator compare = new Comparator(db, args[2], args[3], new CallGraphAnalyzer());
		compare.CompareCommits();

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
					// Read in scm2sql db input
					callGraphAnalyzer.readDB(args[0], args[1], args[2]);
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

