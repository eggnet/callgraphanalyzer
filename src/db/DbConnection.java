package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import callgraphanalyzer.Resources;

public class DbConnection {

	public static Connection conn = null;
	public static DbConnection ref = null;
	
	private DbConnection() 
	{
		try 
		{
			Class.forName("org.postgresql.Driver").newInstance();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static DbConnection getInstance()
	{
		if (ref == null)
			return (ref = new DbConnection());
		else
			return ref;
	}
	
	/**
	 * Executes a string of SQL on the current database
	 * NOTE: this assumes your sql is valid.
	 * @param sql
	 * @return true if successful
	 */
	public boolean exec(String sql)
	{
		try {
			PreparedStatement s = conn.prepareStatement(sql);
			s.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean execPrepared(String sql, String[] params)
	{
		try {
			PreparedStatement s = conn.prepareStatement(sql);
			for (int i = 1;i <= params.length;i++)
			{
				s.setString(i, params[i-1]);
			}
			s.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Executes the given query with escaped values in String[] params in place of
	 * ? characters in sql.
	 * @param sql ex. "SELECT * FROM something where my_column=?"
	 * @param params ex. {"braden's work"}
	 * @return Query ResultSet on success, null otherwise
	 */
	public ResultSet execPreparedQuery(String sql, String[] params)
	{
		try {
			PreparedStatement s = conn.prepareStatement(sql);
			for (int i = 1;i <= params.length;i++)
			{
				s.setString(i, params[i-1]);
			}
			return s.executeQuery();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Connects to the given database.  
	 * @param dbName
	 * @return true if successful
	 */
	public boolean connect(String dbName)
	{
		try {
			conn = DriverManager.getConnection(Resources.dbUrl + dbName.toLowerCase(), Resources.dbUser, Resources.dbPassword);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Close all connection
	 */
	public boolean close()
	{
		try {
			conn.close();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns a HashMap<(filePath), (fileContents)>
	 * @param commitID
	 * @return
	 */
	public Map<String, String> getCommitFiles(String commitID)
	{
		Map<String, String> files = new HashMap<String, String>();
		try {
			String sql = "SELECT file_id, raw_file FROM files where commit_id=?;";
			String[] params = {commitID};
			ResultSet rs = execPreparedQuery(sql, params);
			while(rs.next())
			{
				files.put(rs.getString("file_id"), rs.getString("raw_file"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return files;
	}
	
}
