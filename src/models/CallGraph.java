package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
		
		printResolveStatistics();
	}
	
	/**
	 * This returns a list of all clazzes that are contained
	 * inside of the given package.
	 * @param pkg
	 * @return
	 */
	public List<Clazz> getClazzesInPackage(String pkg) {
		List<Clazz> clazzes = new ArrayList<Clazz>();
		
		for(Clazz clazz: getAllClazzes()) {
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
			
			for(Clazz clazz: getAllClazzes()) {
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
		
		for(File file: getAllFiles()) {
			if(file.getFilePackage().equals(pkg))
				files.add(file);
		}
		
		return files;
	}
	
	public Clazz lookupUnqualifiedClassName(Clazz clazz, String className) {
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
	
	public void printResolveStatistics() {
		int resolved = 0;
		int total = 0;
		Iterator it = methods.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        Method m = (Method) pairs.getValue();
	        if (!m.getClazz().getName().matches("java.util.*")) {
	        	resolved += m.getMethodCalls().size();
	        	resolved += m.getFuzzyCalls().size();
	        	total += m.getMethodCalls().size();
	        	total += m.getUnresolvedCalls().size();
	        	total += m.getFuzzyCalls().size();
	        }
	    }
		
		System.out.println("TOTAL RESOLVED CALLS: " + resolved);
		System.out.println("TOTAL CALLS:          " + total);
		System.out.println();
		System.out.println("ABLE TO RESOLVE: " + ((double)resolved/(double)total)*100 + "% OF ALL CALLS");
	}
	
	private String stripGenericParameters(String className) {
		return className.substring(0, className.indexOf("<"));
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
