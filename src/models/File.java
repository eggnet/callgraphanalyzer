package models;

import java.util.ArrayList;
import java.util.List;

public class File {
	
	private String			fileName;

	private List<String> 	fileImports;
	private String 			filePackage;
	private List<Clazz> 	fileClazzes;
	private List<Clazz> 	fileInterfaces;
	
	
	public File() {
		fileImports = new ArrayList<String>();
		fileClazzes = new ArrayList<Clazz>();
		fileInterfaces = new ArrayList<Clazz>();
	}

	public File(List<String> fileImports, String filePackage,
			List<Clazz> fileClazzes, List<Clazz> fileInterfaces) {
		super();
		this.fileImports = fileImports;
		this.filePackage = filePackage;
		this.fileClazzes = fileClazzes;
		this.fileInterfaces = fileInterfaces;
	}
	
	public void print() {
		System.out.println("FILE: " + fileName);
        System.out.println("  Package: " + filePackage);
        System.out.println("  Imports: ");
        for(String imp: fileImports)
        	System.out.println("    " + imp);
        for(Clazz clazz: fileClazzes)
        	clazz.print();
	}
	
	public void addFileImport(String fileImport) {
		this.fileImports.add(fileImport);
	}
	
	public void addClazz(Clazz clazz) {
		this.fileClazzes.add(clazz);
	}
	
	public void addInterface(Clazz clazz) {
		this.fileInterfaces.add(clazz);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getFileImports() {
		return fileImports;
	}
	
	public void setFileImports(List<String> fileImports) {
		this.fileImports = fileImports;
	}
	
	public String getFilePackage() {
		return filePackage;
	}
	
	public void setFilePackage(String filePackage) {
		this.filePackage = filePackage;
	}
	
	public List<Clazz> getFileClazzes() {
		return fileClazzes;
	}
	
	public void setFileClazzes(List<Clazz> fileClazzes) {
		this.fileClazzes = fileClazzes;
	}
	
	public List<Clazz> getFileInterfaces() {
		return fileInterfaces;
	}
	
	public void setFileInterfaces(List<Clazz> fileInterfaces) {
		this.fileInterfaces = fileInterfaces;
	}
}

