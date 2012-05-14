package testModels;

import static org.junit.Assert.*;
import testModels.TestCallGraph;
import org.junit.Test;
import models.File;
import models.CallGraph;

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

}
