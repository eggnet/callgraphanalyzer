package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class File
{

	private String			fileName;

	private List<String>	fileImports;
	private String			filePackage;
	private List<Clazz>		fileClazzes;
	private List<Clazz>		fileInterfaces;
	public Map<String, Set<Change>> Owners;
	public File()
	{
		fileImports = new ArrayList<String>();
		fileClazzes = new ArrayList<Clazz>();
		fileInterfaces = new ArrayList<Clazz>();
		filePackage = "";
		Owners = new HashMap<String, Set<Change>>();
	}

	public File(List<String> fileImports, String filePackage,
			List<Clazz> fileClazzes, List<Clazz> fileInterfaces)
	{
		super();
		this.fileImports = fileImports;
		this.filePackage = filePackage;
		this.fileClazzes = fileClazzes;
		this.fileInterfaces = fileInterfaces;
	}

	public void print()
	{
		if (!this.filePackage.equals("java.util"))
		{
			System.out.println("FILE: " + fileName);
			System.out.println("  Package: " + filePackage);
			System.out.println("  Imports: ");
			for (String imp : fileImports)
				System.out.println("    " + imp);
			for (Clazz clazz : fileClazzes)
				clazz.print();
		}
	}

	public void addFileImport(String fileImport)
	{
		this.fileImports.add(fileImport);
	}

	public void addClazz(Clazz clazz)
	{
		this.fileClazzes.add(clazz);
	}

	public void addInterface(Clazz clazz)
	{
		this.fileInterfaces.add(clazz);
	}

	public Clazz hasUnresolvedClazz(String c)
	{
		for (Clazz clazz : fileClazzes)
		{
			String unresolved = clazz.getName();
			unresolved = unresolved.substring(unresolved.lastIndexOf(".") + 1,
					unresolved.length());

			if (c.equals(unresolved))
			{
				System.out.println("Found " + c + " in class "
						+ this.getFileName());
				return clazz;
			}
		}

		return null;
	}

	public Clazz hasUnresolvedInterface(String c)
	{
		for (Clazz clazz : fileInterfaces)
		{
			String unresolved = clazz.getName();
			unresolved = unresolved.substring(unresolved.lastIndexOf(".") + 1,
					unresolved.length());

			if (c.equals(unresolved))
			{
				System.out.println("Found " + c + " in class "
						+ this.getFileName());
				return clazz;
			}
		}

		return null;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public List<String> getFileImports()
	{
		return fileImports;
	}

	public void setFileImports(List<String> fileImports)
	{
		this.fileImports = fileImports;
	}

	public String getFilePackage()
	{
		return filePackage;
	}

	public void setFilePackage(String filePackage)
	{
		this.filePackage = filePackage;
	}

	public List<Clazz> getFileClazzes()
	{
		return fileClazzes;
	}

	public void setFileClazzes(List<Clazz> fileClazzes)
	{
		this.fileClazzes = fileClazzes;
	}

	public List<Clazz> getFileInterfaces()
	{
		return fileInterfaces;
	}

	public void setFileInterfaces(List<Clazz> fileInterfaces)
	{
		this.fileInterfaces = fileInterfaces;
	}
}
