package pt.isel.alsmon.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the dim_muscle database table.
 * 
 */
@Entity
@Table(name="dim_muscle")
@NamedQuery(name="DimMuscle.findAll", query="SELECT d FROM DimMuscle d")
public class DimMuscle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="keycol", unique=true, nullable=false)
	private int keycol;

	@Column(nullable=false, length=10)
	private String acronym;

	@Column(name="muscle_id", nullable=false)
	private int muscleId;

	@Column(nullable=false, length=50)
	private String name;

	@Column(length=25)
	private String side;

	//bi-directional many-to-one association to FactEmg
	@OneToMany(mappedBy="dimMuscle")
	private List<FactEmg> factEmgs;

	public DimMuscle() {
	}

	public int getKeycol() {
		return this.keycol;
	}

	public void setKeycol(int keycol) {
	}

	public String getAcronym() {
		return this.acronym;
	}

	public void setAcronym(String acronym) {
	}

	public int getMuscleId() {
		return this.muscleId;
	}

	public void setMuscleId(int muscleId) {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
	}

	public String getSide() {
		return this.side;
	}

	public void setSide(String side) {
	}

	public List<FactEmg> getFactEmgs() {
		return this.factEmgs;
	}

	public void setFactEmgs(List<FactEmg> factEmgs) {
	}

}