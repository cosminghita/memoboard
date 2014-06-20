package com.espresoh.memoboard.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Cosmin
 * 
 */
@Table(name = "users")
@Entity
public class User extends BaseEntity
{

	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 1L;

	
	// ====================== 2. Instance Fields =============================

	@Column(nullable=false, unique=true)
	private String email;

	@Column(nullable=false)
	private String name;

	private String avatar;

	// ==================== 4. Constructors ====================

	public User()
	{
	}


	// ==================== 7. Getters & Setters ====================

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getAvatar()
	{
		return avatar;
	}
	
	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}

	public String toText()
	{
		return name + " - " + email;
	}
	
	

}
