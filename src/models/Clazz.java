package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Clazz {
	
	public String 			name;
	public boolean 			isInterface;
	public List<Method> 	methods;
	public ArrayList<Clazz> interfaces;
	public Clazz 			superClazz;
	public List<Clazz> 		subClazzes;

	public Clazz() {
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

	public ArrayList<Clazz> getInterfaces() {
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
	
	
	
	}

