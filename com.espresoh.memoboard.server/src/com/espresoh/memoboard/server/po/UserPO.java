package com.espresoh.memoboard.server.po;

import java.io.Serializable;

public class UserPO implements Serializable
{
	
	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 1L;

	
	// ====================== 2. Instance Fields =============================
	
	private String email;

	private String name;

	private String avatar;

	
	// ==================== 7. Getters & Setters ====================

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAvatar()
	{
		return avatar;
	}

	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}

	
	// ==================== 12. Presentation ====================

	@Override
	public String toString()
	{
		return "UserPO [email=" + email + ", name=" + name + ", avatar=" + avatar + "]";
	}

	
	
}
