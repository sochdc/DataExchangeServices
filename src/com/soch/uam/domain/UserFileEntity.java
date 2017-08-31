package com.soch.uam.domain;
// default package
// Generated Feb 25, 2017 11:33:37 PM by Hibernate Tools 4.3.5.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * UserFileTb generated by hbm2java
 */
@Entity
@Table(name = "user_file_tb")
public class UserFileEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6814594955736291994L;
	private int fileId;
	private TempUserEntity tempUserEntity;
	private String fileName;
	private byte[] content;

	public UserFileEntity() {
	}

	public UserFileEntity(int fileId) {
		this.fileId = fileId;
	}

	@Id
	@Column(name = "file_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator="fileSeqGen")
	 @TableGenerator(
		        name="fileSeqGen", 
		        table="SEQ_TB", 
		        pkColumnName="seq_name", 
		        valueColumnName="seq_val", 
		        pkColumnValue="user_file_tb.file_id", 
		        allocationSize=1)
	public int getFileId() {
		return this.fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	public TempUserEntity getTempUserEntity() {
		return tempUserEntity;
	}

	public void setTempUserEntity(TempUserEntity tempUserEntity) {
		this.tempUserEntity = tempUserEntity;
	}
	
	@Column(name = "file_name", length = 100)
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "content")
	public byte[] getContent() {
		return this.content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
