package pt.isel.alsmon.olap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mondrian.olap.MondrianProperties;

import org.olap4j.*;
import org.olap4j.metadata.*;

public class Utilities {

	static MondrianProperties prop = MondrianProperties.instance();

	private static void infoCube() throws SQLException, ClassNotFoundException { 	

		Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
		Connection connection = DriverManager.getConnection(prop.getProperty("mondrian­.­connectString"));

		OlapWrapper wrapper = (OlapWrapper) connection;
		OlapConnection olapConnection = wrapper.unwrap(OlapConnection.class);
		NamedList<Cube> cubes = olapConnection.getOlapSchema().getCubes();
		for (Cube eachCube : cubes) {

			System.out.println(" Cube name..." + eachCube.getName());

			for (Measure measure : eachCube.getMeasures()) {

				System.out.println(" Measures " + measure.getName());
				System.out.println("Measure Levels...."
						+ measure.getLevel().getCaption());

			}

			for (Hierarchy hierarchy : eachCube.getHierarchies()) {

				System.out.println("hierarchy " + hierarchy.getName());
				NamedList<Level> levels = hierarchy.getLevels();

				for (Level l : levels) {

					System.out.println(" Hierarchy levels " + l.getName());
					List<Member> members = l.getMembers();
					for(Member member:members){
						System.out.println(" Member name " +member.getName());
					}

				}
			}

		}
	}

	private static void printResult(CellSet result) {
		List<CellSetAxis> cellSetAxes = result.getAxes();

		// Print headings.
		System.out.print("\t");
		CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
		for (Position position : columnsAxis.getPositions()) {
			Member measure = position.getMembers().get(0);
			System.out.print(measure.getName());
		}   
		// Print rows.
		CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
		List<Integer> coordList = new ArrayList<Integer>(3);
		int row = 0;
		for (Position rowPosition : rowsAxis.getPositions()) {
			assert rowPosition.getOrdinal() == row;
			coordList.set(0, row++);

			// Print the row label.
			int memberOrdinal = 0;
			for (Member member : rowPosition.getMembers()) {
				if (memberOrdinal++ > 0) {
					System.out.print('\t');
				}
				System.out.print(member.getName());
			}

			//Print the value of the cell in each column.
			int column = 0;
			for (Position columnPosition : columnsAxis.getPositions()) {
				assert columnPosition.getOrdinal() == column;
				coordList.set(1, column++);
				Cell cell = result.getCell(coordList);
				System.out.print('\t');
				System.out.print(cell.getFormattedValue());
			}
			System.out.println();
		}
	}

}
