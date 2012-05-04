package differ;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import differ.diff_match_patch;
import differ.diff_match_patch.Diff;

////////////////////////////////////////////////////////////////
// Compare two files to get all the changes out (deleted, added, modified lines)
public class filediffer {
	private String fileContent1;
	private String fileContent2;
	private String diffContent;
	private diff_match_patch myDiffer = new diff_match_patch();
	
	private List<String> changedMethods = new ArrayList<String>();
	private List<String> changedClasses = new ArrayList<String>();
	
	public filediffer(String filecontent1, String filecontent2) {
		this.fileContent1 = filecontent1;
		this.fileContent2 = filecontent2;
	}

	/**
	 * diff two file contents and produce diffContent
	 */
	public void diffFiles(){
		// todo: compare two files line by line to get
		//		+ Line added
		//		- Line deleted
		//		= Line modified
		LinkedList<Diff> diffObjects = myDiffer.diff_main(fileContent1, fileContent2);
		myDiffer.diff_cleanupSemantic(diffObjects);
		myDiffer.diff_cleanupMerge(diffObjects);
		
		// convert diff object to set of lines
		List<String> mylines = new ArrayList<String>();
		myDiffer.diff_charsToLines(diffObjects, mylines);
		// Print diff objects
		for(Diff mydiff : diffObjects)
		{
			System.out.println(mydiff.toString());
		}
		
		// Print lines
		for(String line : mylines)
		{
			System.out.println(line);
		}
		
	}
	
	/**
	 * @return the functions, class and other changes
	 */
	public void getChanges()
	{
		changedClasses.clear();
		changedMethods.clear();
		
		getChangedClasses();
		getChangedMethods();
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// get all public class X appeared in the file
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
			System.out.println("first match: " +
					matcher.group() + " from "+
					matcher.start() + " to " +
					matcher.end());
			
			changedClasses.add(matcher.group(1));
			found = true;
		}
		if(!found)
			System.out.println("No match found");		
	}
	
	////////////////////////////////////////////////////////////////////////
	// Get all methods appeared in the file
	// public int getId() {
	// private int getId( int a, hash<string> map)
	// protected
	// public Collection<User> getUsers() {
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
			
			changedMethods.add(matcher.group(1));
			found = true;
		}
		if(!found)
			System.out.println("No match found");		
	}
	
			
	public String getFilecontent1() {
		return fileContent1;
	}

	public void setFilecontent1(String filecontent1) {
		this.fileContent1 = filecontent1;
	}

	public String getFilecontent2() {
		return fileContent2;
	}

	public void setFilecontent2(String filecontent2) {
		this.fileContent2 = filecontent2;
	}

	public String getDiffcontent() {
		return diffContent;
	}

	public void setDiffcontent(String diffcontent) {
		this.diffContent = diffcontent;
	}
	
}
