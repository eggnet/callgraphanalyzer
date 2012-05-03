package callgraphanalyzer;

import java.io.IOException;
import callgraphanalyzer.CallGraphAnalyzer;
import parser.*;

public class Main {

	private static CallGraphAnalyzer callGraphAnalyzer = new CallGraphAnalyzer();
	
	/**
	 * @param [dbname] [commit_before] [commit_after] 
	 */
	public static void main(String[] args) {
		System.out.println("CallGraphAnalyzer tool developed by eggnet.");
		
		Parser parser = new Parser();
		parser.parseFile("/home/jordan/Documents/testproject/src/test/A.java");
		
		System.out.println("Done");
		
		try {
			System.out.println(args.length);
			if (args.length < 3 )
			{
				System.out.println("Retry: callGraphAnalyzer [dbname] [commit_before] [commit_after]");
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

