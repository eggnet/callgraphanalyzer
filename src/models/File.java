package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class File
{

	private String			fileName;
	
	private int				startChar;
	private int				endChar;

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
	
	public void insertOwner(Change change) {
		Set<Change> changes = Owners.get(change.getOwnerId());
		// The owner is new
		if(changes == null) {
			Set<Change> newOwner = new HashSet<Change>();
			newOwner.add(change);
			this.Owners.put(change.getOwnerId(), newOwner);
		}
		// The owner already existed
		else {
			changes.add(change);
		}
	}
	
	public void updateOwnership(Change change) {
		// Get the intersection
		Set<Change> intersection = getOwnershipIntersection(change.getCharStart(), change.getCharEnd());
		
		// Clean up and insert
		if(!intersection.isEmpty()) {
			ownershipCleanUp(change.getCharStart(), change.getCharEnd(), intersection);
		}
		insertOwner(change);
	}
	
	public Set<Change> getOwnershipIntersection(int start, int end) {
		Set<Change> intersection = new HashSet<Change>();
		
		// Find out what owners intersect with the new owner change
		for(Map.Entry<String, Set<Change>> entry: this.Owners.entrySet()) {
			for(Change change: entry.getValue()) {
				if(intersectionOfCode(start, end, change.getCharStart(), change.getCharEnd()))
					intersection.add(change);
			}
		}
		
		return intersection;
	}
	
	public boolean intersectionOfCode(int start1, int end1, int start2, int end2) {
		return (start2 < start1 && end2 >= start1 ||
		   (start2 >= start1 && end2 <= end1)  ||
		   (start2 <= end1 && end2 > end1)  ||
		   (start2 <= start1 && end2 >= end1));
	}
	
	public void ownershipCleanUp(int start, int end, Set<Change> intersections) {
		for(Iterator<Change> intersectIter = intersections.iterator(); intersectIter.hasNext();) {
			Change intersect = intersectIter.next();
			// Case 1
			if(intersect.getCharStart() < start 
					&& (intersect.getCharEnd() >= start && intersect.getCharEnd() < end)) {
				intersect.setCharEnd(start-1);
			}
			// Case 2
			else if((intersect.getCharStart() >= start && intersect.getCharStart() < end) 
					&& (intersect.getCharEnd() > start && intersect.getCharEnd() <= end)) {
				intersectIter.remove();
			}
			// Case 3
			else if((intersect.getCharStart() > start && intersect.getCharEnd() <= end)
					&& intersect.getCharEnd() > end) {
				intersect.setCharStart(end+1);
			}
			// Case 4
			else if(intersect.getCharStart() < start && intersect.getCharEnd() > end) {
				// We need to split this intersect owner
				Change split1 = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
										   intersect.getChangeType(), intersect.getFileId(), 
										   intersect.getCharStart(), intersect.getCharEnd());
				Change split2 = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
						   				   intersect.getChangeType(), intersect.getFileId(), 
						   				   intersect.getCharStart(), intersect.getCharEnd());
				
				split1.setCharEnd(start-1);
				split2.setCharStart(end+1);
				intersectIter.remove();
				intersections.add(split1);
				intersections.add(split2);
			}
		}
	}
	
	public int getMethodWeight(String owner, Method method) {
		// Get all ranges of ownerhsip
		Set<Change> ranges = this.Owners.get(owner);
		if(ranges.isEmpty())
			return -1;
		
		for(Change range: ranges) {
			if(intersectionOfCode(method.getstartChar(), method.getendChar(),
					range.getCharStart(), range.getCharEnd())) {
				// Get the weight here
			}
		}
		
		return -1;
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

	public int getStartChar()
	{
		return startChar;
	}

	public void setStartChar(int startChar)
	{
		this.startChar = startChar;
	}

	public int getEndChar()
	{
		return endChar;
	}

	public void setEndChar(int endChar)
	{
		this.endChar = endChar;
	}
}
