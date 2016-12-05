package pt.isel.alsmon.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fact_emg database table.
 * 
 */
@Embeddable
public class FactEmgPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="date_keycol", insertable=true, updatable=false, unique=true, nullable=false)
	private int dateKeycol;

	@Column(name="time_keycol", insertable=true, updatable=false, unique=true, nullable=false)
	private int timeKeycol;

	@Column(name="patient_keycol", insertable=true, updatable=false, unique=true, nullable=false)
	private int patientKeycol;

	@Column(name="muscle_keycol", insertable=true, updatable=false, unique=true, nullable=false)
	private int muscleKeycol;

	public FactEmgPK() {
	}
	public int getDateKeycol() {
		return this.dateKeycol;
	}
	public void setDateKeycol(int dateKeycol) {
	}
	public int getTimeKeycol() {
		return this.timeKeycol;
	}
	public void setTimeKeycol(int timeKeycol) {
	}
	public int getPatientKeycol() {
		return this.patientKeycol;
	}
	public void setPatientKeycol(int patientKeycol) {
	}
	public int getMuscleKeycol() {
		return this.muscleKeycol;
	}
	public void setMuscleKeycol(int muscleKeycol) {
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FactEmgPK)) {
			return false;
		}
		FactEmgPK castOther = (FactEmgPK)other;
		return 
			(this.dateKeycol == castOther.dateKeycol)
			&& (this.timeKeycol == castOther.timeKeycol)
			&& (this.patientKeycol == castOther.patientKeycol)
			&& (this.muscleKeycol == castOther.muscleKeycol);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dateKeycol;
		hash = hash * prime + this.timeKeycol;
		hash = hash * prime + this.patientKeycol;
		hash = hash * prime + this.muscleKeycol;
		
		return hash;
	}
}