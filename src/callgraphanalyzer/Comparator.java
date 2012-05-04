package callgraphanalyzer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.CommitsTO;
import db.DbConnection;
import differ.filediffer;
public class Comparator {
	private DbConnection db;
	private filediffer differ;
	private CallGraphAnalyzer cga;
	public Map<String, String> FileMap;
	public Map<String, String> CommitOneFileTree;
	public Map<String, String> CommitTwoFileTree;
	public String CurrentBranch;
	public String CurrentBranchID;
	
	/**
	 * Constructs a new Comparator class.  This class connects the FileDiffer {@link #differ} and the CallGraphAnalyzer {@link #cga}
	 * @param branchName Name of the branch
	 * @param db Name of the Database.
	 * @param CommitIDOne SHA-1 Hash of the commit in question.
	 * @param CommitIDTwo SHA-1 Hash of the second commit.
	 */
	public Comparator(String branchName, DbConnection db, String CommitIDOne, String CommitIDTwo, CallGraphAnalyzer cga) {
		this.db = db;
		this.CommitOneFileTree = this.getFilesTreeForCommit(CommitIDOne);
		this.CommitTwoFileTree = this.getFilesTreeForCommit(CommitIDTwo);
		this.cga = cga;
	}

	public boolean CompareCommits()
	{
		// TODO @braden
		return true;
	}
	
	public boolean getChangedFilesForCommit(String commitID)
	{
		FileMap = db.getCommitChangedFiles(commitID);
		return true;
	}
	
	/**
	 * Recursively get the files for a commit, going down from the given commit.
	 * adding them to @see {@link #CommitOneFileTree}
	 * @param commitID
	 * @return true when successful
	 */
	public Map<String, String> getFilesTreeForCommit(String commitID)
	{
		Map<String, String> CommitFileTree = new HashMap<String, String>();
		List<CommitsTO> commitsBefore = db.getCommitsBefore(commitID);
		Set<String> requiredFiles = commitsBefore.get(0).getFile_structure();	// First commit;
		while(true) {
			if (db.isPaging) commitsBefore = db.getNextCommitsPage();
			else commitsBefore = db.getCommitsBefore(commitID);
			for (CommitsTO commit : commitsBefore)
			{
				Iterator<String> i = commit.getChanged_files().iterator();
				String currentChangedFile;
				while (i.hasNext())
				{
					currentChangedFile = i.next();
					System.out.println(currentChangedFile);
					if (requiredFiles.contains(currentChangedFile) &&
							!CommitFileTree.containsKey(currentChangedFile))
					{
						CommitFileTree.put(currentChangedFile, commit.getCommit_id());
					}
				}
			}
			if (!db.isPaging) break;
		}
		return CommitFileTree;
	}
	
}
