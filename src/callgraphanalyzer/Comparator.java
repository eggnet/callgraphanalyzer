package callgraphanalyzer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.CommitsTO;
import db.DbConnection;
public class Comparator {
	private DbConnection db;
	public Map<String, String> FileMap;
	public Map<String, String> CommitOneFileTree;
	public Map<String, String> CommitTwoFileTree;
	public String CurrentBranch;
	public String CurrentBranchID;
	
	public Comparator(String branchName, DbConnection db, String CommitIDOne, String CommitIDTwo) {
		this.db = db;
		this.CommitOneFileTree = new HashMap<String, String>();
		this.CommitTwoFileTree = new HashMap<String, String>();
		this.getFilesTreeForCommit(CommitIDOne);
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
	public boolean getFilesTreeForCommit(String commitID)
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
		return true;
	}
	
}
