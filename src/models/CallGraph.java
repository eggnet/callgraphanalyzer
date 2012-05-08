package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public List<File> getAllFiles() {
		List<File> files = new ArrayList<File>();
		
		for(Map.Entry<String, File> entry: this.files.entrySet()) {
			files.add(entry.getValue());
		}
		
		return files;
	}
	
	public List<Clazz> getAllClazzes() {
		List<Clazz> clazzes = new ArrayList<Clazz>();
		
		for(Map.Entry<String, Clazz> entry: this.clazzes.entrySet()) {
			clazzes.add(entry.getValue());
		}
		
		return clazzes;
	}
	
	/**
	 * This function will return a list of methods that are inside the given
	 * file name and that are involved in the start and end character locations.
	 * @param fileName
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Method> getMethodsUsingCharacters(String fileName, int start, int end) {
		List<Method> m = new ArrayList<Method>();
		
		File file = containsFile(fileName);
		if(file == null)
			return m;
		else {
			for(Clazz clazz: file.getFileClazzes()) {
				for(Method method: clazz.getMethods()) {
					if((method.getStartLine() <= start && method.getEndLine() >= end) ||
					   (method.getStartLine() <= start && method.getEndLine() >= start) ||
					   (method.getStartLine() >= start && method.getEndLine() <= end) ||
					   (method.getStartLine() <= end   && method.getEndLine() >= end)) {
						m.add(method);
					}
				}
			}
		}
		
		return m;
	}
	
	public void print() {
		Iterator it = files.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        File currentFile = (File) pairs.getValue();
	        currentFile.print();
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
