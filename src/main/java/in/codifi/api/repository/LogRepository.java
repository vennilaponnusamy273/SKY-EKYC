package in.codifi.api.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.DatabaseMetaData;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;


import io.quarkus.logging.Log;

@ApplicationScoped
public class LogRepository {

	@Named("logs")
	@Inject
	DataSource dataSource;

	/*
	 * method to get specific database total number of table names
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	public List<String> getExistingTables() {
		List<String> tableNames = new ArrayList<>();
		Connection connection = null;
		DatabaseMetaData metaData = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			metaData = connection.getMetaData();
			String[] tableTypes = { "TABLE" };
			resultSet = metaData.getTables("logs_db_sky", null, "%", tableTypes);
			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				tableNames.add(tableName);
			}

		} catch (Exception e) {
			Log.error("getExistingTables -" + e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("getExistingTables -" + e);
			}
		}
		return tableNames;
	}

	/*
	 * method to create a table from database
	 * 
	 * @author SOWMIYA
	 * 
	 * @return
	 */
	public void createTables(List<String> tableToCreate) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			String databaseName = "logs_db_sky";
			for (String tableName : tableToCreate) {
				String sql = "CREATE TABLE " + databaseName + "." + tableName
						+ " (id int AUTO_INCREMENT  PRIMARY KEY, application_id VARCHAR(15),"
						+ "uri text,method varchar(150),req_id VARCHAR(15),"
						+ "req_body longtext,res_body longtext,device_ip varchar(100),"
						+ "user_agent longtext,content_type varchar(200),session mediumtext)";
				statement.executeUpdate(sql);
			}
		} catch (Exception e) {
			Log.error("createTables -" + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("createTables -" + e);
			}
		}

	}
	public void createRestTable(List<String> tableToCreate) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			String databaseName = "logs_db_sky";
			for (String tableName : tableToCreate) {
				String sql = "CREATE TABLE " + databaseName + "." + tableName
					    + " (id int AUTO_INCREMENT PRIMARY KEY, application_id VARCHAR(15),"
					    + " uri text, method varchar(150),"
					    + " req_body longtext, res_body longtext,"
					    + " createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					    + " updatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
					    + " activeStatus BOOLEAN)";

				statement.executeUpdate(sql);
			}
		} catch (Exception e) {
			Log.error("createTables -" + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("createTables -" + e);
			}
		}

	}

}
