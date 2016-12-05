package pt.isel.alsmon.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the dim_date database table.
 * 
 */
@Entity
@Table(name="dim_date")
@NamedQuery(name="DimDate.findAll", query="SELECT d FROM DimDate d")
public class DimDate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="keycol", unique=true, nullable=false)
	private int keycol;

	@Temporal(TemporalType.DATE)
	@Column(name="date_iso", nullable=false)
	private Date dateIso;

	@Column(name="dayname_en", nullable=false, length=15)
	private String daynameEn;

	@Column(name="dayname_pt", nullable=false, length=15)
	private String daynamePt;

	@Column(nullable=false)
	private short dayofmonth;

	@Column(nullable=false)
	private short dayofweek;

	@Column(name="fulldate_en", nullable=false, length=50)
	private String fulldateEn;

	@Column(name="fulldate_pt", nullable=false, length=50)
	private String fulldatePt;

	@Column(name="monthname_en", nullable=false, length=25)
	private String monthnameEn;

	@Column(name="monthname_pt", nullable=false, length=25)
	private String monthnamePt;

	@Column(nullable=false)
	private short monthnumber;

	@Column(nullable=false)
	private short quarter;

	@Column(nullable=false)
	private short weekofyear;

	@Column(nullable=false)
	private short year;

	@Column(name="year_monthname_en", nullable=false, length=32)
	private String yearMonthnameEn;

	@Column(name="year_monthname_pt", nullable=false, length=32)
	private String yearMonthnamePt;

	@Column(name="year_monthnumber", nullable=false, length=9)
	private String yearMonthnumber;

	@Column(name="year_quarter", nullable=false, length=9)
	private String yearQuarter;

	@Column(name="year_weekofyear", nullable=false, length=9)
	private String yearWeekofyear;

	//bi-directional many-to-one association to FactEmg
	@OneToMany(mappedBy="dimDate")
	private List<FactEmg> factEmgs;

	public DimDate() {
	}

	public int getKeycol() {
		return this.keycol;
	}

	public void setKeycol(int keycol) {
	}

	public Date getDateIso() {
		return this.dateIso;
	}

	public void setDateIso(Date dateIso) {
	}

	public String getDaynameEn() {
		return this.daynameEn;
	}

	public void setDaynameEn(String daynameEn) {
	}

	public String getDaynamePt() {
		return this.daynamePt;
	}

	public void setDaynamePt(String daynamePt) {
	}

	public short getDayofmonth() {
		return this.dayofmonth;
	}

	public void setDayofmonth(short dayofmonth) {
	}

	public short getDayofweek() {
		return this.dayofweek;
	}

	public void setDayofweek(short dayofweek) {
	}

	public String getFulldateEn() {
		return this.fulldateEn;
	}

	public void setFulldateEn(String fulldateEn) {
	}

	public String getFulldatePt() {
		return this.fulldatePt;
	}

	public void setFulldatePt(String fulldatePt) {
	}

	public String getMonthnameEn() {
		return this.monthnameEn;
	}

	public void setMonthnameEn(String monthnameEn) {
	}

	public String getMonthnamePt() {
		return this.monthnamePt;
	}

	public void setMonthnamePt(String monthnamePt) {
	}

	public short getMonthnumber() {
		return this.monthnumber;
	}

	public void setMonthnumber(short monthnumber) {
	}

	public short getQuarter() {
		return this.quarter;
	}

	public void setQuarter(short quarter) {
	}

	public short getWeekofyear() {
		return this.weekofyear;
	}

	public void setWeekofyear(short weekofyear) {
	}

	public short getYear() {
		return this.year;
	}

	public void setYear(short year) {
	}

	public String getYearMonthnameEn() {
		return this.yearMonthnameEn;
	}

	public void setYearMonthnameEn(String yearMonthnameEn) {
	}

	public String getYearMonthnamePt() {
		return this.yearMonthnamePt;
	}

	public void setYearMonthnamePt(String yearMonthnamePt) {
	}

	public String getYearMonthnumber() {
		return this.yearMonthnumber;
	}

	public void setYearMonthnumber(String yearMonthnumber) {
	}

	public String getYearQuarter() {
		return this.yearQuarter;
	}

	public void setYearQuarter(String yearQuarter) {
	}

	public String getYearWeekofyear() {
		return this.yearWeekofyear;
	}

	public void setYearWeekofyear(String yearWeekofyear) {
	}

	public List<FactEmg> getFactEmgs() {
		return this.factEmgs;
	}

	public void setFactEmgs(List<FactEmg> factEmgs) {
	}



}