package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.Resources;

public class File
{

	private String			fileName;
	
	private int				startChar;
	private int				endChar;

	private List<String>	fileImports;
	private String			filePackage;
	private List<Clazz>		fileClazzes;
	private List<Clazz>		fileInterfaces;
	public Map<String, List<Change>> Owners;
	
	private int				shift;
	private Change			oldCommit;
	
	public File()
	{
		fileImports = new ArrayList<String>();
		fileClazzes = new ArrayList<Clazz>();
		fileInterfaces = new ArrayList<Clazz>();
		filePackage = "";
		Owners = new HashMap<String, List<Change>>();
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
			System.out.println("  Owners: ");
			for(Map.Entry<String, List<Change>> entry: this.Owners.entrySet()) {
				System.out.println("    " + entry.getKey() + ": ");
				for(Change change: entry.getValue()) {
					System.out.println("      " + change.getCharStart() + " - " + change.getCharEnd());
				}
			}
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
	
	public void newCommit() {
		this.shift = 0;
	}
	
	private void addOwnershipChange(Change change, List<Change> list) {
		int i = 0;
		for(Change lChange: list) {
			if(change.getCharStart() > lChange.getCharStart())
				i++;
		}
		
		list.add(i, change);
	}
	
	public void insertOwner(Change change) {
		List<Change> changes = Owners.get(change.getOwnerId());
		// The owner is new
		if(changes == null) {
			List<Change> newOwner = new ArrayList<Change>();
			newOwner.add(change);
			
			this.Owners.put(change.getOwnerId(), newOwner);
		}
		// The owner already existed
		else {
			addOwnershipChange(change, changes);
		}
	}
	
	public void updateOwnership(Change change) {		
		// Set up old commit tracker.
		if(oldCommit == null)
			oldCommit = change;
		// Reset the shift on new commit.
		if(!oldCommit.getCommitId().equals(change.getCommitId())) {
			newCommit();
			oldCommit = change;
		}
		
		// hack the add changetype to a modify insert.  Better way? TODO @braden
		if (change.getChangeType() == Resources.ChangeType.ADD)
			change.setChangeType(Resources.ChangeType.MODIFYINSERT);
		
		// Apply the shift
		change.setCharStart(change.getCharStart()+shift);
		change.setCharEnd(change.getCharEnd()+shift);
		
		// Get the intersection
		List<List<Change>> intersection = getOwnershipIntersection(change.getCharStart(), change.getCharEnd());
		
		// Clean up and insert
		if(!intersection.isEmpty()) {
			ownershipCleanUp(change, intersection);
		}
		if(change.getChangeType() != Resources.ChangeType.MODIFYDELETE)
			insertOwner(change);
		
		// Update the shift
		if(change.getChangeType() == Resources.ChangeType.MODIFYINSERT)
			this.shift += (change.getCharEnd() - change.getCharStart())+1;
		else if(change.getChangeType() == Resources.ChangeType.MODIFYDELETE)
			this.shift -= (change.getCharEnd() - change.getCharStart())+1;
		
		if(shift < 0)
			shift = 0;
	}
	
	public List<List<Change>> getOwnershipIntersection(int start, int end) {
		List<List<Change>> intersection = new ArrayList<List<Change>>();
		
		// Find out what owners intersect with the new owner change
		for(Map.Entry<String, List<Change>> entry: this.Owners.entrySet()) {
			for(Change change: entry.getValue()) {
				if(intersectionOfCode(change.getCharStart(), change.getCharEnd(), start, end))
					if(!intersection.contains(entry.getValue()))
						intersection.add(entry.getValue());
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
	
	public void ownershipCleanUp(Change change, List<List<Change>> intersections) {
		for(Iterator<List<Change>> intersectListIter = intersections.iterator(); intersectListIter.hasNext();) {
			List<Change> intersectList = intersectListIter.next();
			List<Change> addedChanges = new ArrayList<Change>();
			for(Iterator<Change> intersectIter = intersectList.iterator(); intersectIter.hasNext();) {
				Change intersect = intersectIter.next();
				// Case 1
				if(change.getCharStart() < intersect.getCharStart()
						&& (change.getCharEnd() >= intersect.getCharStart() && change.getCharEnd() < intersect.getCharEnd())) {
					if(change.getChangeType() == Resources.ChangeType.MODIFYDELETE) {
						intersect.setCharStart(change.getCharEnd()+1);
						if(intersect.getCharEnd() - intersect.getCharStart() <= 0)
							intersectIter.remove();
					}
					else if(change.getChangeType() == Resources.ChangeType.MODIFYINSERT) {
						shiftOwnershipBelowChange((change.getCharEnd() - intersect.getCharStart()), 
								intersect.getCharStart(), addedChanges);
					}
				}
				// Case 2
				else if((change.getCharStart() >= intersect.getCharStart() && change.getCharEnd() > intersect.getCharStart()) 
						&& (change.getCharStart() < intersect.getCharEnd() && change.getCharEnd() <= intersect.getCharEnd())) {
					// Split here
					Change split1 = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
							intersect.getChangeType(), intersect.getFileId(), 
							intersect.getCharStart(), intersect.getCharEnd());
					Change split2 = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
							intersect.getChangeType(), intersect.getFileId(), 
							intersect.getCharStart(), intersect.getCharEnd());
					Change split2Insert = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
							intersect.getChangeType(), intersect.getFileId(), 
							intersect.getCharStart(), intersect.getCharEnd());

					split1.setCharEnd(change.getCharStart()-1);
					split2.setCharStart(change.getCharEnd()+1);
					split2Insert.setCharStart(change.getCharStart()+1);
					
					if(change.getChangeType() == Resources.ChangeType.MODIFYDELETE) {
						intersectIter.remove();
						if(split1.getCharEnd() - split1.getCharStart() > 0)
							addOwnershipChange(split1, addedChanges);
						if(split2.getCharEnd() - split2.getCharStart() > 0)
							addOwnershipChange(split2, addedChanges);
					}
					else if(change.getChangeType() == Resources.ChangeType.MODIFYINSERT) {
						intersectIter.remove();
						if(split1.getCharEnd() - split1.getCharStart() > 0)
							addOwnershipChange(split1, addedChanges);
						if(split2Insert.getCharEnd() - split2Insert.getCharStart() > 0)
							addOwnershipChange(split2Insert, addedChanges);
						shiftOwnershipBelowChange((change.getCharEnd() - change.getCharStart()), 
								split2Insert.getCharStart(), addedChanges);
					}
					// Split can cause other problems so we need to start from
					// the top of the list again.
					intersectIter = intersectList.iterator();
				}
				// Case 3
				else if((change.getCharStart() > intersect.getCharStart() && change.getCharStart() <= intersect.getCharEnd())
						&& change.getCharEnd() > intersect.getCharEnd()) {
					if(change.getChangeType() == Resources.ChangeType.MODIFYDELETE) {
						intersect.setCharEnd(change.getCharStart()-1);
						if(intersect.getCharEnd() - intersect.getCharStart() <= 0)
							intersectIter.remove();
					}
					else if(change.getChangeType() == Resources.ChangeType.MODIFYINSERT) {
						// Split here
						Change split1 = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
								intersect.getChangeType(), intersect.getFileId(), 
								intersect.getCharStart(), intersect.getCharEnd());
						Change split2 = new Change(intersect.getOwnerId(), intersect.getCommitId(), 
								intersect.getChangeType(), intersect.getFileId(), 
								intersect.getCharStart(), intersect.getCharEnd());
						
						split1.setCharEnd(change.getCharStart()-1);
						split2.setCharStart(change.getCharStart());
						
						intersectIter.remove();
						if(split1.getCharEnd() - split1.getCharStart() > 0)
							addOwnershipChange(split1, addedChanges);
						if(split2.getCharEnd() - split2.getCharStart() > 0)
							addOwnershipChange(split2, addedChanges);
						shiftOwnershipBelowChange((change.getCharEnd() - change.getCharStart())+1, 
								split2.getCharStart(), addedChanges);
						
					}
				}
				// Case 4
				else if(change.getCharStart() <= intersect.getCharStart() && change.getCharEnd() >= intersect.getCharEnd()) {
					if(change.getChangeType() == Resources.ChangeType.MODIFYDELETE) {
						intersectIter.remove();
					}
					else if(change.getChangeType() == Resources.ChangeType.MODIFYINSERT) {
						shiftOwnershipBelowChange((change.getCharEnd() - intersect.getCharStart()), 
								intersect.getCharStart(), addedChanges);
					}
				}
			}
			if(!addedChanges.isEmpty()) {
				for(Change achange: addedChanges)
					addOwnershipChange(achange, intersectList);
			}
		}
		// Shift everything after a delete
		if(change.getChangeType() == Resources.ChangeType.MODIFYDELETE) {
			shiftOwnershipBelowChange((change.getCharStart() - change.getCharEnd())-1, 
					change.getCharStart(), null);
		}
	}
	
	private void shiftOwnershipBelowChange(int shift, int startingPoint, List<Change> additionalList) {
		for(Map.Entry<String, List<Change>> entry: this.Owners.entrySet()) {
			for(Change change: entry.getValue()) {
				if(change.getCharStart() >= startingPoint) {
					change.setCharStart(change.getCharStart()+shift);
					change.setCharEnd(change.getCharEnd()+shift);
				}
			}
		}
		if(additionalList != null)
			shiftListBelowChange(shift, startingPoint, additionalList);
	}
	
	public Set<WeightedChange> getMethodWeights(Method method )
	{
		Set<WeightedChange> weights = new HashSet<WeightedChange>();
		for (String owner : this.Owners.keySet())
		{
			for (Change change : this.Owners.get(owner))
			{
				float weight = getMethodWeight(owner, method);
				if (weight > 0)
					weights.add(new WeightedChange(change, weight));
			}
		}
		return weights;
	}
	
	private void shiftListBelowChange(int shift, int startingPoint, List<Change> list) {
		for(Change change: list) {
			if(change.getCharStart() >= startingPoint) {
				change.setCharStart(change.getCharStart()+shift);
				change.setCharEnd(change.getCharEnd()+shift);
			}
		}
	}
	
	public float getMethodWeight(String owner, Method method) {
		// Get all ranges of ownership
		List<Change> ranges = this.Owners.get(owner);
		if(ranges == null)
			return -1;
		
		float sum = 0;
		
		for(Change range: ranges) {
			if(intersectionOfCode(range.getCharStart(), range.getCharEnd(), 
					method.getstartChar(), method.getendChar())) {
				// Case 1
				if(range.getCharStart() < method.getstartChar() 
						&& (range.getCharEnd() >= method.getstartChar() && range.getCharEnd() < method.getendChar())) {
					sum += range.getCharEnd() - method.getstartChar();
				}
				// Case 2
				else if((range.getCharStart() >= method.getstartChar() && range.getCharStart() < method.getendChar()) 
						&& range.getCharEnd() > method.getstartChar() && range.getCharEnd() <= method.getendChar()) {
					sum += range.getCharEnd() - range.getCharStart();
				}
				// Case 3
				else if((range.getCharStart() > method.getstartChar() && range.getCharStart() <= method.getendChar())
						&& range.getCharEnd() > method.getendChar()) {
					sum += method.getendChar() - range.getCharStart();
				}
				// Case 4
				else if(range.getCharStart() < method.getstartChar() && range.getCharEnd() > method.getendChar()) {
					sum += method.getendChar() - method.getstartChar();
				}
			}
		}
		
		// Get the percentage
		float weight = (sum / (method.getendChar() - method.getstartChar()));
		
		return weight;
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
