package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import models.Change;
import models.Commit;
import models.CommitFamily;
import models.User;


public class CallGraphDb extends DbConnection
{
	public CallGraphDb()
	{
		super();
	}
	
	public Map<String, List<Change>> getAllFileOwnerChangesBefore(String FileId, String CommitId)
	{
		try 
		{
			String sql = "SELECT commit_id, file_id, owner_id, char_start, char_end, change_type FROM owners natural join commits where commit_date <= (select commit_date from commits where commit_id=?)" +
					"and (branch_id is NULL OR branch_id=?) and file_id=? order by commit_id;"; 
			String[] parms = {CommitId, branchID, FileId};
			ResultSet rs = execPreparedQuery(sql, parms);
			
			// Create a map for <Commit_id, List<Change>>
			Map<String, List<Change>> commitMap = new HashMap<String, List<Change>>();
			LinkedList<Change> currentChanges = new LinkedList<Change>();
			String currentCommit = "";
			
			while(rs.next())
			{
				String commitId = rs.getString("commit_id");
				if(currentCommit.isEmpty())
				{
					// first commit
					currentCommit = rs.getString("commit_id");
					currentChanges.add(new Change(rs.getString("owner_id"),
												  rs.getString("commit_id"), 
												  Resources.ChangeType.valueOf(rs.getString("change_type")),
												  rs.getString("file_id"),
												  rs.getInt("char_start"),
												  rs.getInt("char_end")));
					
				}
				else 
				{
					if(commitId.equals(currentCommit))
					{
						// same commit, push into current map
						currentChanges.add(new Change(rs.getString("owner_id"),
													  rs.getString("commit_id"), 
													  Resources.ChangeType.valueOf(rs.getString("change_type")),
													  rs.getString("file_id"),
													  rs.getInt("char_start"),
													  rs.getInt("char_end")));
					}
					else
					{
						// add new File map
						commitMap.put(currentCommit, currentChanges);
						currentCommit = commitId;
						currentChanges = new LinkedList<Change>();
						currentChanges.add(new Change(rs.getString("owner_id"),
													  rs.getString("commit_id"), 
													  Resources.ChangeType.valueOf(rs.getString("change_type")),
													  rs.getString("file_id"),
													  rs.getInt("char_start"),
													  rs.getInt("char_end")));
					}
				}
			}
			
			//Add last commit
			if(!currentCommit.isEmpty())
				commitMap.put(currentCommit, currentChanges);
			
			return commitMap;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get onwer breakdown for a file for a specific commit. If the current commit does not have the file, look for its parent commit until find one.
	 * @param FileId
	 * @param CommitId
	 * @return
	 */
	public List<Change> getAllOwnersForFileAtCommit(String FileId, String CommitId, List<CommitFamily> commitPath)
	{
		// get all the onwer entries for this file since this commit
		Map<String, List<Change>> commitMap = getAllFileOwnerChangesBefore(FileId, CommitId);
		
		// traverse comithPath if grab the first found as it has the latest owner break down.
		for(CommitFamily cf: commitPath)
		{
			String commitId = cf.getChildId();
			if(commitMap.containsKey(commitId))
				return commitMap.get(commitId);
		}

		return null;
	}
	
	public List<Commit> getCommitChildren(String CommitID) {
		try 
		{
			LinkedList<Commit> commits = new LinkedList<Commit>();
			String sql = "SELECT commit_id, author, author_email, comments, commit_date, branch_id FROM commit_family " +
					"JOIN Commits ON (commit_family.child = Commits.commit_id) where parent=?" +
					"and (branch_id is NULL OR branch_id=?) order by commit_date, commit_id;"; 
			String[] parms = {CommitID, branchID};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
			{
				commits.add(new Commit(rs.getString(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getTimestamp(5), rs.getString(6)));
			}
			return commits;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Commit> getCommitParents(String CommitID) {
		try 
		{
			LinkedList<Commit> commits = new LinkedList<Commit>();
			String sql = "SELECT commit_id, author, author_email, comments, commit_date, branch_id FROM commit_family " +
					"JOIN Commits ON (commit_family.parent = Commits.commit_id) where child=?" +
					"and (branch_id is NULL OR branch_id=?) order by commit_date, commit_id;"; 
			String[] parms = {CommitID, branchID};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
			{
				commits.add(new Commit(rs.getString(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getTimestamp(5), rs.getString(6)));
			}
			return commits;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getFilesChanged(String oldCommit, String newCommit) {
		try 
		{
			LinkedList<String> files = new LinkedList<String>();
			String sql = "SELECT file_id FROM file_diffs " +
					"WHERE old_commit_id=? AND new_commit_id=?"; 
			String[] parms = {oldCommit, newCommit};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
			{
				if(!files.contains(rs.getString("file_id")))
					files.add(rs.getString("file_id"));
			}
			return files;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getFilesAdded(String commitID) {
		try 
		{
			LinkedList<String> files = new LinkedList<String>();
			String sql = "SELECT file_id, diff_type FROM file_diffs " +
					"WHERE new_commit_id=?"; 
			String[] parms = {commitID};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
			{
				if(rs.getString("diff_type").equals("DIFF_ADD") && !files.contains(rs.getString("file_id")))
					files.add(rs.getString("file_id"));
			}
			return files;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getFilesDeleted(String commitID) {
		try 
		{
			LinkedList<String> files = new LinkedList<String>();
			String sql = "SELECT file_id, diff_type FROM file_diffs " +
					"WHERE new_commit_id=?"; 
			String[] parms = {commitID};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
			{
				if(rs.getString("diff_type").equals("DIFF_DELETE") && !files.contains(rs.getString("file_id")))
					files.add(rs.getString("file_id"));
			}
			return files;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean parentHasChild(String parent, String child) {
		try 
		{
			String sql = "SELECT parent, child FROM commit_family " +
					"WHERE parent=? AND child=?"; 
			String[] parms = {parent, child};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
				return true;
			
			return false;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Adds a new network record into the networks table for a pair of commits.
	 * @param NewCommitId
	 * @param OldCommitId
	 */
	public int addNetworkRecord(String NewCommitId, String OldCommitId)
	{
		try {
			// delete duplicates
			String sql = "DELETE FROM networks where new_commit_id=? and old_commit_id=?";
			String[] parms = {NewCommitId, OldCommitId};
			execPrepared(sql, parms);
			
			// add new network
			sql = "INSERT INTO networks (new_commit_id, old_commit_id, network_id) VALUES (?, ?, default);";
			execPrepared(sql, parms);
			
			// get the id generated;
			sql = "SELECT network_id from networks where new_commit_id=? and old_commit_id=?;";
			ResultSet rs = execPreparedQuery(sql, parms);
			if (!rs.next())
				return -1;
			else
				return rs.getInt(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Adds a node record into the nodes table for a given User/UserfullName
	 * @param UserId
	 * @param UserFullName
	 */
	public void addNode(String UserId, int NetworkId)
	{
		try {
			if (nodeExists(UserId))
				return;
			String sql = "INSERT INTO nodes (id, label, network_id) VALUES (?, ?, ?);";
			PreparedStatement s = conn.prepareStatement(sql);
			s.setString(1, UserId);
			s.setString(2, UserId);
			s.setInt(3, NetworkId);
			s.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean nodeExists(String UserId)
	{
		try {
			String sql = "SELECT * FROM nodes WHERE id=?;";
			String[] parms = {UserId};
			ResultSet rs = execPreparedQuery(sql, parms);
			return rs.next();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public String getUserFullName(String UserId, int NetworkId)
	{
		try
		{
			String sql = "SELECT author from commits where author_email=?;";
			String[] parms = {UserId};
			ResultSet rs = execPreparedQuery(sql, parms);
			if(!rs.next())
				return null;
			else
				return rs.getString("author");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds an edge record between two users with the weight given.
	 * @param UserId
	 * @param UserSource
	 * @param UserTarget
	 * @param weight
	 */
	public void addEdge(String UserSource, String UserTarget, float weight, boolean isFuzzy, int NetworkId)
	{
		try {
			String sql = "INSERT INTO edges (source, target, weight, is_fuzzy, network_id) VALUES (?, ?, ?, ?, ?);";
			PreparedStatement s = conn.prepareStatement(sql);
			s.setString(1, UserSource);
			s.setString(2, UserTarget);
			s.setFloat(3, weight);
			s.setBoolean(4, isFuzzy);
			s.setInt(5, NetworkId);
			s.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public User getUserFromCommit(String CommitId)
	{
		try {
			User u = new User();
			String sql = "SELECT author, author_email from commits where commit_id = ?";
			String[] parms = {CommitId};
			ResultSet rs = this.execPreparedQuery(sql, parms);
			if (!rs.next())
				return null;
			u.setUserName(rs.getString("author"));
			u.setUserEmail(rs.getString("author_email"));
			return u;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}