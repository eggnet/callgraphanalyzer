package differ;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import differ.diff_match_patch;
import differ.diff_match_patch.Diff;
import differ.diff_match_patch.LinesToCharsResult;

////////////////////////////////////////////////////////////////
// Compare two files to get all the changes out (deleted, added, modified lines)
public class filediffer {
	public class diffResult
	{
		public diffResult(){};
		public List<String> changedMethods = new ArrayList<String>();
		public List<String> changedClasses = new ArrayList<String>();
	};
	
	private boolean isModified = false;
	private boolean hasMethodChanged = false;
	private String oldFileContent;
	private String newFileContent;
	private String diffContent;
	private diff_match_patch myDiffer = new diff_match_patch();
	private diffResult result = new diffResult();
	private List<Diff> diffObjects = new LinkedList<Diff>();
	
	/**
	 * fileDiffer constructor
	 * @param filecontent1: raw file from old commit
	 * @param filecontent2: raw file from new commit
	 */
	public filediffer(String oldFileContent, String newFileContent) {
		this.oldFileContent = oldFileContent;
		this.newFileContent = newFileContent;
	}

	/**
	 * diff two file contents and produce diffContent
	 * + Line added
	 * - Line deleted
	 * = Line modified
	 */
	public void diffFilesCharMode(){
		this.diffObjects.clear();
		
		// Look at the content character by character
		this.diffObjects = myDiffer.diff_main(oldFileContent, newFileContent);
		myDiffer.diff_cleanupSemantic((LinkedList)this.diffObjects);
		myDiffer.diff_cleanupMerge	 ((LinkedList)this.diffObjects);
		
		objectToContent();
		getChanges();
	}

	/**
	 * Diff the two files by line number
	 * Compare line by line
	 */
	public void diffFilesLineMode()
	{
		this.diffObjects.clear();
		
		// convert diff object to set of lines
		LinesToCharsResult result = myDiffer.diff_linesToChars(oldFileContent, newFileContent);
		this.diffObjects = myDiffer.diff_main(result.chars1, result.chars2, false);
		myDiffer.diff_charsToLines((LinkedList)diffObjects, result.lineArray);
		
		// merge and cleanup junk changes
		myDiffer.diff_cleanupSemantic((LinkedList)this.diffObjects);
		myDiffer.diff_cleanupMerge	 ((LinkedList)this.diffObjects);
		
		objectToContent();
		getChanges();
		getDiffLocation();
	}
	
	/**
	 * Convert diffObject to diffContent
	 */
	private void objectToContent()
	{
		this.diffContent = "";
		
		for(Diff mydiff : this.diffObjects)
		{
			if(mydiff.operation != diff_match_patch.Operation.EQUAL)
			{
				diffContent += mydiff.text;
				isModified = true;
			}
		}
	}
	/**
	 * @return the functions, class and other changes
	 */
	public void getChanges()
	{
		result.changedClasses.clear();
		result.changedMethods.clear();
		
		getChangedClasses();
		getChangedMethods();
	}
	
	public void print()
	{
		// Print diff objects
		for(Diff mydiff : this.diffObjects)
		{
			if(mydiff.operation != diff_match_patch.Operation.EQUAL)
				System.out.println(mydiff.toString());
		}
	}
	
	/**
	 * Parse DiffObjects to methods and class list
	 */
	public void getChangedClasses()
	{
		if (diffContent.isEmpty())
			return;
		
		// get all method names
		String regex = "public[\\s]+class[\\s]+([\\w]+)[\\s]+[\\w]+";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(diffContent);
		
		boolean found = false;
		while (matcher.find())
		{
			System.out.println("Class: " +
					matcher.group() + " from "+
					matcher.start() + " to " +
					matcher.end());
			
			result.changedClasses.add(matcher.group(1));
			found = true;
		}
		if(!found)
			System.out.println("No match found");		
	}
	
	/**
	 * Parse DiffObjects to methods and class list
	 */
	public void getChangedMethods()
	{
		if (diffContent.isEmpty())
			return;
		
		// get all method names
		// Need to use non-greedy match 
		// Todo: account for public, private, protected, static function etc
		String regex = "public[\\s]+[\\w<>]+?[\\s]+([\\w]+)[(]";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(diffContent);
		boolean found = false;

		while (matcher.find())
		{
			System.out.println("Function: " +
					matcher.group() + " from "+
					matcher.start() + " to " +
					matcher.end());
			
			result.changedMethods.add(matcher.group(1));
			found = true;
			
			hasMethodChanged = true;
		}
		if(!found)
			System.out.println("No match found");	
	}
	
	/**
	 * 	// Get all methods appeared in the file
	 * public int getId() {
	 * private int getId( int a, hash<string> map)
	 * protected
	 * public Collection<User> getUsers() {
	 */
	public void getDiffLocation()
	{
		if (diffObjects.isEmpty())
			return;
		
		// each diff object, find the function that the diff belongs to
		for(Diff mydiff : this.diffObjects)
		{
			// Search OldFileContent for DELETE
			// Old TEXT is EQUAL + DELETE
			if(mydiff.operation == diff_match_patch.Operation.DELETE)
			{
				System.out.println("OldTextLength: " + this.oldFileContent.length());
				System.out.println("NewTextLength: " + this.newFileContent.length());
				
				// Each diff, match old txt to find the location
				//int location = myDiffer.match_main(this.oldFileContent,mydiff.text, 0);
				int location = this.oldFileContent.indexOf(mydiff.text, 0);
				if(location == -1)
				{
					// none found
					System.out.println("No location found for this delete");
				}
				else
				{
					// calculate the range of this diff
					int stoplocation = location + mydiff.text.length();
					System.out.println("Diff start:" + location + " Stop:" + stoplocation);
				}
			}
			else
			// Search NewFileContent for INSERT
			// New TEXT is EQUAL + INSERT	
			if(mydiff.operation == diff_match_patch.Operation.INSERT)
			{
				System.out.println("OldTextLength: " + this.oldFileContent.length());
				System.out.println("NewTextLength: " + this.newFileContent.length());
				
				// Each Insert, match new txt to find the location
				//int location = myDiffer.match_main(this.newFileContent,mydiff.text, 0);
				int location = this.newFileContent.indexOf(mydiff.text, 0);
				if(location == -1)
				{
					// none found
					System.out.println("No location found for this Insert");
				}
				else
				{
					// calculate the range of this diff
					System.out.println("Diff start:" + location + " Stop:" + location + mydiff.text.length());
				}
			}
		}
		
	
	}
			
	public String getOldFileContent() {
		return oldFileContent;
	}

	public void setOldFileContent(String filecontent1) {
		this.oldFileContent = filecontent1;
	}

	public String getNewFileContent() {
		return newFileContent;
	}

	public void setNewFileContent(String filecontent2) {
		this.newFileContent = filecontent2;
	}

	public String getDiffcontent() {
		return diffContent;
	}

	public void setDiffcontent(String diffcontent) {
		this.diffContent = diffcontent;
	}
	
	public List<Diff> getDiffObjects()
	{
		return this.diffObjects;
	}
	
	public diffResult getResult()
	{
		return this.result;
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public boolean isHasMethodChanged() {
		return hasMethodChanged;
	}

	public void setHasMethodChanged(boolean hasMethodChanged) {
		this.hasMethodChanged = hasMethodChanged;
	}
	
}
