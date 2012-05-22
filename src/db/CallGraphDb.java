package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import models.Change;


public class CallGraphDb extends DbConnection
{
	public CallGraphDb()
	{
		super();
	}
	
	/**
	 * Gets all of the change objects associated including and before a given commit.
	 * @param CommitId
	 * @return
	 */
	public List<Change> getAllOwnerChangesBefore(String CommitId)
	{
		try 
		{
			LinkedList<Change> changes = new LinkedList<Change>();
			String sql = "SELECT commit_id, file_id, owner_id, char_start, char_end, change_type FROM owners natural join commits where commit_date <= (select commit_date from commits where commit_id=?)" +
					"and (branch_id is NULL OR branch_id=?) order by commit_date desc;"; 
			String[] parms = {CommitId, branchID};
			ResultSet rs = execPreparedQuery(sql, parms);
			while(rs.next())
			{
				changes.addFirst(new Change(rs.getString("owner_id"), rs.getString("commit_id"), Resources.ChangeType.valueOf(rs.getString("change_type")), rs.getString("file_id"), rs.getInt("char_start"), 
						rs.getInt("char_end")));
			}
			return changes;
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
}