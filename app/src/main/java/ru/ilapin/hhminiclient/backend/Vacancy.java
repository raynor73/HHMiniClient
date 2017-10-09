package ru.ilapin.hhminiclient.backend;

import java.util.Date;

public class Vacancy {

	private int mId;
	private String mName;
	private int mSalaryFrom;
	private int mSalaryTo;
	private String mCurrency;
	private String mEmployerName;
	private String mAreaName;
	private Date mPublishedAt;
	private String mDescription;

	public int getId() {
		return mId;
	}

	public void setId(final int id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(final String name) {
		mName = name;
	}

	public int getSalaryFrom() {
		return mSalaryFrom;
	}

	public void setSalaryFrom(final int salaryFrom) {
		mSalaryFrom = salaryFrom;
	}

	public int getSalaryTo() {
		return mSalaryTo;
	}

	public void setSalaryTo(final int salaryTo) {
		mSalaryTo = salaryTo;
	}

	public String getCurrency() {
		return mCurrency;
	}

	public void setCurrency(final String currency) {
		mCurrency = currency;
	}

	public String getEmployerName() {
		return mEmployerName;
	}

	public void setEmployerName(final String employerName) {
		mEmployerName = employerName;
	}

	public String getAreaName() {
		return mAreaName;
	}

	public void setAreaName(final String areaName) {
		mAreaName = areaName;
	}

	public Date getPublishedAt() {
		return mPublishedAt;
	}

	public void setPublishedAt(final Date publishedAt) {
		mPublishedAt = publishedAt;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(final String description) {
		mDescription = description;
	}
}
