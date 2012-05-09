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
	
	/**
	 * This function will resolve all unresolved exprezzions in a 
	 * given method.
	 * @param file
	 * @param clazz
	 * @param method
	 * @return
	 */
	public boolean resolveMethodCalls(File file, Clazz clazz, Method method) {
		
		for(int i = method.getUnresolvedExprezzions().size()-1; i >= 0; i--) {
			Exprezzion exprezzion = method.getUnresolvedExprezzions().get(i);
			
			// Start resolving expressions now
			
			// Handle case where it is a variable
			if(exprezzion.getExpression() != null && exprezzion.getMethodCall() == null) {
				// Handle if it is already resolved
				if(exprezzion.getResolvedType() != null) {
					continue;
				}
				// If the variable is not resolved, we have big problems
				if(exprezzion.getResolvedType() == null) {
					System.err.println("A variable was not resolved in parse time.");
					return false;
				}
			}
			
			// Handle case where it is a local method
			else if(exprezzion.getExpression() == null && exprezzion.getMethodCall() != null) {
				String unresolved = exprezzion.getMethodCall() + "(";
				List<String> params = resolveParameters(method, exprezzion);
				for(String param: params) {
					unresolved += param + ", ";
				}
				unresolved = unresolved.substring(0, unresolved.length()-2);
				unresolved += ")";
				
				Method resolved = clazz.hasUnresolvedMethod(unresolved);
				if(resolved != null) {
					resolved.addCalledBy(method);
					method.addMethodCall(resolved);
					exprezzion.setResolvedType(resolved.getReturnType());
				}
			}
			
			// Handle the case where it is an external method
			// This is the most complex case
			else if(exprezzion.getExpression() != null && exprezzion.getMethodCall() != null) {
				
			}
		}
		
		return true;
	}
	
	private String resolveExpression(Method method, Exprezzion exprezzion) {
		for(int i = method.getUnresolvedExprezzions().size()-1; i >= 0; i--) {
			Exprezzion resolved = method.getUnresolvedExprezzions().get(i);
			
			// If the resolved exprezzion we are looking at is not resolved then exit
			if(resolved.getResolvedType().equals("")) {
				System.out.println("Could not resolve exprezzion");
				break;
			}
			
			// Combine the tested expression and method call
			String exp = "";
			if(!resolved.getExpression().equals("")) {
				exp += resolved.getExpression();
				if(!resolved.getMethodCall().equals("")) {
					exp += "." + resolved.getMethodCall();
				}
			}
			else {
				if(!resolved.getMethodCall().equals("")) {
					exp += resolved.getMethodCall();
				}
			}
			exp += "(";
			List<String> params = resolveParameters(method, exprezzion);
			for(String param: params) {
				exp += param + ", ";
			}
			exp = exp.substring(0, exp.length()-2);
			exp += ")";
			
			// Check to see if that is the expression we are looking for
			if(exp.equals(exprezzion.getExpression())) {
				return resolved.getResolvedType();
			}
		}
		
		return null;
	}
	
	private List<String> resolveParameters(Method method, Exprezzion exprezzion) {
		List<String> resolvedParams = new ArrayList<String>();
		for(int i = exprezzion.getParameters().size()-1; i >= 0; i--) {
			Exprezzion resolved = exprezzion.getParameters().get(i);
			
			resolvedParams.add(resolveExpression(method, resolved));
		}
		
		return resolvedParams;
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
}
