package testCallgraphanalyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import callgraphanalyzer.CallGraphAnalyzer;
import callgraphanalyzer.Comparator;
import callgraphanalyzer.Comparator.CompareResult;
import callgraphanalyzer.Comparator.ModifiedMethod;
import db.CallGraphDb;


public class TestComparator {

	private static CallGraphDb db = new CallGraphDb();
	private String dbName ="testproject";
	private String dbBranch = "master";
	
	@Test
	public void testComparatorConstructor() {
		db.connect(dbName);
		db.setBranchName(dbBranch);
		
		String newCommit = "719e209640b77f685e6ea38ca78e1addebdcecd7";
		String oldCommit = "2969fc3a59fbac659524685a5198673c561aac0c";
		Comparator compare = new Comparator(db, newCommit, oldCommit);
		
		assertEquals(compare.newCommit.getCommit_id(), newCommit);
		assertEquals(compare.oldCommit.getCommit_id(), oldCommit);
		
		Comparator compare1 = new Comparator(db, oldCommit, newCommit);
		
		assertEquals(compare1.newCommit.getCommit_id(), newCommit);
		assertEquals(compare1.oldCommit.getCommit_id(), oldCommit);
	}

	@Test
	public void testComparatorCompareCommitsConsecutive() {
		db.connect(dbName);
		db.setBranchName(dbBranch);
		
		String newCommit = "753bd7d12df07cd285956b67c33bacb7fa4fbf2a";
		String oldCommit = "f4672afd22f79dde79a4248b66616591db40befc";
		Comparator compare = new Comparator(db, newCommit, oldCommit);
		compare.CompareCommits(oldCommit, newCommit );
		
		// 2 files added, 2 modified
		CompareResult result = compare.getCompareResult();
		assertEquals(result.addedFiles.size(), 2);
		assertEquals(result.deletedFiles.size(),0);
		assertEquals(result.modifiedFileMethodMap.size(),2);
		assertEquals(result.modifiedBinaryFiles.size(),0);
		
		// Function changed
		ModifiedMethod methods = result.modifiedFileMethodMap.get("src/pak/B.java");
		assertEquals(methods.oldMethods.size(), 4);
		assertEquals(methods.newMethods.size(), 7);
		
		methods = result.modifiedFileMethodMap.get("src/test/Child.java");
		assertEquals(methods.oldMethods.size(),0);
		assertEquals(methods.newMethods.size(),1);
	}
}
