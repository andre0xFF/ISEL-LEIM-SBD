package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
	private static final String ip = "192.168.1.134";
	private static final String port = "3306";
	private static final String db_name = "sbd";
	private static final String user = "root";
	private static final String pswd = "bitnami";
	  
	public static Connection get_connection() throws SQLException {
		
		String c_string = String.format("jdbc:mysql://%s:%s/%s", ip, port, db_name);
	
		return DriverManager.getConnection(c_string, user, pswd);
	}
	
	public static boolean test_connection() {
		Connection connection;
		Statement stmt;
		ResultSet rs;
		String query = "SELECT id from client";
		
		try {
			connection = MySQL.get_connection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			
			rs.close();
			stmt.close();
			connection.close();
			
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean test_stored_procedure() {

		String query = "{CALL get_orders(?, ?)}";
		
		try (
			Connection conn = MySQL.get_connection();
			CallableStatement cstmt = conn.prepareCall(query);
		) {
			cstmt.setDate("in_date", java.sql.Date.valueOf("2017-01-20"));
			cstmt.setString("in_order_state", "Ready");
			cstmt.execute();
			ResultSet rs = cstmt.getResultSet();
			
			while (rs.next()) {
				
				System.out.format(
						"%02d %02d %02d %s\n",
						rs.getInt("client_order_id"),
						rs.getInt("MAX(order_state_id)"),
						rs.getInt("employee_id"),
						rs.getDate("date")
						);
			}
		} catch (Exception e) {
			return false;
			
		}
		
		return true;
	}
}
