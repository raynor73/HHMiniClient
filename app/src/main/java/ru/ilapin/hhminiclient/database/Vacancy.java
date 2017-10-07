package ru.ilapin.hhminiclient.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = Vacancy.TABLE_NAME)
public class Vacancy {

	public static final String TABLE_NAME = "Vacancy";

	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_SALARY_FROM = "salaryFrom";
	public static final String COLUMN_NAME_SALARY_TO = "salaryTo";
	public static final String COLUMN_NAME_CURRENCY = "currency";
	public static final String COLUMN_NAME_EMPLOYER_NAME = "employerName";
	public static final String COLUMN_NAME_AREA_NAME = "areaName";
	public static final String COLUMN_NAME_PUBLISHED_AT = "publishedAt";

	@DatabaseField(id = true, columnName = COLUMN_NAME_ID)
	private int mId;
	@DatabaseField(columnName = COLUMN_NAME_NAME)
	private String mName;
	@DatabaseField(columnName = COLUMN_NAME_SALARY_FROM)
	private int mSalaryFrom;
	@DatabaseField(columnName = COLUMN_NAME_SALARY_TO)
	private int mSalaryTo;
	@DatabaseField(columnName = COLUMN_NAME_CURRENCY)
	private String mCurrency;
	@DatabaseField(columnName = COLUMN_NAME_EMPLOYER_NAME)
	private String mEmployerName;
	@DatabaseField(columnName = COLUMN_NAME_AREA_NAME)
	private String mAreaName;
	@DatabaseField(columnName = COLUMN_NAME_PUBLISHED_AT, dataType = DataType.DATE_LONG)
	private Date mPublishedAt;

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
}
