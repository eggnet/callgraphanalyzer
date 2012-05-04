package models;

import java.util.ArrayList;
import java.util.List;

public class Clazz {
	
	private File				file;
	private String 				name;
	private boolean 			isInterface;
	private List<Method> 		methods;
	private List<Clazz> 		subClazzes;
	
	private List<Clazz> 		interfaces;
	private List<String> 		unresolvedInterfaces;
	
	private Clazz 				superClazz;
	private String 				unresolvedSuperClazz;

	public Clazz() {
		methods = new ArrayList<Method>();
		interfaces = new ArrayList<Clazz>();
		unresolvedInterfaces = new ArrayList<String>();
		subClazzes = new ArrayList<Clazz>();
		unresolvedSuperClazz = "";
		
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
	}
	
	public void print() {
		System.out.println("  CLASS: " + name);
		System.out.println("    Interface: " + isInterface);
		System.out.println("    Implements: ");
		System.out.println("    Unresolved Interfaces: ");
		for(String i: unresolvedInterfaces)
			System.out.println("      " + i);
		for(Clazz interf: interfaces)
			System.out.println("      " + interf.getName());
		System.out.print("    Super Class: ");
		if(superClazz != null)
			System.out.println(superClazz.getName());
		else
			System.out.println("");
		System.out.println("    Unresolved Super Class: " + unresolvedSuperClazz);
		System.out.println("    Sub Classes: ");
		for(Clazz clazz: subClazzes)
			System.out.println("      " + clazz.getName());
		for(Method m: methods)
			m.print();
	}
	
	public void addMethod(Method m) {
		this.methods.add(m);
	}
	
	public Method hasUnresolvedMethod(String m) {
		for(Method method: methods) {
			String unresolved = method.getName();
			unresolved = unresolved.substring(unresolved.lastIndexOf(".")+1, unresolved.length());
			
			if(m.equals(unresolved)) {
				System.out.println("Found " + m + " in class " + this.getName());
				return method;
			}
		}
		
		return null;
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
	
	
}

