package parser;

import java.util.ArrayList;
import java.util.List;

import callgraphanalyzer.Resources;
import models.CallGraph;
import models.Clazz;
import models.File;
import models.Method;

public class Resolver {
	
	CallGraph callGraph;
	
	public Resolver(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
	
	public void resolveAll() {
		resolveClazzes();
		resolveReturnTypesAndParameters();
		resolveMethods();
	}
	
	/*********************************************************
	 * This section is for resolving return types of methods
	 * and for resolving fully qualified names of parameters.
	 ********************************************************/
	private boolean resolveReturnTypesAndParameters() {
		for(File file: callGraph.getAllFiles())
			resolveFileReturnTypesAndParameters(file);
		return true;
	}
	
	private boolean resolveFileReturnTypesAndParameters(File file) {
		for(Clazz clazz: file.getFileClazzes())
			resolveClassReturnTypesAndParameters(file, clazz);
		return true;
	}
	
	private boolean resolveClassReturnTypesAndParameters(File file, Clazz clazz) {
		for(Method m: clazz.getMethods())
			resolveMethodReturnTypeAndParameters(file, clazz, m);
		return true;
	}
	
	public boolean resolveMethodReturnTypeAndParameters(File file, Clazz clazz, Method method) {
		// Resolve the return type
		if(method.getReturnType() != null && !Resources.isLiteral(method.getReturnType())) {
			Clazz returnType = callGraph.lookupUnqualifiedClassName(clazz, method.getReturnType());
			if(returnType != null)
				method.setReturnType(returnType.getName());
		}
		
		// Resolve the parameter types
		String[] parameters = method.getName().substring(method.getName().lastIndexOf("(")+1,
				method.getName().lastIndexOf(")")).split(",");
		List<String> resolvedParameters = new ArrayList<String>();
		for(String param: parameters) {
			if(!Resources.isLiteral(param.trim())) {
				if(callGraph.lookupUnqualifiedClassName(clazz, param.trim()) != null)
					resolvedParameters.add(
							callGraph.lookupUnqualifiedClassName(clazz, param.trim()).getName());
				else
					resolvedParameters.add(param.trim());
			}
			else
				resolvedParameters.add(param.trim());
		}
		
		String methodName = method.getName().substring(0, method.getName().lastIndexOf("(")+1);
		for(String par: resolvedParameters)
			methodName += par + ", ";
		if(resolvedParameters.size() != 0)
			methodName = methodName.substring(0, methodName.length()-2);
		methodName += ")";
		
		method.setName(methodName);
		
		return true;
	}
	
	/*********************************************************
	 * This section is for resolving method invocations
	 ********************************************************/
	
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
		for(Method m: clazz.getMethods())
			resolveMethodCalls(file, clazz, m);
		
		return true;
	}
	
	public boolean resolveMethodCalls(File file, Clazz clazz, Method method) {
		RVisitor rvisitor = new RVisitor(this, callGraph, clazz, method);
		method.getNode().accept(rvisitor);
		
		return true;
	}
	
	/*********************************************************
	 * This section is for resolving class super classes and
	 * interfaces with fully qualified names.
	 ********************************************************/
	
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
			Clazz inter = callGraph.lookupUnqualifiedClassName(clazz, i);
			if(inter != null && inter.isInterface()) {
				clazz.getInterfaces().add(inter);
				inter.getImplementedBy().add(clazz);
			}
		}
		
		// Resolve super clazz
		if(clazz.getUnresolvedSuperClazz() != null) {
			Clazz superClazz = callGraph.lookupUnqualifiedClassName(clazz, clazz.getUnresolvedSuperClazz());
			if(superClazz != null)
				clazz.setSuperClazz(superClazz);
		}
		
		return true;
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
}
