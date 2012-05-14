package testModels;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import models.CallGraph;
import models.Clazz;
import models.File;
import models.Method;

import org.junit.Test;
public class TestCallGraph {

	@Test
	public void testGetMethodsUsingCharacters() 
	{
		CallGraph cg = new CallGraph();
		
		// dummy map
		Map<String, File> files = new HashMap<String, File>();
		Map<String, Clazz> 	clazzes = new HashMap<String, Clazz>();
		Map<String, Method> methods = new HashMap<String, Method>();
		
		for(int i=0; i< 10; i++)
		{
			String filename ="File_" + i;
			File newFile = new File();
			String packageName = "Package_" + filename;
			for(int j =0; j<20; j++)
			{
				Clazz c = new Clazz();
				String className = "class" + j;
				
				for(int k=0; k<10; k++)
				{
					Method m = new Method();
					String methodName = String.format("%s.%s.method%s", packageName, className, i);
					m.setName(methodName);
				}
			}
		}
		
		assertTrue(false);
	}

}
