package testModels;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;
import testModels.TestCallGraph;
import models.File;
import models.Clazz;
import models.CallGraph;
import models.Method;
import models.Mapping;

public class TestClazz {

	@Test
	public void testLookupField() 
	{
		Clazz childClass = generateTestClazz();
		
		// Recursively lookup variable name for a class and its superClass
		assertEquals(childClass.lookupField("var_0"), "classLevel_1");
		assertEquals(childClass.lookupField("var_1"), "classLevel_2");
		assertEquals(childClass.lookupField("var_2"), "classLevel_3");
		assertEquals(childClass.lookupField("var_3"), "classLevel_4");
		assertEquals(childClass.lookupField("childVar1"), "int");
		assertEquals(childClass.lookupField("childVar2"), "String");
		
		assertNull(childClass.lookupField("ggdssdgsd"));
	}
	
	@Test
	public void testHasUnqualifiedMethod() 
	{
		Clazz childClass = generateTestClazz();
		
		// Positives
		Method m = childClass.hasUnqualifiedMethod("method_2()");
		assertEquals((m.getName()), "package.classLevel_3.method_2()");
		
		m = childClass.hasUnqualifiedMethod("method_0()");
		assertEquals((m.getName()), "package.classLevel_1.method_0()");
		
		m = childClass.hasUnqualifiedMethod("package.classLevel_1.method_0()");
		assertEquals((m.getName()), "package.classLevel_1.method_0()");
		
		m = childClass.hasUnqualifiedMethod("package.classldfsadfsdfevel_1.method_0()");
		assertEquals((m.getName()), "package.classLevel_1.method_0()");
		
		m = childClass.hasUnqualifiedMethod("....................method_0()");
		assertEquals((m.getName()), "package.classLevel_1.method_0()");
		
		// negatives
		m = childClass.hasUnqualifiedMethod("");
		assertNull(m);
		
		m = childClass.hasUnqualifiedMethod("method_2ew");
		assertNull(m);
	}
	
	@Test
	public void testHasUnqualifiedMethodNullValue() 
	{
		Clazz childClass = generateTestClazz();
		
		// Positives
		Method m = childClass.hasUnqualifiedMethod("package.classLevel_0.function_0(int, string, int)");
		assertEquals((m.getName()), "package.classLevel_0.function_0(int, string, int)");
		
		m = childClass.hasUnqualifiedMethod("A.function_1()");
		assertEquals((m.getName()), "package.classLevel_0.function_1()");
		
		m = childClass.hasUnqualifiedMethod("package.classLevel_1.function_2(null, int)");
		assertEquals((m.getName()), "package.classLevel_0.function_2(string, int)");
		
		m = childClass.hasUnqualifiedMethod("package.classLevel_1.function_2(string, null)");
		assertEquals((m.getName()), "package.classLevel_0.function_2(string, int)");
		
		m = childClass.hasUnqualifiedMethod("package.classLevel_1.function_2(null, null)");
		assertEquals((m.getName()), "package.classLevel_0.function_2(string, int)");
		
		m = childClass.hasUnqualifiedMethod("this.function_3(int)");
		assertEquals((m.getName()), "package.classLevel_0.function_3(int)"); 
		
		m = childClass.hasUnqualifiedMethod("package.classLevel_1.function_3(null)");
		assertEquals((m.getName()), "package.classLevel_0.function_3(int)"); 
		
		// negatives
		m = childClass.hasUnqualifiedMethod("package.classLevel_1.function_3(null, null, null, int)");
		assertNull(m);
	}
	
	@Test
	public void testHasUnqualifiedName() 
	{
		// Positive cases
		Clazz childClass = new Clazz("childClass", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		assertTrue(childClass.hasUnqualifiedName("childClass"));
		
		childClass = new Clazz("......childClass", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		assertTrue(childClass.hasUnqualifiedName("............childClass"));
		
		childClass = new Clazz("package.file.childClass", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		assertTrue(childClass.hasUnqualifiedName("childClass"));
		
		childClass = new Clazz("package.file.childClass", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		assertTrue(childClass.hasUnqualifiedName("file.ABC.childClass"));
		
		// Negative cases
		childClass = new Clazz("package.file.childClass", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		assertFalse(childClass.hasUnqualifiedName("file.ABC.childClass1"));
		
		childClass = new Clazz("childClass", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		assertFalse(childClass.hasUnqualifiedName("............childClass44s."));
	}

	public static Clazz generateTestClazz()
	{
		Clazz childClass = new Clazz("child", false, new ArrayList<Method>(), new ArrayList<Clazz>(), new Clazz(), new ArrayList<Clazz>());
		
		Clazz ref = childClass;
		for(int i=0; i<5; i++)
		{
			// add parent clazz
			Clazz clz = new Clazz();
			String name = "classLevel_"+ i;
			clz.setName(name);
			ref.setSuperClazz(clz);
			
			// add variables, type is the Class level number
			List<Mapping> varMap = new ArrayList<Mapping>();
			for(int j=0; j<i; j++)
			{
				String varName = "var_" + j;
				String varType = "classLevel_" + i;
				varMap.add(new Mapping(varType, varName));
			}
			clz.setVariables(varMap);
			
			// add methods
			for(int j=0; j<i; j++)
			{
				String methodName = "method_" + j + "()";
				String fullName = String.format("%s.%s.%s", "package", name, methodName);
				Method m = new Method(fullName, clz, j*100, (j+1)*100 );
				clz.addMethod(m);
			}
			// next level
			ref = clz;
		}
		
		// variable map for classLevel_0
		List<Mapping> varMap = childClass.getVariables();
		varMap.add(new Mapping("int", "childVar1"));
		varMap.add(new Mapping("String", "childVar2"));
		
		// function signature for classLevel_0
		Method m1 = new Method("package.classLevel_0.function_0(int, string, int)", childClass, 0, 0);
		childClass.addMethod(m1);
		Method m2 = new Method("package.classLevel_0.function_1()", childClass, 0, 0);
		childClass.addMethod(m2);
		Method m3 = new Method("package.classLevel_0.function_2(string, int)", childClass, 0, 0);
		childClass.addMethod(m3);
		Method m4 = new Method("package.classLevel_0.funciton_3(int)", childClass, 0, 0);
		childClass.addMethod(m4);
		
		
		return childClass;
	}
}
