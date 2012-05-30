package testCallgraphanalyzer;

import static org.junit.Assert.*;

import java.util.List;

import models.Change;
import models.File;

import org.junit.Test;

import ownership.OwnerManager;

import callgraphanalyzer.CallGraphAnalyzer;
import callgraphanalyzer.Comparator;
import db.CallGraphDb;
import db.Resources.ChangeType;

public class testCallgraphanalyzer {

	private static CallGraphDb db = new CallGraphDb();
	private String dbName ="testproject";
	private String dbBranch = "master";
	
	@Test
	public void testCGAOwnership()
	{
		db.connect(dbName);
		db.setBranchName(dbBranch);
		
		String newCommit = "9ce7f149150cebd0b5178dc0759b856d50b26ffb";
		String oldCommit = "cc7f49bdccfc3a52dc90a737f06f8cb3499ed8d7";
		Comparator compare = new Comparator(db, newCommit, oldCommit);
		CallGraphAnalyzer cga = new CallGraphAnalyzer();
		OwnerManager ownerMgr = new OwnerManager();
		
		// Setup the owner table, compare commit and generate ownership
		ownerMgr.init(dbName, dbBranch);
		ownerMgr.update();
		compare.CompareCommits();
		cga.init(compare);
		
		// Check the ownership table
		//9 files
		assertEquals(compare.newCallGraph.getAllFiles().size(), 9);
		
		/*
		src/models/Date.java
		jordan.ell7@gmail.com
		753bd7d12df07cd285956b67c33bacb7fa4fbf2a 0-233
		*/
		File file1 = new File();
		for(File f:compare.newCallGraph.getAllFiles())
		{
			if(f.getFileName().equals("src/models/Date.java"))
			{
				file1 =f;
				break;
			}
		}
		
		assertEquals(file1.getFileName(), "src/models/Date.java");
		assertEquals(file1.Owners.size(), 1);
		assertEquals(file1.Owners.get("jordan.ell7@gmail.com").get(0).getCommitId(), "753bd7d12df07cd285956b67c33bacb7fa4fbf2a");
		assertEquals(file1.Owners.get("jordan.ell7@gmail.com").get(0).getChangeType(), ChangeType.ADD);
		assertEquals(file1.Owners.get("jordan.ell7@gmail.com").get(0).getCharStart(), 0);
		assertEquals(file1.Owners.get("jordan.ell7@gmail.com").get(0).getCharEnd(), 233);
		
		/*
		src/pak/B.java
		braden@uvic.ca
		MODIFYINSERT -9ce7f149150cebd0b5178dc0759b856d50b26ffb
		151-189		
		233- 254
		*/
		for(File f:compare.newCallGraph.getAllFiles())
		{
			if(f.getFileName().equals("src/pak/B.java"))
			{
				file1 =f;
				break;
			}
		}
		assertEquals(file1.getFileName(), "src/pak/B.java");
		assertEquals(file1.Owners.size(), 2);
		List<Change> bradenChanges = file1.Owners.get("braden@uvic.ca");
		
		assertEquals(bradenChanges.get(0).getCommitId(), "9ce7f149150cebd0b5178dc0759b856d50b26ffb");
		assertEquals(bradenChanges.get(0).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(bradenChanges.get(0).getCharStart(), 151);
		assertEquals(bradenChanges.get(0).getCharEnd(), 189);
		
		assertEquals(bradenChanges.get(1).getCommitId(), "9ce7f149150cebd0b5178dc0759b856d50b26ffb");
		assertEquals(bradenChanges.get(1).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(bradenChanges.get(1).getCharStart(), 233);
		assertEquals(bradenChanges.get(1).getCharEnd(), 254);
		
		/*
			jordan.ell7@gmail.com
			24 changes
				[0]ADD - 3dc4b05fd0ef5460f951c6ecf6c80f6f202dff61 - 0-13
				[1]MODIFYINSERT - 753bd7d12df07cd285956b67c33bacb7fa4fbf2a - 14-47
				[2]MODIFYINSERT - f4672afd22f79dde79a4248b66616591db40befc - 48-80
				[6]ADD - 3dc4b05fd0ef5460f951c6ecf6c80f6f202dff61 - 113 -115
				[9]MODIFYINSERT - c67de123306d9a0e3be63ff23e3cf74074b4e41d - 189 - 223
				[22]MODIFYINSERT - 753bd7d12df07cd285956b67c33bacb7fa4fbf2a - 588 - 634
				[23]MODIFYINSERT - 753bd7d12df07cd285956b67c33bacb7fa4fbf2a - 746 - 890
				*/
				
		List<Change> jordanChanges = file1.Owners.get("jordan.ell7@gmail.com");		
		assertEquals(jordanChanges.size(),24);
		assertEquals(jordanChanges.get(0).getCommitId(), "3dc4b05fd0ef5460f951c6ecf6c80f6f202dff61");
		assertEquals(jordanChanges.get(0).getChangeType(), ChangeType.ADD);
		assertEquals(jordanChanges.get(0).getCharStart(), 0);
		assertEquals(jordanChanges.get(0).getCharEnd(), 13);
		
		assertEquals(jordanChanges.get(1).getCommitId(), "753bd7d12df07cd285956b67c33bacb7fa4fbf2a");
		assertEquals(jordanChanges.get(1).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(jordanChanges.get(1).getCharStart(), 14);
		assertEquals(jordanChanges.get(1).getCharEnd(), 47);
		
		assertEquals(jordanChanges.get(2).getCommitId(), "f4672afd22f79dde79a4248b66616591db40befc");
		assertEquals(jordanChanges.get(2).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(jordanChanges.get(2).getCharStart(), 48);
		assertEquals(jordanChanges.get(2).getCharEnd(), 80);
		
		assertEquals(jordanChanges.get(6).getCommitId(), "3dc4b05fd0ef5460f951c6ecf6c80f6f202dff61");
		assertEquals(jordanChanges.get(6).getChangeType(), ChangeType.ADD);
		assertEquals(jordanChanges.get(6).getCharStart(), 113);
		assertEquals(jordanChanges.get(6).getCharEnd(), 115);
		
		assertEquals(jordanChanges.get(9).getCommitId(), "c67de123306d9a0e3be63ff23e3cf74074b4e41d");
		assertEquals(jordanChanges.get(9).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(jordanChanges.get(9).getCharStart(), 189);
		assertEquals(jordanChanges.get(9).getCharEnd(), 223);
		
		assertEquals(jordanChanges.get(22).getCommitId(), "753bd7d12df07cd285956b67c33bacb7fa4fbf2a");
		assertEquals(jordanChanges.get(22).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(jordanChanges.get(22).getCharStart(), 588);
		assertEquals(jordanChanges.get(22).getCharEnd(), 634);
		
		assertEquals(jordanChanges.get(23).getCommitId(), "753bd7d12df07cd285956b67c33bacb7fa4fbf2a");
		assertEquals(jordanChanges.get(23).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(jordanChanges.get(23).getCharStart(), 746);
		assertEquals(jordanChanges.get(23).getCharEnd(), 890);
				
		/*		
		src/views/Date.java start 0 - 292
		2 owners
		inferno71123@yahoo.com 
		1 change f788f2183525e2e0fe2ab7137c90a37e45ab214e, 235-289 MODIFYINSERT
		jordan.ell7@gmail.com	
		[0]ADD 753bd7d12df07cd285956b67c33bacb7fa4fbf2a - 0-234
		[1]ADD 753bd7d12df07cd285956b67c33bacb7fa4fbf2a - 290-293
		*/
		for(File f:compare.newCallGraph.getAllFiles())
		{
			if(f.getFileName().equals("src/views/Date.java"))
			{
				file1 =f;
				break;
			}
		}
		assertEquals(file1.getFileName(), "src/views/Date.java");
		assertEquals(file1.Owners.size(), 2);
		List<Change> trietChanges = file1.Owners.get("inferno71123@yahoo.com");
		
		assertEquals(trietChanges.get(0).getCommitId(), "f788f2183525e2e0fe2ab7137c90a37e45ab214e");
		assertEquals(trietChanges.get(0).getChangeType(), ChangeType.MODIFYINSERT);
		assertEquals(trietChanges.get(0).getCharStart(), 235);
		assertEquals(trietChanges.get(0).getCharEnd(), 289);
		
		jordanChanges = file1.Owners.get("jordan.ell7@gmail.com");
		assertEquals(jordanChanges.get(0).getCommitId(), "753bd7d12df07cd285956b67c33bacb7fa4fbf2a");
		assertEquals(jordanChanges.get(0).getChangeType(), ChangeType.ADD);
		assertEquals(jordanChanges.get(0).getCharStart(), 0);
		assertEquals(jordanChanges.get(0).getCharEnd(), 234);
		
		assertEquals(jordanChanges.get(1).getCommitId(), "753bd7d12df07cd285956b67c33bacb7fa4fbf2a");
		assertEquals(jordanChanges.get(1).getChangeType(), ChangeType.ADD);
		assertEquals(jordanChanges.get(1).getCharStart(), 290);
		assertEquals(jordanChanges.get(1).getCharEnd(), 293);
		
	}

}
