package callgraphanalyzer;

import java.util.Map;
import db.DbConnection;
public class Comparator {
	private DbConnection db;
	public Map<String, String> FileMap;
	public Comparator() {
		db = DbConnection.getInstance();
		db.connect("agilefant1");
	}

	public boolean getFilesForCommit(String commitID)
	{
		FileMap = db.getCommitFiles(commitID);
		return true;
	}
}
