package callgraphanalyzer;

import models.CallGraph;
import parser.Parser;
import parser.Resolver;
import db.DbConnection;

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
		
		/*CallGraph callGraph = new CallGraph();
		Parser parser = new Parser(callGraph);
		//parser.parseFileFromFile("/home/jordan/Documents/testproject/src/test/Child.java");
		//parser.parseFileFromFile("/home/jordan/Documents/testproject/src/pak/Base.java");
		//parser.parseFileFromFile("/home/jordan/Documents/testproject/src/pak/B.java");
		//parser.parseFileFromFile("/home/jordan/Documents/testproject/src/pak/Face.java");
		parser.parseFileFromString("src/test/A.java", db.getRawFile("src/test/A.java", "118038c324f6be1f4d427879515eed60d02f6aff")); // testing
		parser.parseFileFromString("src/test/B.java", db.getRawFile("src/test/B.java", "ea276fbd7e46f84e02574823169cc06982542f0f"));	// testing
		callGraph.print();
		
		System.out.println();
		System.out.println();
		System.out.println("Resolving the fuck out of this CallGraph");
		
		Resolver resolver = new Resolver(callGraph);
		resolver.resolveAll();
		
		callGraph.print();
		*/
		Comparator compare = new Comparator("master", db, args[2], args[3], new CallGraphAnalyzer());
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

