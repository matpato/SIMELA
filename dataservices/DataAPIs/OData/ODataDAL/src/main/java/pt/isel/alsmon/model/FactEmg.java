package pt.isel.alsmon.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fact_emg database table.
 * 
 */
@Entity
@Table(name="fact_emg")
@NamedQuery(name="FactEmg.findAll", query="SELECT f FROM FactEmg f")
public class FactEmg implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FactEmgPK id;

	@Column(nullable=false)
	private double area;

	@Column(nullable=false, length=50)
	private String electrode;

	@Column(name="emg_id", unique=true, nullable=false)
	private Long emgId;

	@Column(name="high_pass", nullable=false, length=10)
	private String highPass;

	@Column(name="low_pass", nullable=false, length=10)
	private String lowPass;

	@Column(name="nr_peaks", nullable=false)
	private int nrPeaks;

	@Column(nullable=false, length=50)
	private String protocol;

	//bi-directional many-to-one association to DimDate
	@ManyToOne
	@JoinColumn(name="date_keycol", referencedColumnName="keycol", nullable=false, insertable=false, updatable=false)
	private DimDate dimDate;

	//bi-directional many-to-one association to DimMuscle
	@ManyToOne
	@JoinColumn(name="muscle_keycol",  referencedColumnName="keycol", nullable=false, insertable=false, updatable=false)
	private DimMuscle dimMuscle;

	//bi-directional many-to-one association to DimPatient
	@ManyToOne
	@JoinColumn(name="patient_keycol",  referencedColumnName="keycol", nullable=false, insertable=false, updatable=false)
	private DimPatient dimPatient;

	//bi-directional many-to-one association to DimTime
	@ManyToOne
	@JoinColumn(name="time_keycol",  referencedColumnName="keycol", nullable=false, insertable=false, updatable=false)
	private DimTime dimTime;

	public FactEmg() {
	}

	public FactEmgPK getId() {
		return this.id;
	}

	public void setId(FactEmgPK id) {
	}

	public double getArea() {
		return this.area;
	}

	public void setArea(double area) {
	}

	public String getElectrode() {
		return this.electrode;
	}

	public void setElectrode(String electrode) {
	}

	public Long getEmgId() {
		return this.emgId;
	}

	public void setEmgId(Long emgId) {
	}

	public String getHighPass() {
		return this.highPass;
	}

	public void setHighPass(String highPass) {
	}

	public String getLowPass() {
		return this.lowPass;
	}

	public void setLowPass(String lowPass) {
	}

	public int getNrPeaks() {
		return this.nrPeaks;
	}

	public void setNrPeaks(int nrPeaks) {
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
	}

	public DimDate getDimDate() {
		return this.dimDate;
	}

	public void setDimDate(DimDate dimDate) {
	}

	public DimMuscle getDimMuscle() {
		return this.dimMuscle;
	}

	public void setDimMuscle(DimMuscle dimMuscle) {
	}

	public DimPatient getDimPatient() {
		return this.dimPatient;
	}

	public void setDimPatient(DimPatient dimPatient) {
	}

	public DimTime getDimTime() {
		return this.dimTime;
	}

	public void setDimTime(DimTime dimTime) {
	}

}