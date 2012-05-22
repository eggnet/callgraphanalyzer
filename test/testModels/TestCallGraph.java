package testModels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
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
		CallGraph cg = generateDummyCallGraph();
		
		// Method starts at classNumber*100 + methodNumber*10
		//		  end    at classNumber*100	+ methodNumber*10 + 10
		
		// File 2 has 2 classes, 1 method
		List<Method> rgMethods = cg.getMethodsUsingCharacters("file_2", 0, 100);
		assertEquals(rgMethods.size(), 1);
		
		// Changes happened inside methods
		rgMethods = cg.getMethodsUsingCharacters("file_3", 0,199);
		assertEquals(rgMethods.size(), 1 ); //only class_1 methods
		rgMethods = cg.getMethodsUsingCharacters("file_3", 200,299);
		assertEquals(rgMethods.size(), 2 ); //only class_2 methods
		rgMethods = cg.getMethodsUsingCharacters("file_3", 0,300);
		assertEquals(rgMethods.size(), 3 ); //included class_2 methods
		
		// Changes happened In between methods
		rgMethods = cg.getMethodsUsingCharacters("file_3", 107,205);
		assertEquals(rgMethods.size(), 2 ); //included class_2 methods
		boolean c1m0 = false;
		boolean c2m0 = false;
		for(Method m : rgMethods)
		{
			if(m.getName().equals("package_file_3.class_1.method_0"))
				c1m0 = true;
			if(m.getName().equals("package_file_3.class_2.method_0"))
				c2m0 = true;
		}
		assertTrue(c2m0);
		assertTrue(c1m0);
		
		// Changes with 0 or 1 char
		rgMethods = cg.getMethodsUsingCharacters("file_4", 300,302);
		assertEquals(rgMethods.size(), 1 ); //only class_1 methods
		rgMethods = cg.getMethodsUsingCharacters("file_4", 300,300);
		assertEquals(rgMethods.size(), 1 ); //only class_1 methods
		
		// Changes outside any method bound
		rgMethods = cg.getMethodsUsingCharacters("file_4", 400,502);
		assertEquals(rgMethods.size(), 0 );
		rgMethods = cg.getMethodsUsingCharacters("file_4", 150,199);
		assertEquals(rgMethods.size(), 0 );
		
		// File 4 has 4 classes, 3 + 2 + 1 methods
		rgMethods = cg.getMethodsUsingCharacters("file_4", 0,500);
		assertEquals(rgMethods.size(), 6 );
	}
	
	public static CallGraph generateDummyCallGraph()
	{
		CallGraph cg = new CallGraph();
		
		// dummy map
		Map<String, File> files = new HashMap<String, File>();
		Map<String, Clazz> 	clazzes = new HashMap<String, Clazz>();
		Map<String, Method> methods = new HashMap<String, Method>();
		
		// File i has i-1 classes
		// Each class j has j-1 methods
		// ex: package_file_8.class_7.method_6 (start at line 60 end at line 70)
		// Method starts at classNumber*100 + methodNumber*10
		//		  end    at classNumber*100	+ methodNumber*10 + 10
		for(int i=0; i< 10; i++)
		{
			File newFile = new File();
			String filename ="file_" + i;
			String packageName = "package_" + filename;
			newFile.setFileName(filename);
			
			// classes
			for(int j =0; j<i; j++)
			{
				Clazz c = new Clazz();
				String className = "class_" + j;
				c.setName(className);
				
				// interface
				Clazz inter = new Clazz();
				String interclassName = "classinterface_" + j;
				inter.setName(interclassName);
				
				// functions each
				for(int k=0; k<j; k++)
				{
					Method m = new Method();
					String methodName = "method_" + k;
					String fullName = String.format("%s.%s.%s", packageName, className, methodName);
					m.setName(fullName);
					
					// Startline = k, end = k+1;
					m.setstartChar(j*100 + k*10);
					m.setendChar  (j*100 +(k+1)*10 );
					c.addMethod(m);
				}
				
				newFile.addClazz(c);
				newFile.addInterface(inter);
			}
			files.put(newFile.getFileName(),newFile);
		}
		
		cg.setFiles(files);
		cg.setClazzes(clazzes);
		cg.setMethods(methods);
		
		return cg;
	}

}
