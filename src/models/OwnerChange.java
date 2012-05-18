package models;

import db.Resources;
import db.Resources.ChangeType;

public class OwnerChange
{
	private String					CommitId;
	private String					OwnerId;
	private String					FileId;
	private int						CharStart;
	private int						CharEnd;
	private Resources.ChangeType	ChangeType;

	public OwnerChange()
	{
	}

	public OwnerChange(String ownerId, String commitId, ChangeType changeType, String fileId, int charStart, int charEnd)
	{
		super();
		CommitId = commitId;
		OwnerId = ownerId;
		FileId = fileId;
		CharStart = charStart;
		CharEnd = charEnd;
		ChangeType = changeType;
	}

	public String getCommitId()
	{
		return CommitId;
	}

	public OwnerChange setCommitId(String commitId)
	{
		CommitId = commitId;
		return this;
	}

	public String getOwnerId()
	{
		return OwnerId;
	}

	public OwnerChange setOwnerId(String ownerId)
	{
		OwnerId = ownerId;
		return this;
	}

	public String getFileId()
	{
		return FileId;
	}

	public OwnerChange setFileId(String fileId)
	{
		FileId = fileId;
		return this;
	}

	public int getCharStart()
	{
		return CharStart;
	}

	public OwnerChange setCharStart(int charStart)
	{
		CharStart = charStart;
		return this;
	}

	public int getCharEnd()
	{
		return CharEnd;
	}

	public OwnerChange setCharEnd(int charEnd)
	{
		CharEnd = charEnd;
		return this;
	}

	public Resources.ChangeType getChangeType()
	{
		return ChangeType;
	}

	public OwnerChange setChangeType(Resources.ChangeType changeType)
	{
		ChangeType = changeType;
		return this;
	}
}
