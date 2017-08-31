package com.soch.uam.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SecurityQAEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserNotesEntity;
import com.soch.uam.domain.UserRoleEntity;
import com.soch.uam.dto.RoleDTO;
import com.soch.uam.dto.SecurityQADTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.dto.UserNotesDTO;
import com.soch.uam.request.UserProfileResp;

public class CopyBeanProperties {
	
	public static UserDTO copyUserPerperties(UserEntity userEntity)
	{
		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName(userEntity.getFirstName());
		userDTO.setId(userEntity.getId());
		userDTO.setLastName(userEntity.getLastName());
		userDTO.setEmailId(userEntity.getEmailId());
		if(userEntity.getDateOfBirth() != null)
		userDTO.setDateOfBirth(convertFormat(userEntity.getDateOfBirth()));
		userDTO.setUserId(userEntity.getUserId());
		userDTO.setActiveFlag(userEntity.getActiveFlag());
		//if(userEntity.getSSN() != null)
		//userDTO.setSSN(truncateSSN(userEntity.getSSN()));
		userDTO.setPassword(userEntity.getPassowrd());
		userDTO.setLockFlag(userEntity.getLockFlag());
		if(!userEntity.getSecurityQA().isEmpty())
			userDTO.setSecurityQA(copySecurityQAProperties(userEntity.getSecurityQA()));
		return userDTO; 
	}
	
	private static Set<SecurityQADTO>  copySecurityQAProperties(Set<SecurityQAEntity> securityQAEntities)
	{
		Set<SecurityQADTO> securityQADTOs = new HashSet<SecurityQADTO>();
		
		SecurityQADTO securityQADTO = null;
		
		for(SecurityQAEntity securityQAEntity : securityQAEntities)
		{
			securityQADTO = new SecurityQADTO();
			securityQADTO.setAnswer(securityQAEntity.getAnswer());
			
			securityQADTO.setQuestion(securityQAEntity.getQuestion());
			securityQADTOs.add(securityQADTO);
		}
		
		return securityQADTOs;
	}
	
	private static String convertFormat(Date date)
	{
		SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
		return sf.format(date);
	}
	
	private static String truncateSSN(String SSN)
	{
		System.out.println(SSN.split("-")[2]);
		return SSN.split("-")[2] ;
	}
	
	public static UserProfileResp createUserProfile(UserEntity userEntity)
	{
		UserProfileResp userProfileResp = new UserProfileResp();
		Set<RoleDTO> roleDTOs = new HashSet<RoleDTO>();
		RoleDTO roleDTO = null;
		
		userProfileResp.setId(userEntity.getId());
		userProfileResp.setUserId(userEntity.getUserId());
		userProfileResp.setEmailId(userEntity.getEmailId());
		userProfileResp.setFirstName(userEntity.getFirstName());
		userProfileResp.setLastName(userEntity.getLastName());
		userProfileResp.setMiddleName(userEntity.getMiddleName());
		if(userEntity.getDateOfBirth() != null)
			userProfileResp.setDateOfBirth(convertFormat(userEntity.getDateOfBirth()));
		
		userProfileResp.setActiveFlag(userEntity.getActiveFlag());
		userProfileResp.setLockFlag(userEntity.getLockFlag());
		
		if(userEntity.getUserWorkEntity() != null)
		{
			userProfileResp.setWorkEmailId(userEntity.getUserWorkEntity().getEmailAddress());
			userProfileResp.setWorkPhoneNumber(userEntity.getUserWorkEntity().getPhoneNumber());
			userProfileResp.setStartDate(convertFormat(userEntity.getUserWorkEntity().getStartDate()));
		}
		System.out.println("userEntity.getUserRoleEntities()  "+userEntity.getUserRoleEntities() );
		if(userEntity.getUserRoleEntities() != null && !userEntity.getUserRoleEntities().isEmpty())
		{
			Set<UserRoleEntity> userRoleEntities = userEntity.getUserRoleEntities();
			for(UserRoleEntity userRoleEntity : userRoleEntities)
			{
				if(userRoleEntity.isActiveStatus())
				{
					roleDTO = new RoleDTO();
					roleDTO.setRoleName(userRoleEntity.getRolesEntity().getRoleName());
					roleDTO.setRoleId(userRoleEntity.getUserRoleId());
					if(userRoleEntity.getRolesEntity().getSystemEntity() != null)
					{
						roleDTO.setSystemName(userRoleEntity.getRolesEntity().getSystemEntity().getSystemName());
						roleDTO.setSystemId(userRoleEntity.getRolesEntity().getSystemEntity().getSystemId());
						roleDTO.setDepartmentName(userRoleEntity.getRolesEntity().getSystemEntity().getDeptEntity().getDeptName());
						roleDTO.setDepartmentId(userRoleEntity.getRolesEntity().getSystemEntity().getDeptEntity().getDeptId());
					}
				
					roleDTOs.add(roleDTO);
				}
			}
			userProfileResp.setRoleDTOs(roleDTOs);
			
		}
		
		if(userEntity.getUserNotesEntities() != null && userEntity.getUserNotesEntities().size() > 0)
		{
			UserNotesDTO userNotesDTO = null;
			Set<UserNotesDTO> userNotesDTOs = new HashSet<UserNotesDTO>(0);
			Set<UserNotesEntity> userNotesEntities = userEntity.getUserNotesEntities();
			
			for(UserNotesEntity userNotesEntity : userNotesEntities)
			{
				userNotesDTO = new UserNotesDTO();
				userNotesDTO.setNotes(userNotesEntity.getNotes());
				userNotesDTO.setCreatedTs(userNotesEntity.getCreatedTs());
				userNotesDTO.setApprovedBy(userNotesEntity.getCreatedBy());
				userNotesDTOs.add(userNotesDTO);
			}
			
			userProfileResp.setUserNotesDTO(userNotesDTOs);
		}
		return userProfileResp;
	}
	
}
