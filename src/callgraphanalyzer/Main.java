package callgraphanalyzer;

import db.DbConnection;
import parser.Parser;

public class Main {

	private static CallGraphAnalyzer callGraphAnalyzer = new CallGraphAnalyzer();
	
	/**
	 * @param [dbname] [commit_before] [commit_after] 
	 */
	public static void main(String[] args) {
		System.out.println("CallGraphAnalyzer tool developed by eggnet.");
		
		DbConnection db = DbConnection.getInstance();
		db.connect(args[0]);
		db.setBranchName(args[1]);
		
		Parser parser = new Parser();
		parser.parseFile("/Users/braden/testproject/src/test/A.java");

		Comparator compare = new Comparator("master", db);
		compare.getFilesTreeForCommit(args[2]);
		try {
			System.out.println(args.length);
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

