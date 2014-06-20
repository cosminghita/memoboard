package com.espresoh.memoboard.server.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gncsoft.util.DateManipulate;

@MappedSuperclass
public abstract class BaseEntity  implements Serializable{

	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 7219641651938961932L;

	// ====================== 2. Instance Fields =============================

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate = new Date(Calendar.getInstance().getTimeInMillis());

	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	// ==================== 7. Getters & Setters ====================

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}


	public Date getStartDate() {
		return startDate;
	}

	public Calendar geEndCal() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getEndDate());
		return cal;
	}

	public String getStartDateText() {
		if(startDate == null)
			return "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String text = df.format(startDate);
		return text;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getEndDateText() {
		if(startDate == null)
			return "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String text = df.format(endDate);
		return text;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public boolean isActiv() 
	{
		return isActiv(Calendar.getInstance().getTime());
	}

	public boolean isActiv(final Date activeDate) 
	{
		boolean activ = activeDate.compareTo(DateManipulate.resetToStart(startDate)) >= 0;
		
		if(endDate != null)
			activ = activ && activeDate.compareTo(DateManipulate.resetToEnd(endDate)) <= 0;
		
		return activ;
	}


	// ==================== 8. Business Methods ====================



	// ==================== 10. Validation ====================

	/**
	 * poate fi suprascrisa.
	 */
	public boolean isValid() {
		return true;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		BaseEntity other = (BaseEntity) obj;
		if (id != other.id)
			return false;

		return true;
	}
	public boolean isSavable() {
		return true;
	}

	// ==================== 13. Utility Methods ====================

	public <T> List<T> getClientList(final Collection<T> collection) {
		return new ArrayList<T>(collection);
	}

	public boolean isNewBean() {
		return getId() == 0;
	}
}
