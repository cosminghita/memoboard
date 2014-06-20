package com.espresoh.memoboard.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.espresoh.memoboard.server.po.MemoPO;

/**
 * 
 * @author cghita
 *
 */
@Entity
public class Memo extends BaseEntity
{

	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 1L;

	
	// ====================== 2. Instance Fields =============================
	
	private String title;
	private String content;
	
	private boolean requiresConfirmation = false;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;

	@OneToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "memo_target_users", joinColumns = @JoinColumn(name = "memo_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> targetUsers = new HashSet<>();
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User owner;

	@Transient
	private List<User> confirmedUsers = new ArrayList<User>();

	@Transient
	private List<User> unconfirmedUsers = new ArrayList<User>();

	
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

	public Set<User> getTargetUsers()
	{
		return targetUsers;
	}

	public void setTargetUsers(Set<User> targetUsers)
	{
		this.targetUsers = targetUsers;
	}

	public User getOwner()
	{
		return owner;
	}

	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	
	public int getProgress()
	{
		return confirmedUsers.size();
	}
	
	public List<User> getConfirmedUsers()
	{
		return confirmedUsers;
	}
	
	public Memo setConfirmedUsers(List<User> confirmedUsers)
	{
		this.confirmedUsers = confirmedUsers;
		this.unconfirmedUsers = new ArrayList<User>(targetUsers);
		this.unconfirmedUsers.removeAll(confirmedUsers);
		
		return this;
	}
	
	public List<User> getUnconfirmedUsers()
	{
		return unconfirmedUsers;
	}
	
	// ==================== 13. Utility Methods ====================

	public MemoPO toPO()
	{
		MemoPO memoPO = new MemoPO();
		
		return memoPO;
	}
	
}
