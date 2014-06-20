package com.espresoh.memoboard.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author cghita
 *
 */
@Entity
public class MemoAction extends BaseEntity
{

	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 1L;

	
	// ====================== 2. Instance Fields =============================
	
	public enum MemoActionType
	{
		CONFIRMED,
		DISMISSED;
	}
	
	@Column(nullable=false)
	private MemoActionType type;
	
	@ManyToOne
	@JoinColumn(name = "id_memo", nullable=false)
	private Memo memo;
	
	@ManyToOne
	@JoinColumn(name = "id_user", nullable=false)
	private User user;

	
	// ==================== 7. Getters & Setters ====================

	public MemoActionType getType()
	{
		return type;
	}

	public void setType(MemoActionType type)
	{
		this.type = type;
	}

	public Memo getMemo()
	{
		return memo;
	}

	public void setMemo(Memo memo)
	{
		this.memo = memo;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
	
	
}
