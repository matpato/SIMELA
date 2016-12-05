package pt.isel.alsmon.olap;
import mondrian.olap.MondrianProperties;

import org.olap4j.*;
import org.olap4j.layout.*;
//import org.olap4j.mdx.*;
//import org.olap4j.mdx.parser.MdxParser;
//import org.olap4j.mdx.parser.MdxParserFactory;
import org.apache.log4j.Logger;





import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class XMLAClient 
{
	static Logger log = Logger.getLogger(XMLAClient.class.getName());
	static MondrianProperties prop = MondrianProperties.instance();
	
	

	public static void main( String[] args ) throws ClassNotFoundException, SQLException
	{
		// Register driver.
		Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
		
		
		
		Authenticator.setDefault(new Authenticator() {
			 @Override
			        protected PasswordAuthentication getPasswordAuthentication() {
			         return new PasswordAuthentication(
			   "alsmon", "Alsmisel2015".toCharArray());
			        }
			});
		
		// Create local connection.
/*		Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
		Connection connection = DriverManager.getConnection(prop.getProperty("mondrian­.­connectString"));
		OlapWrapper wrapper = (OlapWrapper) connection;
		OlapConnection olapConnection = wrapper.unwrap(OlapConnection.class);*/
		
		//Create XMLA connection
		Connection connection = DriverManager.getConnection(
			    "jdbc:xmla:Server=https://alsmon.adeetc.e.ipl.pt:8443/OlapXMLA/xmla;"
			    + "Catalog=ALSMon");
		OlapWrapper wrapper = (OlapWrapper) connection;
		OlapConnection olapConnection = wrapper.unwrap(OlapConnection.class);

		
/*		String MDX = "SELECT { [Measures].[area] } ON COLUMNS, \n"
				+ "{ [DimDate.year-quarter-monthname_pt-day].[2015].[2015 - 3].Children } ON ROWS \n"
				+ "FROM ALSMon";*/
		
		String MDX = "SELECT { [DimDate.year-quarter-monthname_pt-day].[2015].[2015 - 2].Children, [DimDate.year-quarter-monthname_pt-day].[2015].[2015 - 3].Children } ON COLUMNS, \n"
      			+ " { [DimPatient.name].Children } ON ROWS\n"
				+ "FROM ALSMon";
		
		// Create a parser.
//		MdxParserFactory parserFactory = olapConnection.getParserFactory();
//	    MdxParser parser = parserFactory.createMdxParser(olapConnection);
//		SelectNode query = parser.parseSelect(MDX);
//		query.getAxisList().get(0).setNonEmpty(false);

		// Create statement.
		OlapStatement statement = olapConnection.createStatement();
		CellSet result = statement.executeOlapQuery(MDX);
		
		// Iterate over result
		for (Position axis_0 : result.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositions()) {
			for (Position axis_1 : result.getAxes().get(Axis.ROWS.axisOrdinal()).getPositions()) {
				Cell currentCell = result.getCell(axis_0, axis_1);
				Object value = currentCell.getValue();
			}
		}
		
		// Result to stdout
		CellSetFormatter formatter = new RectangularCellSetFormatter(false);
		formatter.format(result, new PrintWriter(System.out, true));
		
		statement.close();
	    connection.close();


	}



}
