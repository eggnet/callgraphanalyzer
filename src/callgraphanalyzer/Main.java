package callgraphanalyzer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import parser.Parser;
import db.DbConnection;

public class Main {

	private static CallGraphAnalyzer callGraphAnalyzer = new CallGraphAnalyzer();
	
	/**
	 * @param [dbname] [commit_before] [commit_after] 
	 */
	public static void main(String[] args) {
		System.out.println("CallGraphAnalyzer tool developed by eggnet.");
		
		Parser parser = new Parser();
		parser.parseFile("/Users/braden/testproject/src/test/A.java");

		Comparator compare = new Comparator();
		compare.getFilesForCommit("1737517d34bca762356077a47539169820923af8");
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

