package db;

import java.sql.ResultSet;
import java.sql.SQLException;

import db.util.ISetter;
import db.util.ISetter.BooleanSetter;
import db.util.ISetter.FloatSetter;
import db.util.ISetter.IntSetter;
import db.util.ISetter.StringSetter;
import db.util.PreparedStatementExecutionItem;


public class CallGraphDb extends TechnicalDb
{
	public CallGraphDb()
	{
		super();
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
			ISetter[] params = {new StringSetter(1,NewCommitId), new StringSetter(2,OldCommitId)};
			PreparedStatementExecutionItem ei = new PreparedStatementExecutionItem(sql, params);
			addExecutionItem(ei);
			ei.waitUntilExecuted();
			
			// add new network
			sql = "INSERT INTO networks (new_commit_id, old_commit_id, network_id) VALUES (?, ?, default);";
			ei = new PreparedStatementExecutionItem(sql, params);
			addExecutionItem(ei);
			ei.waitUntilExecuted();
			
			// get the id generated;
			sql = "SELECT network_id from networks where new_commit_id=? and old_commit_id=?;";
			ei = new PreparedStatementExecutionItem(sql, params);
			addExecutionItem(ei);
			ei.waitUntilExecuted();
			
			ResultSet rs = ei.getResult();
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
		if (nodeExists(UserId))
			return;
		String sql = "INSERT INTO nodes (id, label, network_id) VALUES (?, ?, ?);";
		ISetter[] params = {new StringSetter(1,UserId), new StringSetter(2,UserId), new IntSetter(3,NetworkId)};
		PreparedStatementExecutionItem ei = new PreparedStatementExecutionItem(sql, params);
		addExecutionItem(ei);
		ei.waitUntilExecuted();
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
	
	/**
	 * Adds an edge record between two users with the weight given.
	 * @param UserId
	 * @param UserSource
	 * @param UserTarget
	 * @param weight
	 */
	public void addEdge(String UserSource, String UserTarget, float weight, boolean isFuzzy, int NetworkId)
	{
		String sql = "INSERT INTO edges (source, target, weight, is_fuzzy, network_id) VALUES (?, ?, ?, ?, ?);";
		ISetter[] params = {
				new StringSetter(1, UserSource), 
				new StringSetter(2, UserTarget), 
				new FloatSetter(3, weight),
				new BooleanSetter(4, isFuzzy),
				new IntSetter(5, NetworkId)};
		PreparedStatementExecutionItem ei = new PreparedStatementExecutionItem(sql, params);
		addExecutionItem(ei);
		ei.waitUntilExecuted();
	}
}