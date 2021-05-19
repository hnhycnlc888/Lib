package yzkf.model;

import java.io.Serializable;

public class DiskInfo implements Serializable {
	private static final long serialVersionUID = 7622785923370266722L;
	
	public boolean isMcloud;
	public boolean isShareSiChuan;
	public int totalSize;
	public int useSize;
	public int freeSize;
	public int fileNum;
	public int fileMaxSize;
	public int shareNum;
	public String rootId;
	public String mailId;
	public int rootDirType;
	public boolean isMcloud() {
		return isMcloud;
	}
	public void setMcloud(boolean isMcloud) {
		this.isMcloud = isMcloud;
	}
	public boolean isShareSiChuan() {
		return isShareSiChuan;
	}
	public void setShareSiChuan(boolean isShareSiChuan) {
		this.isShareSiChuan = isShareSiChuan;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getUseSize() {
		return useSize;
	}
	public void setUseSize(int useSize) {
		this.useSize = useSize;
	}
	public int getFreeSize() {
		return freeSize;
	}
	public void setFreeSize(int freeSize) {
		this.freeSize = freeSize;
	}
	public int getFileNum() {
		return fileNum;
	}
	public void setFileNum(int fileNum) {
		this.fileNum = fileNum;
	}
	public int getFileMaxSize() {
		return fileMaxSize;
	}
	public void setFileMaxSize(int fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}
	public int getShareNum() {
		return shareNum;
	}
	public void setShareNum(int shareNum) {
		this.shareNum = shareNum;
	}
	public String getRootId() {
		return rootId;
	}
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	public String getMailId() {
		return mailId;
	}
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
	public int getRootDirType() {
		return rootDirType;
	}
	public void setRootDirType(int rootDirType) {
		this.rootDirType = rootDirType;
	}
	
	
}
