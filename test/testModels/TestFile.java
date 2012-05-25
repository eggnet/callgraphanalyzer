package testModels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.CallGraph;
import models.Change;
import models.File;

import org.junit.Test;


import db.Resources;

public class TestFile {

	
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
	public void testFileOwnershipUpdate1() {
		CallGraph cg = TestCallGraph.generateDummyCallGraph();
		
		File file = cg.containsFile("file_3");
		
		//Generate changes
		Change change1 = new Change("personA", "commit-1", Resources.ChangeType.ADD, "file_3", 0, 500);
		Change change2 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYDELETE, "file_3", 0, 20);
		Change change3 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 400, 450);
		
		Change change11 = new Change("personA", "commit-1", Resources.ChangeType.ADD, "file_3", 0, 399);
		Change change12 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 400, 450);
		Change change13 = new Change("personA", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 451, 529);
		
		// Create the "real" results.
		Map<String, List<Change>> result = new HashMap<String, List<Change>>();
		List<Change> personAList = new ArrayList<Change>();
		personAList.add(change11);
		personAList.add(change13);
		result.put("personA", personAList);
		List<Change> personBList = new ArrayList<Change>();
		personBList.add(change12);
		result.put("personB", personBList);
		
		// Update owners and test
		file.updateOwnership(change1);
		file.updateOwnership(change2);
		file.updateOwnership(change3);
		
		// Run Test
		assertTrue(compareOwnershipMaps(file.Owners, result));
	}
	
	@Test
	public void testFileOwnershipUpdate2() {
		CallGraph cg = TestCallGraph.generateDummyCallGraph();
		
		File file = cg.containsFile("file_3");
		
		//Generate changes
		Change change1 = new Change("personA", "commit-1", Resources.ChangeType.ADD, "file_3", 0, 500);
		Change change2 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 100, 200);
		Change change3 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 300, 500);
		
		Change change11 = new Change("personA", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 0, 99);
		Change change12 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 100, 200);
		Change change13 = new Change("personA", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 201, 400);
		Change change14 = new Change("personB", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 401, 601);
		Change change15 = new Change("personA", "commit-1", Resources.ChangeType.MODIFYINSERT, "file_3", 602, 801);
		
		// Create the "real" results.
		Map<String, List<Change>> result = new HashMap<String, List<Change>>();
		List<Change> personAList = new ArrayList<Change>();
		personAList.add(change11);
		personAList.add(change13);
		personAList.add(change15);
		result.put("personA", personAList);
		List<Change> personBList = new ArrayList<Change>();
		personBList.add(change12);
		personBList.add(change14);
		result.put("personB", personBList);
		
		// Update owners and test
		file.updateOwnership(change1);
		file.updateOwnership(change2);
		file.updateOwnership(change3);
		
		// Run Test
		assertTrue(compareOwnershipMaps(file.Owners, result));
	}
	
	private boolean compareOwnershipMaps(Map<String, List<Change>> map1, Map<String, List<Change>> map2) {
		try {
			for(Map.Entry<String, List<Change>> entry1: map1.entrySet()) {
				assertTrue(map2.containsKey(entry1.getKey()));
				List<Change> list2 = map2.get(entry1.getKey());
				int i = 0;
				for(Change change: entry1.getValue()) {
					assertEquals(change.getCharStart(), list2.get(i).getCharStart());
					assertEquals(change.getCharEnd(), list2.get(i).getCharEnd());
					i++;
				}
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

}
