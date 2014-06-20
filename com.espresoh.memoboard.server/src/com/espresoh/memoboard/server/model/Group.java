package com.espresoh.memoboard.server.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Cosmin
 *
 */
@Table(name = "groups")
@Entity
public class Group extends BaseEntity {

	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 1L;

	

	// ====================== 2. Instance Fields =============================

	@Column(nullable=false, unique=true)
	private String name;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "group_users", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users = new HashSet<User>();

	
	// ==================== 4. Constructors ====================

	public Group() 
	{
	}
	

	// ==================== 7. Getters & Setters ====================

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Set<User> getUsers()
	{
		return users;
	}
	
	public void setUsers(Set<User> users)
	{
		this.users = users;
	}
}
