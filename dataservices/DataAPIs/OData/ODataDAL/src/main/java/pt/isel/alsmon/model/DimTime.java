package pt.isel.alsmon.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the dim_time database table.
 * 
 */
@Entity
@Table(name="dim_time")
@NamedQuery(name="DimTime.findAll", query="SELECT d FROM DimTime d")
public class DimTime implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="keycol", unique=true, nullable=false)
	private int keycol;

	@Column(nullable=false, length=11)
	private String fulltime12;

	@Column(nullable=false, length=8)
	private String fulltime24;

	@Column(nullable=false, length=5)
	private String hour12;

	@Column(nullable=false)
	private short hour24;

	@Column(nullable=false)
	private short minute;

	@Column(name="period_en", nullable=false, length=25)
	private String periodEn;

	@Column(name="period_pt", nullable=false, length=25)
	private String periodPt;

	@Column(nullable=false)
	private short second;

	//bi-directional many-to-one association to FactEmg
	@OneToMany(mappedBy="dimTime")
	private List<FactEmg> factEmgs;

	public DimTime() {
	}

	public int getKeycol() {
		return this.keycol;
	}

	public void setKeycol(int keycol) {
	}

	public String getFulltime12() {
		return this.fulltime12;
	}

	public void setFulltime12(String fulltime12) {
	}

	public String getFulltime24() {
		return this.fulltime24;
	}

	public void setFulltime24(String fulltime24) {
	}

	public String getHour12() {
		return this.hour12;
	}

	public void setHour12(String hour12) {
	}

	public short getHour24() {
		return this.hour24;
	}

	public void setHour24(short hour24) {
	}

	public short getMinute() {
		return this.minute;
	}

	public void setMinute(short minute) {
	}

	public String getPeriodEn() {
		return this.periodEn;
	}

	public void setPeriodEn(String periodEn) {
	}

	public String getPeriodPt() {
		return this.periodPt;
	}

	public void setPeriodPt(String periodPt) {
	}

	public short getSecond() {
		return this.second;
	}

	public void setSecond(short second) {
	}

	public List<FactEmg> getFactEmgs() {
		return this.factEmgs;
	}

	public void setFactEmgs(List<FactEmg> factEmgs) {
	}

}