package parser;

import java.util.ArrayList;
import java.util.List;

import models.*;

public class Resolver {
	
	CallGraph callGraph;
	
	public Resolver(CallGraph callGraph) {
		this.callGraph = callGraph;
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
		
		
		// Resolve all the method calls
		Method resolved;
		for(String unresolved: method.getUnresolvedMethods()) {
			for(Clazz clazz: pkgClazzes) {
				resolved = clazz.hasUnresolvedMethod(unresolved);
				if(resolved != null) {
					method.addMethodCall(resolved);
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
	public List<Clazz> getClazzesInImports(String imports) {
		List<Clazz> clazzes = new ArrayList<Clazz>();
		
		for(Clazz clazz: callGraph.getAllClazzes()) {
			if(clazz.getFile().getFilePackage().equals(imports))
				clazzes.add(clazz);
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
