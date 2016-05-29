package frames;

import java.sql.*; 
import java.util.*;

import org.apache.log4j.Logger;
import org.testng.Assert;

//-----------------------------------------------------------------------------------
// 					This class is used to execute SQL queries on Oracle database
//-----------------------------------------------------------------------------------

public class Dataset {
	private String dbDriver;
	private String dbUser;
	private String dbPwd;
	private String dbURL;
	private Connection con = null;
	private Statement smt = null;
	private Logger log = Logger.getLogger(Dataset.class.getName());

	// Constructor
	// Initializes the database connection attributes from run.properties
	public Dataset(){
		log.info("@ Dataset constructor");
		Read_properties runProperty = new Read_properties("run");
		dbDriver=runProperty.getPropertyValue("USER_DB_DRIVER");
		dbURL=runProperty.getPropertyValue("USER_DB_URL");
		dbUser=runProperty.getPropertyValue("USER_DB_USERNAME");
		dbPwd=runProperty.getPropertyValue("USER_DB_PASSWORD");
	}


	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It gets the output of a query execution
	// Parameter(s) : SQL Query
	// Return: List of String - Contains the row-wise data
	//-----------------------------------------------------------------------------------
	public List<String> getData(String sQuery) {
		log.info("@ Dataset.getData");
		List<String> lsData=new ArrayList<String>();
		String str="";
		int i=0;
		try {
			// Execute the SQL query
			ResultSet rslt = executeSQL(sQuery);

			// Iterate the whole ResultSet row-by-row
			while (rslt.next()) {
				str="";

				//Get each column value in a row
				i=1;
				while (true){
					try{
						str = str + "," + rslt.getString(i);
						log.info("col" + i + ":" + str);
						i++;

					} catch (SQLException e){
						break;
					}
				}

				str=str.substring(1);
				log.info("Rowdata: " + str);

				// Add the row data to the list
				lsData.add(str);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
			//throw (e);
		} finally {
			closeDbConnection();
		}
		return lsData;
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It establishes the database connection
	// Parameter(s) : NA
	// Return: Connection object
	//-----------------------------------------------------------------------------------
	private void getDatabaseConnection() {
		log.info("@ Dataset.getDatabaseConnection");
		try {
			Class.forName(dbDriver);
			log.info("** Class.forName: " + dbURL);

		} catch(java.lang.ClassNotFoundException e) {
			e.printStackTrace();
			Assert.fail();
		}

		try {
			log.info("** DriverManager.getConnection( " + dbURL + "," + dbUser + "," + dbPwd + ")");
			// Establish the database connection
			con = DriverManager.getConnection(dbURL, dbUser, dbPwd);
		} catch(SQLException e) {
			e.printStackTrace();
			Assert.fail(); 
		}

		if (con==null){
			log.error("Could not Get Connection");
			Assert.fail();
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It executes the query on the database
	// Parameter(s) : SQL Query
	// Return: ResultSet object
	//-----------------------------------------------------------------------------------
	public ResultSet executeSQL(String sQuery) {
		log.info("@ Dataset.executeSQL");
		ResultSet rslt = null;
		// Call to method to get the database connection
		getDatabaseConnection();

		try {

			smt = con.createStatement();
			// Execute the SQL query
			rslt = smt.executeQuery(sQuery);

		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
		if (rslt==null){
			log.error("Error during execution of query: " + sQuery);
			Assert.fail();
		}
		return rslt;
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It closes the database connection
	// Parameter(s) : NA
	// Return: VOID
	//-----------------------------------------------------------------------------------
	private void closeDbConnection(){
		try {
			smt.close();
			con.close();
			log.info("Database connection is closed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

