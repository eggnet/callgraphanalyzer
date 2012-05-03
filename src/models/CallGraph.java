package models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CallGraph {
	
	private Map<String, File> 	files;
	private Map<String, Clazz> 	clazzes;
	private Map<String, Method> methods;
	
	public CallGraph() {
		clazzes = new HashMap<String, Clazz>();
		methods = new HashMap<String, Method>();
		files = new HashMap<String, File>();
	}
	
	public CallGraph(Map<String, Clazz> clazzes, Map<String, Method> methods,
			Map<String, File> files) {
		super();
		this.clazzes = clazzes;
		this.methods = methods;
		this.files = files;
	}
	
	public void addFile(File file) {
		files.put(file.getFileName(), file);
	}
	
	public void addClazz(Clazz clazz) {
		clazzes.put(clazz.getName(), clazz);
	}
	
	public void addMethod(Method method) {
		methods.put(method.getName(), method);
	}
	
	public File containsFile(String name) {
		return files.get(name);
	}
	
	public Clazz containsClazz(String name) {
		return clazzes.get(name);
	}
	
	public Method containsMethod(String name) {
		return methods.get(name);
	}
	
	public void print() {
		Iterator it = files.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        File currentFile = (File) pairs.getValue();
	        currentFile.print();
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	public Map<String, Clazz> getClazzes() {
		return clazzes;
	}

	public void setClazzes(Map<String, Clazz> clazzes) {
		this.clazzes = clazzes;
	}

	public Map<String, Method> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, Method> methods) {
		this.methods = methods;
	}

	public Map<String, File> getFiles() {
		return files;
	}

	public void setFiles(Map<String, File> files) {
		this.files = files;
	}
}
