package com.espresoh.memoboard.server.po;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class MemoPO implements Serializable
{
	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 1L;

	
	// ====================== 2. Instance Fields =============================
	
	private String title;
	private String content;
	
	private boolean requiresConfirmation;
	
	private Date dueDate;

	private Set<Integer> targetUsers;
	
	private Integer ownerId;


	// ==================== 7. Getters & Setters ====================

	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getContent()
	{
		return content;
	}


	public void setContent(String content)
	{
		this.content = content;
	}


	public boolean isRequiresConfirmation()
	{
		return requiresConfirmation;
	}


	public void setRequiresConfirmation(boolean requiresConfirmation)
	{
		this.requiresConfirmation = requiresConfirmation;
	}


	public Date getDueDate()
	{
		return dueDate;
	}


	public void setDueDate(Date dueDate)
	{
		this.dueDate = dueDate;
	}


	public Set<Integer> getTargetUsers()
	{
		return targetUsers;
	}


	public void setTargetUsers(Set<Integer> targetUsers)
	{
		this.targetUsers = targetUsers;
	}


	public Integer getOwnerId()
	{
		return ownerId;
	}


	public void setOwnerId(Integer ownerId)
	{
		this.ownerId = ownerId;
	}


	// ==================== 13. Utility Methods ====================

}
