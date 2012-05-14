package models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class Clazz {
	
	private File					file;
	private String 					name;
	private boolean 				isInterface;
	private List<Method> 			methods;
	private List<Clazz> 			subClazzes;
	
	private List<Clazz> 			interfaces;
	private List<String> 			unresolvedInterfaces;
	
	private Clazz 					superClazz;
	private String 					unresolvedSuperClazz;
	
	private List<Mapping>			variables;
	
	private List<MethodInvocation> 	invocations;

	public Clazz() {
		methods = new ArrayList<Method>();
		interfaces = new ArrayList<Clazz>();
		unresolvedInterfaces = new ArrayList<String>();
		subClazzes = new ArrayList<Clazz>();
		unresolvedSuperClazz = "";
		
		variables = new ArrayList<Mapping>();
		
		invocations = new ArrayList<MethodInvocation>();
		
	}

	public Clazz(String name, boolean isInterface, List<Method> methods,
			ArrayList<Clazz> interfaces, Clazz superClazz,
			List<Clazz> subClazzes) {
		this.name = name;
		this.isInterface = isInterface;
		this.methods = methods;
		this.interfaces = interfaces;
		this.superClazz = superClazz;
		this.subClazzes = subClazzes;
		
		unresolvedInterfaces = new ArrayList<String>();
		unresolvedSuperClazz = "";
		
		variables = new ArrayList<Mapping>();
		
		invocations = new ArrayList<MethodInvocation>();
	}
	
	public String lookupField(String variable) {
		for(Clazz clazz = this; clazz != null; clazz = clazz.getSuperClazz()) {
			for(Mapping map: clazz.variables) {
				if(map.getVarName().equals(variable))
					return map.getType();
			}
		}
		
		return null;
	}
	
	public boolean hasUnqualifiedName(String unqualifiedName) {
		String shortC = unqualifiedName;
		if(shortC.contains("."))
			shortC = shortC.substring(shortC.lastIndexOf(".")+1);
		if(this.name.substring(this.name.lastIndexOf(".")+1).equals(shortC))
			return true;
		else
			return false;
	}
	
	public Method hasUnqualifiedMethod(String unqualifiedMethod) {
		String shortM = unqualifiedMethod;
		shortM = shortM.substring(shortM.lastIndexOf(".")+1);
		for(Clazz clazz = this; clazz != null; clazz = clazz.getSuperClazz()) {
			for(Method method: clazz.methods) {
				String unresolved = method.getName();
				unresolved = unresolved.substring(unresolved.lastIndexOf(".")+1);
				if(unresolved.equals(shortM))
					return method;
				// Handle the case where parameters are null
				else if(shortM.contains("null")) {
					String[] unqualifiedParams = shortM.substring(shortM.lastIndexOf("(")+1, 
							shortM.lastIndexOf(")")).split(",");
					String[] methodParams = unresolved.substring(unresolved.lastIndexOf("(")+1, 
							unresolved.lastIndexOf(")")).split(",");
					boolean isMethod = true;
					int i;
					for(i = 0; i < methodParams.length; i++) {
						if(!unqualifiedParams[i].equals("null") && 
								!methodParams[i].equals(unqualifiedParams[i])) {
								isMethod = false;
						}
					}
					if(methodParams.length != unqualifiedParams.length)
						isMethod = false;
					if(isMethod)
						return method;
				}
			}
		}
		return null;
	}
	
	public void print() {
		System.out.println("  CLASS: " + name);
		System.out.println("    Interface: " + isInterface);
		System.out.println("    Implements: ");
		for(Clazz interf: interfaces)
			System.out.println("      " + interf.getName());
		System.out.println("    Unresolved Interfaces: ");
		for(String i: unresolvedInterfaces)
			System.out.println("      " + i);
		System.out.print("    Super Class: ");
		if(superClazz != null)
			System.out.println(superClazz.getName());
		else
			System.out.println("");
		System.out.println("    Unresolved Super Class: " + unresolvedSuperClazz);
		System.out.println("    Sub Classes: ");
		for(Clazz clazz: subClazzes)
			System.out.println("      " + clazz.getName());
		System.out.println("    Fields: ");
		for(Mapping map: variables) 
			System.out.println("      " + map.getType() + ": " + map.getVarName());
		for(Method m: methods)
			m.print();
	}
	
	public void addMethod(Method m) {
		this.methods.add(m);
	}
	
	public void addUnresolvedInterface(String i) {
		this.unresolvedInterfaces.add(i);
	}
	
	public void removeUnresolvedInterface(String i) {
		this.unresolvedInterfaces.remove(i);
	}
	
	public void addUnresolvedSuperClazz(String s) {
		this.unresolvedSuperClazz = s;
	}
	
	public void removeUnresolvedSuperClazz() {
		this.unresolvedSuperClazz = "";
	}
	
	public void addInterface(Clazz clazz) {
		this.interfaces.add(clazz);
	}
	
	public void addSubClazz(Clazz clazz) {
		this.subClazzes.add(clazz);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	public List<Clazz> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(ArrayList<Clazz> interfaces) {
		this.interfaces = interfaces;
	}

	public Clazz getSuperClazz() {
		return superClazz;
	}

	public void setSuperClazz(Clazz superClazz) {
		this.superClazz = superClazz;
	}

	public List<Clazz> getSubClazzes() {
		return subClazzes;
	}

	public void setSubClazzes(List<Clazz> subClazzes) {
		this.subClazzes = subClazzes;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getUnresolvedSuperClazz() {
		return unresolvedSuperClazz;
	}

	public void setUnresolvedSuperClazz(String unresolvedSuperClazz) {
		this.unresolvedSuperClazz = unresolvedSuperClazz;
	}

	public List<String> getUnresolvedInterfaces() {
		return unresolvedInterfaces;
	}

	public void setUnresolvedInterfaces(List<String> unresolvedInterfaces) {
		this.unresolvedInterfaces = unresolvedInterfaces;
	}

	public List<Mapping> getVariables() {
		return variables;
	}

	public void setVariables(List<Mapping> variables) {
		this.variables = variables;
	}

	public List<MethodInvocation> getInvocations() {
		return invocations;
	}

	public void setInvocations(List<MethodInvocation> invocations) {
		this.invocations = invocations;
	}
}

