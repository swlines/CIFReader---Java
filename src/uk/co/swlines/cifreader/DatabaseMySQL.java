package uk.co.swlines.cifreader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseMySQL {	
	private Connection connection = null;
	
	protected void createConnection(String location, String username, String password) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {		
		connection = DriverManager.getConnection(location, username, password);
	}
	
	protected Connection getConnection() {
		return connection;
	}
	
	protected boolean isValidConnection() {
		return connection != null;
	}
	
	protected void closeConnection() {
		if(isValidConnection()) {
			try {
				connection.close();
			} catch (SQLException e) {

			}
		}
	}
}
