package parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;

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
		RVisitor rvisitor = new RVisitor(this, callGraph, clazz, method);
		method.getNode().accept(rvisitor);
		
		return true;
	}
	
	public boolean resolveClazzes() {
		for(Clazz clazz: callGraph.getAllClazzes()) {
			resolveClazz(clazz);
		}
		return true;
	}
	
	private boolean resolveClazz(Clazz clazz) {
		File file = clazz.getFile();
		
		// Resolve all interfaces
		for(String i: clazz.getUnresolvedInterfaces()) {
			Clazz inter = lookupClassName(clazz, i);
			if(inter != null && inter.isInterface())
				clazz.getInterfaces().add(inter);
		}
		
		// Resolve super clazz
		if(clazz.getUnresolvedSuperClazz() != null) {
			Clazz superClazz = lookupClassName(clazz, clazz.getUnresolvedSuperClazz());
			if(superClazz != null)
				clazz.setSuperClazz(superClazz);
		}
		
		return true;
	}
	
	private String stripGenericParameters(String className) {
		return className.substring(0, className.indexOf("<"));
	}
	
	private Clazz lookupClassName(Clazz clazz, String className) {
		if(className.contains("<") && className.contains(">"))
			className = stripGenericParameters(className);
		// Look through the package for the class name
		for (Clazz packageClazz : getClazzesInPackage(clazz.getFile().getFilePackage())) {
			if(packageClazz.hasUnqualifiedName(className))
				return packageClazz;
		}
		
		// Look through the imports for the class name
		List<Clazz> imports = getClazzesInImports(clazz.getFile().getFileImports());
		for (Clazz s : imports) {
			if(s.hasUnqualifiedName(className))
				return s;
		}
		
		// Check the current class for the name
		if(clazz.hasUnqualifiedName(className))
			return clazz;
		
		return null;
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
	 * @param imports
	 * @return
	 */
	public List<Clazz> getClazzesInImports(List<String> imports) {
		Clazz tc = null;
		try {
			List<Clazz> clazzes = new ArrayList<Clazz>();
			
			for(Clazz clazz: callGraph.getAllClazzes()) {
				tc = clazz;
				for(String imp: imports) {
					tc = clazz;
					if(clazz.getName().equals(imp) || clazz.getFile().getFilePackage().equals(imp))
						clazzes.add(clazz);
				}
			}
			
			return clazzes;
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
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

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
}
