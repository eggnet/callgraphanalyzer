package callgraphanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import owners.OwnerManager;

import models.CallGraph;
import models.Method;
import parser.Parser;
import parser.Resolver;
import db.CommitsTO;
import db.DbConnection;
import differ.filediffer;
import differ.filediffer.diffObjectResult;

public class Comparator {
	private DbConnection db;
	private filediffer differ;
	private CallGraphAnalyzer CallGraphAnalyzer;
	private CallGraph newCallGraph;
	private CallGraph oldCallGraph;
	public Map<String, String> FileMap;
	public Map<String, String> newCommitFileTree;
	public Map<String, String> oldCommitFileTree;
	public Map<String, List<Method>> modifiedMethods;
	public CommitsTO newCommit;
	public CommitsTO oldCommit;
	public String CurrentBranch;
	public String CurrentBranchID;
	private OwnerManager OwnerMgr;
	
	/**
	 * Constructs a new Comparator class.  This class connects the FileDiffer {@link #differ} and the CallGraphAnalyzer {@link #cga}
	 * @param db Name of the Database.
	 * @param CommitIDOne SHA-1 Hash of the commit in question.
	 * @param CommitIDTwo SHA-1 Hash of the second commit.
	 */
	public Comparator(DbConnection db, String CommitIDOne, String CommitIDTwo, CallGraphAnalyzer cga) {
		this.db = db;
		
		// Figure out which commit is newer
		CommitsTO first = db.getCommit(CommitIDOne);
		CommitsTO second = db.getCommit(CommitIDTwo);
		if (first.getCommit_date().compareTo(second.getCommit_date()) > 0)
		{
			this.newCommit = first;
			this.oldCommit = second;
			this.newCommitFileTree = this.getFilesTreeForCommit(CommitIDOne);			
			this.oldCommitFileTree = this.getFilesTreeForCommit(CommitIDTwo);			
		}
		else
		{
			this.newCommit = second;
			this.oldCommit = first;
			this.newCommitFileTree = this.getFilesTreeForCommit(CommitIDTwo);			
			this.oldCommitFileTree = this.getFilesTreeForCommit(CommitIDOne);
		}
		
		// check and create our owners.
		this.OwnerMgr = new OwnerManager(db);
		this.OwnerMgr.update(this.newCommit.getCommit_id());
		
		this.CallGraphAnalyzer = cga;
		this.newCallGraph = generateCallGraph(this.newCommitFileTree);
		this.oldCallGraph = generateCallGraph(this.oldCommitFileTree);
	}
	
	/**
	 * Generate Callgraph from a commitFileTree
	 * @param commitFileTree map of file name and path from a commit
	 * @return CallGraph a resolved CallGraph
	 */
	public CallGraph generateCallGraph(Map<String, String> commitFileTree) 
	{
		CallGraph callGraph = new CallGraph();
		Parser parser = new Parser(callGraph);
		
		for (String key : commitFileTree.keySet())
		{
			if (!key.endsWith(".java"))	// Currently don't care about non-java files in our callgraph
				continue;
			parser.parseFileFromString(key, db.getRawFile(key, commitFileTree.get(key)));
		}
		callGraph.print();
		
		System.out.println();
		System.out.println();
		System.out.println("Resolving the fuck out of this CallGraph");
		
		//Resolver resolver = new Resolver(callGraph);
		//resolver.resolveMethods();
		
		//callGraph.print();
		return callGraph;
	}

	public boolean CompareCommits()
	{
		this.modifiedMethods = new HashMap<String, List<Method>>();
		Set<String> binaryFiles = db.getBinaryFiles();
		
		// For every file in the new commit tree
		for (String newKey : newCommitFileTree.keySet())
		{
			// If the file exists in the old commit
			if (oldCommitFileTree.containsKey(newKey))
			{
				// File is still present, might be modified.
				// Check if its a binary file, ignore
				if(binaryFiles.contains(newKey))
				{
					//todo check if this binary file has changed
					if(isBinaryFileChanged(newKey, oldCommit.getCommit_id(), newCommit.getCommit_id()))
					{
						System.out.println("Binary file is modified: " + newKey);
					}
					continue;
				}
				else
				{
					// Non-binary files, use differ to compare them.
					String oldRaw = db.getRawFile(newKey, oldCommitFileTree.get(newKey));
					String newRaw = db.getRawFile(newKey, newCommitFileTree.get(newKey));
					
					differ = new filediffer(oldRaw, newRaw);
					differ.diffFilesLineMode();
					
					// The file was modified (+-) since the old commit.
					if(differ.isModified())
					{
						// return the change sets from the two files
						System.out.println("+-\t" + newKey);
						List<diffObjectResult> deleteObjects = differ.getDeleteObjects();
						List<diffObjectResult> insertObjects = differ.getInsertObjects();
						differ.print();
						
						// figure out which function has changed
						getModifiedMethodsForFile(newKey, deleteObjects, insertObjects);
					}		
				}
			}
			else
			{
				// The file was added (+) since the old commit.
				System.out.println("+\t" + newKey);
			}
		}
		for (String oldKey : oldCommitFileTree.keySet())
		{
			if (!newCommitFileTree.containsKey(oldKey))
			{
				// The file was deleted from the old tree
				System.out.println("-\t" + oldKey);
			}
		}
		
		printModifiedMethods();
		return true;
	}
	
	/**
	 * get all methods in a file that might be changed from new commit to other
	 * @param fileName file path
	 * @param callgraph 
	 * @param diffs list of diff objects
	 * @return
	 */
	public void getModifiedMethodsForFile(String fileName, List<diffObjectResult> deleteDiffs, List<diffObjectResult> insertDiffs)
	{
		List<Method> methods = new ArrayList<Method>();
		
		// methods from old file version
		for(diffObjectResult diff : deleteDiffs)
		{
			List<Method> changedMethod = this.oldCallGraph.getMethodsUsingCharacters(fileName, diff.start, diff.end);
			for(Method m : changedMethod)
				if(!methods.contains(m)) methods.add(m);
		}
		
		// methods from new file version
		for(diffObjectResult diff : insertDiffs)
		{
			List<Method> changedMethod = this.newCallGraph.getMethodsUsingCharacters(fileName, diff.start, diff.end);
			for(Method m : changedMethod)
				if(!methods.contains(m)) methods.add(m);
		}
		
		// Insert to modifiedMethod map
		if(!this.modifiedMethods.containsKey(fileName))
			this.modifiedMethods.put(fileName, methods);
	}
	
	/**
	 * Recursively get the files for a commit, going down from the given commit.
	 * adding them to @see {@link #newCommitFileTree}
	 * @param commitID
	 * @return true when successful
	 */
	public Map<String, String> getFilesTreeForCommit(String commitID)
	{
		Map<String, String> CommitFileTree = new HashMap<String, String>();
		Map<String, Set<String>> prevChanges = db.getCommitsBeforeChanges(commitID);
		Set<String> requiredFiles = db.getFileStructureFromCommit(commitID);	// First commit;
		Iterator<String> i = db.getCommitChangedFiles(commitID).iterator();
		addFilesFromCommit(i, requiredFiles, CommitFileTree, commitID);
		
		for (String commit : prevChanges.keySet())
		{
			Iterator<String> iter = prevChanges.get(commit).iterator();
			addFilesFromCommit(iter, requiredFiles, CommitFileTree, commit); 
		}
		return CommitFileTree;
	}
	
	public void addFilesFromCommit(Iterator<String> i, Set<String> requiredFiles, Map<String, String> CommitFileTree, String commit)
	{
		String currentChangedFile;
		while (i.hasNext())
		{
			currentChangedFile = i.next();
			System.out.println("Commit :" + commit + " changed file " + currentChangedFile);
			if (requiredFiles.contains(currentChangedFile) &&
					!CommitFileTree.containsKey(currentChangedFile))
			{
				CommitFileTree.put(currentChangedFile, commit);
			}
		}
	}
	
	public boolean isBinaryFileChanged(String filePath, String oldCommitID, String newCommitID)
	{
		// Find the latest commit that has this changes
		
		// If the latest commit exists between newCommit and oldCommit, there is a change
		return false;
	}
	
	public void printModifiedMethods()
	{
		for(String file : this.modifiedMethods.keySet())
		{
			List<Method> methods = this.modifiedMethods.get(file);
			System.out.println("File: " + file);
			for(Method m :methods)
			{
				System.out.println("\tModified method: " +m.getName());
			}
			
		}
	}
}
