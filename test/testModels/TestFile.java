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
