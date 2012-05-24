package testModels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import models.CallGraph;
import models.File;

import org.junit.Test;

import callgraphanalyzer.Comparator;
import callgraphanalyzer.Comparator.CompareResult;
import db.CallGraphDb;

public class TestFile {

	private static CallGraphDb db = new CallGraphDb();
	private String dbName ="testproject";
	private String dbBranch = "master";
	
	@Test
	public void testFileHasUnresolvedClazz()
	{
		CallGraph cg = TestCallGraph.generateDummyCallGraph();
		
		File f = cg.containsFile("file_3");
		assertNotNull(f.hasUnresolvedClazz("class_0"));
		assertNotNull(f.hasUnresolvedClazz("class_1"));
		assertNotNull(f.hasUnresolvedClazz("class_2"));
		assertNull(f.hasUnresolvedClazz("class_3"));
		assertNull(f.hasUnresolvedClazz("classsadfsafwe_0"));
	}
	
	@Test
	public void testFilehasUnresolvedInterface()
	{
		CallGraph cg = TestCallGraph.generateDummyCallGraph();
		
		File f = cg.containsFile("file_3");
		assertNotNull(f.hasUnresolvedInterface("classinterface_0"));
		assertNotNull(f.hasUnresolvedInterface("classinterface_1"));
		assertNotNull(f.hasUnresolvedInterface("classinterface_2"));
		assertNull(f.hasUnresolvedInterface("classinterface_3"));
		assertNull(f.hasUnresolvedInterface("classsadfsafwe_0"));
	}
	
	@Test
	public void testFileUpdateOwnership()
	{
		db.connect(dbName);
		db.setBranchName(dbBranch);
		
		String newCommit = "9ce7f149150cebd0b5178dc0759b856d50b26ffb";
		String oldCommit = "cc7f49bdccfc3a52dc90a737f06f8cb3499ed8d7";
		Comparator compare = new Comparator(db, newCommit, oldCommit);
		compare.CompareCommits();
	
		// 2 files changed
		CompareResult result = compare.getCompareResult();
		assertEquals(result.addedFiles.size(), 0);
		assertEquals(result.deletedFiles.size(),0);
		assertEquals(result.modifiedFileMethodMap.size(),2);
		assertEquals(result.modifiedBinaryFiles.size(),0);
	}

}
