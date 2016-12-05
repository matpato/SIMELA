package pt.isel.alsmon.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the dim_patient database table.
 * 
 */
@Entity
@Table(name="dim_patient")
@NamedQuery(name="DimPatient.findAll", query="SELECT d FROM DimPatient d")
public class DimPatient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="keycol", unique=true, nullable=false)
	private int keycol;

	@Column(name="at_high", nullable=false, precision=10, scale=3)
	private BigDecimal atHigh;

	@Column(name="at_low", nullable=false, precision=10, scale=3)
	private BigDecimal atLow;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date birthdate;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date createdon;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date diagnosedon;

	@Column(name="fcr_high", nullable=false, precision=10, scale=3)
	private BigDecimal fcrHigh;

	@Column(name="fcr_low", nullable=false, precision=10, scale=3)
	private BigDecimal fcrLow;

	@Column(nullable=false, length=1)
	private String gender;

	@Column(nullable=false, length=50)
	private String name;

	@Column(name="patient_id", nullable=false, length=10)
	private String patientId;

	@Column(nullable=false, length=18)
	private String rowversion;

	@Column(name="scm_high", nullable=false, precision=10, scale=3)
	private BigDecimal scmHigh;

	@Column(name="scm_low", nullable=false, precision=10, scale=3)
	private BigDecimal scmLow;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date updatedon;

	//bi-directional many-to-one association to FactEmg
	@OneToMany(mappedBy="dimPatient")
	private List<FactEmg> factEmgs;

	public DimPatient() {
	}

	public int getKeycol() {
		return this.keycol;
	}

	public void setKeycol(int keycol) {
	}

	public BigDecimal getAtHigh() {
		return this.atHigh;
	}

	public void setAtHigh(BigDecimal atHigh) {
	}

	public BigDecimal getAtLow() {
		return this.atLow;
	}

	public void setAtLow(BigDecimal atLow) {
	}

	public Date getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(Date birthdate) {
	}

	public Date getCreatedon() {
		return this.createdon;
	}

	public void setCreatedon(Date createdon) {
	}

	public Date getDiagnosedon() {
		return this.diagnosedon;
	}

	public void setDiagnosedon(Date diagnosedon) {
	}

	public BigDecimal getFcrHigh() {
		return this.fcrHigh;
	}

	public void setFcrHigh(BigDecimal fcrHigh) {
	}

	public BigDecimal getFcrLow() {
		return this.fcrLow;
	}

	public void setFcrLow(BigDecimal fcrLow) {
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
	}

	public String getPatientId() {
		return this.patientId;
	}

	public void setPatientId(String patientId) {
	}

	public String getRowversion() {
		return this.rowversion;
	}

	public void setRowversion(String rowversion) {
	}

	public BigDecimal getScmHigh() {
		return this.scmHigh;
	}

	public void setScmHigh(BigDecimal scmHigh) {
	}

	public BigDecimal getScmLow() {
		return this.scmLow;
	}

	public void setScmLow(BigDecimal scmLow) {
	}

	public Date getUpdatedon() {
		return this.updatedon;
	}

	public void setUpdatedon(Date updatedon) {
	}

	public List<FactEmg> getFactEmgs() {
		return this.factEmgs;
	}

	public void setFactEmgs(List<FactEmg> factEmgs) {
	}


}