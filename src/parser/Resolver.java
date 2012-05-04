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
			resolveAllClazzMethodCalls(clazz);
		}
		return true;
	}
	
	public boolean resolveAllClazzMethodCalls(Clazz clazz) {
		for(Method m: clazz.getMethods()) {
			resolveMethodCalls(m);
		}
		return true;
	}
	
	public boolean resolveMethodCalls(Method method) {
		// Get all classes in the package
		List<Clazz> pkgClazzes = getClazzesInPackage(method.getClazz().getFile().getFilePackage());
		// Get all classes that are imported
		List<Clazz> impClazzes = getClazzesInImports(method.getClazz().getFile().getFileImports());
		
		// Combine the two lists to search over all reachable clazzes
		List<Clazz> clazzes = pkgClazzes;
		for(Clazz clazz: impClazzes)
			clazzes.add(clazz);
		
		
		// Resolve all the method calls if they are in the project's classes
		Method resolved;
		for(String unresolved: method.getUnresolvedMethods()) {
			for(Clazz clazz: clazzes) {
				resolved = clazz.hasUnresolvedMethod(unresolved);
				if(resolved != null) {
					method.addMethodCall(resolved);
					resolved.addCalledBy(method);
					method.removeUnresolvedMethod(unresolved);
					return true;
				}
			}
		}
		
		return false;
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
	 * This will return a list of clazzes that are inside the
	 * imported packages.
	 * TODO This function does not work as intended yet.
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

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
}
