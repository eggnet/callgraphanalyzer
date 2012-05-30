package callgraphanalyzer;

import db.CallGraphDb;
public class Main {
	/**
	 * @param [dbname] [commit_range_start] [commit_range_end] 
	 */
	public static void main(String[] args) {
		System.out.println("CallGraphAnalyzer tool developed by eggnet.");
		CallGraphDb db = new CallGraphDb();
		CallGraphAnalyzer cga = new CallGraphAnalyzer();
		try {
			if (args.length < 4 )
			{
				System.out.println("Retry: callGraphAnalyzer [dbname] [branchname] [commit_range_start] [commit_range_end]");
				throw new ArrayIndexOutOfBoundsException();
			}
			else
			{
				try 
				{
					db.connect(args[0]);
					db.setBranchName(args[1]);
					Comparator compare = new Comparator(db, args[2], args[3]);					
					
					System.out.println("Comparing Commits...");
					compare.CompareCommits();
					cga.init(compare);
					
					System.out.println("Updating callgraphs with the ownerships...");
					System.out.println("Generating the relationships...");
					cga.generateRelationships();
					cga.exportRelations();
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

