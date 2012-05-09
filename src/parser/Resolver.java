package parser;

import java.util.ArrayList;
import java.util.List;

import models.*;

public class Resolver {
	
	CallGraph callGraph;
	
	public Resolver(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
	
	public void resolveAll() {
		resolveClazzes();
		resolveMethods();
	}
	
	public boolean resolveMethods() {
		for(File file: callGraph.getAllFiles()) {
			resolveFileMethodCalls(file);
		}
		return true;
	}
	
	public boolean resolveFileMethodCalls(File file) {
		for(Clazz clazz: file.getFileClazzes()) {
			resolveAllClazzMethodCalls(file, clazz);
		}
		return true;
	}
	
	public boolean resolveAllClazzMethodCalls(File file, Clazz clazz) {
		for(Method m: clazz.getMethods()) {
			resolveMethodCalls(file, clazz, m);
		}
		return true;
	}
	
	public boolean resolveMethodCalls(File file, Clazz clazz, Method method) {
		
		// TODO finish this
		
		return false;
	}
	
	/**
	 * This function will return the 
	 * @param file
	 * @param clazz
	 * @param m
	 * @return
	 */
	public Method resolveLocalMethod(Clazz clazz, String m) {
		return clazz.hasUnresolvedMethod(m);
	}
	
	/**
	 * Pass this method an argument such as A.method() and it will try and
	 * resolve method() in class A, else it will return null
	 * @param unresolvedMethod
	 * @return
	 */
	public Method resolveExternalMethod(Method m, String unresolvedMethod) {
		// Get all classes in the package
		List<Clazz> pkgClazzes = getClazzesInPackage(m.getClazz().getFile().getFilePackage());
		// Get all classes that are imported
		List<Clazz> impClazzes = getClazzesInImports(m.getClazz().getFile().getFileImports());
		
		// Combine the two lists to search over all reachable clazzes
		List<Clazz> clazzes = pkgClazzes;
		for(Clazz claz: impClazzes)
			clazzes.add(claz);
		
		String packageName;
		for(Clazz claz: clazzes) {
			packageName = claz.getFile().getFilePackage();
			for(Method meth: claz.getMethods()) {
				if(meth.getName().equals(packageName + "." + unresolvedMethod))
					return meth;
			}
		}
		
		return null;
	}
	
	public Method recursiveResolveMethodCall(String unresolved, Clazz clazz) {
		Method resolved = clazz.hasUnresolvedMethod(unresolved);
		if(resolved != null)
			return resolved;
		else if (resolved == null && clazz.getSuperClazz() != null)
			return recursiveResolveMethodCall(unresolved, clazz.getSuperClazz());
		else
			return null;
	}
	
	public boolean resolveClazzes() {
		for(Clazz clazz: callGraph.getAllClazzes()) {
			resolveClazz(clazz);
		}
		return true;
	}
	
	public void resolveClazz(Clazz clazz) {
		// Get all classes in the package
		List<File> pkgFiles = getFilesInPackage(clazz.getFile().getFilePackage());
		// Get all classes that are imported
		List<File> impFiles = getFilesInImports(clazz.getFile().getFileImports());

		// Combine the two lists to search over all reachable clazzes
		List<File> files = pkgFiles;
		for(File file: impFiles)
			files.add(file);

		// Resolve the clazz's super clazz if any
		if(clazz.getUnresolvedSuperClazz() != "") {
			Clazz resolvedSuperClazz = null;
			for(File file: files) {
				//Resolve the super clazz
				resolvedSuperClazz = file.hasUnresolvedClazz(clazz.getUnresolvedSuperClazz());
				if(resolvedSuperClazz != null) {
					clazz.setSuperClazz(resolvedSuperClazz);
					resolvedSuperClazz.addSubClazz(clazz);
					clazz.removeUnresolvedSuperClazz();
					return;
				}
			}
		}
		
		// Resolve the clazz's interfaces
		if(clazz.getUnresolvedInterfaces().size() != 0) {
			// Resolve all the interfaces for this clazz
			Clazz resolvedInterface;
			for(String unresolved: clazz.getUnresolvedInterfaces()) {
				for(File file: files) {
					resolvedInterface = file.hasUnresolvedInterface(unresolved);
					if(resolvedInterface != null) {
						clazz.addInterface(resolvedInterface);
						clazz.removeUnresolvedInterface(unresolved);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * This returns a list of all clazzes that are contained
	 * inside of the given package.
	 * @param pkg
	 * @return
	 */
	public List<Clazz> getClazzesInPackage(String pkg) {
		List<Clazz> clazzes = new ArrayList<Clazz>();
		
		for(Clazz clazz: callGraph.getAllClazzes()) {
			if(clazz.getFile().getFilePackage().equals(pkg))
				clazzes.add(clazz);
		}
		
		return clazzes;
	}
	
	/**
	 * This return a list of all files that are in the
	 * supplied package name.
	 * @param pkg
	 * @return
	 */
	public List<File> getFilesInPackage(String pkg) {
		List<File> files = new ArrayList<File>();
		
		for(File file: callGraph.getAllFiles()) {
			if(file.getFilePackage().equals(pkg))
				files.add(file);
		}
		
		return files;
	}
	
	/**
	 * This will return a list of clazzes that are inside the
	 * imported packages.
	 * @param imports
	 * @return
	 */
	public List<Clazz> getClazzesInImports(List<String> imports) {
		List<Clazz> clazzes = new ArrayList<Clazz>();
		
		for(Clazz clazz: callGraph.getAllClazzes()) {
			for(String imp: imports) {
				if(clazz.getName().equals(imp) || clazz.getFile().getFilePackage().equals(imp))
					clazzes.add(clazz);
			}
		}
		
		return clazzes;
	}
	
	/**
	 * This will return a list of files that are inside the
	 * imported packages.
	 * @param imports
	 * @return
	 */
	public List<File> getFilesInImports(List<String> imports) {
		List<File> files = new ArrayList<File>();
		
		for(File file: callGraph.getAllFiles()) {
			for(String imp: imports) {
				if(file.getFilePackage().equals(imp))
					files.add(file);
			}
		}
		
		return files;
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
	
	/**
	 * Looks up which Method Object is being called based on the invocation string
	 * and the current class.
	 * @param methodInvocation
	 * @param invokingClass
	 * @return invokedMethod Object
	 */
	public Method lookupInvokedMethod(String methodInvocation, Clazz invokingClass)
	{
		// 	go through imported classes and match with the invoking class
		//		if not found, go through package classes
		//			once found class, look upwards from method for function defn
		List<String> imports = invokingClass.getFile().getFileImports();
		String objStr = methodInvocation.substring(0, methodInvocation.lastIndexOf("."));
		for (String s : imports)
		{
			// grab the object (ie System.out.println() ==> System.out
			if (s.equals(objStr))
			{
				// we know it's this import
				for (Clazz c : callGraph.getAllClazzes())
				{
					if (c.getName().equals(s))
					{
						// Found the right class
						return lookupMethodCallInClass(c, methodInvocation.substring(methodInvocation.lastIndexOf(".")));
					}
				}
			}
		}
		// otherwise it's in the package classes maybe
		for (Clazz packageClazz : getClazzesInPackage(invokingClass.getFile().getFilePackage()))
		{
			if (packageClazz.getName().equals(objStr))
			{
				// it's this class.
				for (Clazz c : callGraph.getAllClazzes())
				{
					if (c.getName().equals(packageClazz.getName()))
					{
						// Found the right class
						return lookupMethodCallInClass(c, methodInvocation.substring(methodInvocation.lastIndexOf(".")));
					}
				}
			}
		}
		// The method doesn't exist.
		return null;
	}
	
	public Method lookupMethodCallInClass(Clazz clazz, String methodCall)
	{
		for (;clazz != null;clazz=clazz.getSuperClazz())
		{
			for (Method currentMethod : clazz.getMethods())
			{
				if (currentMethod.getName().equals(methodCall))
				{
					return currentMethod;
				}
			}
		}
		return null;
	}
}
