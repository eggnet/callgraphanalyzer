package callgraphanalyzer;

import db.CallGraphDb;
import ownership.OwnerManager;;
public class Main {

	private static CallGraphAnalyzer callGraphAnalyzer = new CallGraphAnalyzer();
	
	/**
	 * @param [dbname] [commit_before] [commit_after] 
	 */
	public static void main(String[] args) {
		System.out.println("CallGraphAnalyzer tool developed by eggnet.");
		CallGraphDb db = new CallGraphDb();
		CallGraphAnalyzer cga = new CallGraphAnalyzer();
		OwnerManager ownerMgr = new OwnerManager();
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
					// Setup the owner table.
					ownerMgr.init(args[0], args[1]);

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

