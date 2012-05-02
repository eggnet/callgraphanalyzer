package models;

import java.util.List;

public class File {

	public List<String> fileImports;
	public String 		filePackage;
	public List<Clazz> 	fileClazzes;
	public List<Clazz> 	fileInterfaces;
	
	
	public File() {
	}

	public File(List<String> fileImports, String filePackage,
			List<Clazz> fileClazzes, List<Clazz> fileInterfaces) {
		super();
		this.fileImports = fileImports;
		this.filePackage = filePackage;
		this.fileClazzes = fileClazzes;
		this.fileInterfaces = fileInterfaces;
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

