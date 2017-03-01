package com.soch.uam.domain;
// default package
// Generated Feb 14, 2017 3:10:35 PM by Hibernate Tools 4.3.5.Final


import java.util.Date;

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
 * OnboardingUserNotes generated by hbm2java
 */
@Entity
@Table(name="onboarding_user_notes_tb"
    ,catalog="sochdb"
)
public class OnboardingUserNotesEntity  implements java.io.Serializable {
     /**
	 * 
	 */
	private static final long serialVersionUID = -5905821175038057540L;
	
	private Integer onboardingUserNotesId;
	private TempUserEntity tempUserEntity;
    private String notes;
    private Date createdTs;
    private String createdBy;
    

    @Id

	@Column(name = "onboarding_user_notes_id", unique = true, nullable = false, length = 45)
    @GeneratedValue(strategy = GenerationType.TABLE, generator="seqGen")
	 @TableGenerator(
		        name="seqGen", 
		        table="SEQ_TB", 
		        pkColumnName="seq_name", 
		        valueColumnName="seq_val", 
		        pkColumnValue="onboarding_user_notes_id", 
		        allocationSize=1)
	public Integer getOnboardingUserNotesId() {
		return this.onboardingUserNotesId;
	}
    
    public void setOnboardingUserNotesId(Integer onboardingUserNotesId) {
		this.onboardingUserNotesId = onboardingUserNotesId;
	}
    
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="temp_user_tb_employee_id")
    public TempUserEntity getTempUserEntity() {
        return this.tempUserEntity;
    }
    
    public void setTempUserEntity(TempUserEntity tempUserEntity) {
        this.tempUserEntity = tempUserEntity;
    }

    @Column(name="notes", length=500)
    public String getNotes() {
        return this.notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }


    @Column(name="created_ts", length=45)
    public Date getCreatedTs() {
        return this.createdTs;
    }
    
    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }


    @Column(name="created_by", length=45)
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


}


