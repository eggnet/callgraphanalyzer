package models;

import db.Resources;

public class OwnerChange {
	private String CommitId;
	private String OwnerId;
	private String FileId;
	private int CharStart;
	private int CharEnd;
	private Resources.ChangeType ChangeType;
	
	public OwnerChange() { }
	
	public String getCommitId() {
		return CommitId;
	}
	public void setCommitId(String commitId) {
		CommitId = commitId;
	}
	public String getOwnerId() {
		return OwnerId;
	}
	public void setOwnerId(String ownerId) {
		OwnerId = ownerId;
	}
	public String getFileId() {
		return FileId;
	}
	public void setFileId(String fileId) {
		FileId = fileId;
	}
	public int getCharStart() {
		return CharStart;
	}
	public void setCharStart(int charStart) {
		CharStart = charStart;
	}
	public int getCharEnd() {
		return CharEnd;
	}
	public void setCharEnd(int charEnd) {
		CharEnd = charEnd;
	}
	public Resources.ChangeType getChangeType() {
		return ChangeType;
	}
	public void setChangeType(Resources.ChangeType changeType) {
		ChangeType = changeType;
	}
}
