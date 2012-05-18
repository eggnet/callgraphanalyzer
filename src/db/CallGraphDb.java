package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.OwnerChange;

public class CallGraphDb extends DbConnection
{
	public CallGraphDb()
	{
		super();
	}

	public OwnerChange getLatestOwnerChange(String fileId, int start, int end)
	{
		try
		{
//			String sql = "SELECT commit_id, file_id, owner_id, char_start, char_end, change_type FROM owners natural join commits where file_id=? AND "
//					+ "((char_start<=? and char_end>=?) OR "
//					+ "(char_start<=? and char_end>=?) OR "
//					+ "(char_start>? and char_end<?) OR"
//					+ "(char_end='-1' and change_type='ADD')) order by commit_date desc limit 1";
			String sql = "SELECT commit_id, file_id, owner_id, char_start, char_end, change_type FROM owners natural join commits where file_id=? AND "
					+ "(branch_id=? OR branch_id is NULL) order by commit_date desc";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, fileId);
//			stmt.setInt(2, start);
//			stmt.setInt(3, start);
//			stmt.setInt(4, end);
//			stmt.setInt(5, end);
//			stmt.setInt(6, start);
//			stmt.setInt(7, end);
			stmt.setString(2, branchID);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next())
				return null;
			return new OwnerChange(rs.getString("owner_id"), rs.getString("commit_id"), Resources.ChangeType.valueOf(rs.getString("change_type")), rs.getString("file_id"), rs.getInt("char_start"), 
						rs.getInt("char_end"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}