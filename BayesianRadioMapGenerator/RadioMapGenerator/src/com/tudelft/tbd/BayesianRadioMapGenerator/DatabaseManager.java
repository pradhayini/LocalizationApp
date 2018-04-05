

package com.tudelft.tbd.BayesianRadioMapGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseReader that provides a connection to a database
 * Reference: https://www.tutorialspoint.com/sqlite/sqlite_java.htm
 */
public class DatabaseManager {
	private static Connection connection = null;
	private static Statement statement = null;
	private static String trainingTableName = "training_measurement";
	private static String pmfTablename = "pmf_parameters";
	
	public DatabaseManager() {
		try {
			Class.forName("org.sqlite.JDBC");
			if(connection == null) {
				connection = DriverManager.getConnection("jdbc:sqlite:resource/training_database.db");
				statement = null;
			}
			if(statement == null) {
				statement = connection.createStatement();
			}
		} catch(ClassNotFoundException | SQLException ex) {
			connection = null;
			statement = null;
			System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
		}
	}
	
	public List<String> getAllBssids() {
		List<String> bssids = new ArrayList<String>();
		if(connection != null && statement != null) {
			try {
				ResultSet results = statement.executeQuery("SELECT DISTINCT bss_id FROM " + trainingTableName);
				while(results.next()) {
					bssids.add(results.getString("bss_id"));
				}
				results.close();
			} catch (SQLException ex) {
				System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
			}
		}
		
		return bssids;
	}
	
	public List<Integer> getAllCellids() {
		List<Integer> cellids = new ArrayList<Integer>();
		if(connection != null && statement != null) {
			try {
				ResultSet results = statement.executeQuery("SELECT DISTINCT cell_id FROM " + trainingTableName);
				while(results.next()) {
					cellids.add(results.getInt("cell_id"));
				}
				results.close();
			}
			catch (SQLException ex) {
				System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
			}
		}
		
		return cellids;
	}
	
	/**
	 * For each cell, determine the number of occurrences of each RSSi value.
	 * Reference : https://stackoverflow.com/a/1217252/2169877	 * 
	 * @param cellid
	 * @return Measurements sets {cellid, bsssid, rssi, rssi Count}
	 */
	public List<Measurement> getHistogramDataForCellid(Integer cellid) {
		List<Measurement> measurements = new ArrayList<Measurement>();
		if(connection != null && statement != null) {
			try {
				ResultSet results = statement.executeQuery(
						"SELECT cell_id, bss_id, rssi, count(*) AS frequency "
						+ "FROM " + trainingTableName 
						+ " WHERE cell_id=" + cellid
						+ " GROUP BY cell_id, bss_id, rssi");
				while(results.next()) {
					measurements.add(new Measurement(
							cellid, 
							results.getString("bss_id"),
							results.getInt("rssi"), 
							results.getInt("frequency")));
				}
				results.close();
			}
			catch (SQLException ex) {
				System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
			}
		}
		
		return measurements;
	}
	
	public List<Integer> getRssiPerCellPerBssid(int cellid, String bssid){
		List<Integer> rssiValues = new ArrayList<Integer>();
		if(connection != null && statement != null) {
			try {
				ResultSet results = statement.executeQuery(
						"SELECT rssi FROM " + trainingTableName + " WHERE cell_id = " + cellid + " AND bss_id = \"" + bssid + "\"");
				while(results.next()) {
					rssiValues.add(results.getInt("rssi"));
				}
			} catch (SQLException ex) {
				System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
			}
		}
		return rssiValues;
	}
	
	/**
	 * Write Gaussian PMF parameters to the database
	 * Create required table, if it doesn't already exist
	 * Reference: http://www.sqlitetutorial.net/sqlite-java/create-table/ 
	 * @param parameterSet
	 */
	public void writePmfParameters(List<PmfParameter> parameterSet) {
		if(connection != null && statement != null) {	        
	        try {
	        	// Create table
	        	statement.execute("CREATE TABLE IF NOT EXISTS " + pmfTablename
		                + " (cell_id INTEGER NOT NULL,"
		                + " bss_id TEXT NOT NULL,"
		                + "	mean REAL NOT NULL,"
		                + "	variance REAL NOT NULL,"
		                + " PRIMARY KEY (cell_id, bss_id))");
	        	
	        	// Insert data into table
	        	for(PmfParameter parameter : parameterSet) {
	        		statement.execute(
	        				"INSERT OR REPLACE INTO "
	        				+ pmfTablename + " (cell_id, bss_id, mean, variance)"
	        				+ " VALUES (" + parameter.getCellId() + ", "
	        				+ "\"" + parameter.getBssId() + "\", "
	        				+ parameter.getMean() + ", "
	        				+ parameter.getVariance() + ")"
	        		);
	        	}
	        	
	        } catch (SQLException ex) {
				System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
			}
		}
	}
	
	/**
	 * Close database connections.
	 */
	public void close() {
		try {
			if(statement != null) {
				statement.close();
			}
			if(connection != null) {
				connection.close();
			}
		} catch (SQLException ex) {
			System.err.println("[" + ex.getClass().getName() + "] " + ex.getMessage());
		}
	}
}
