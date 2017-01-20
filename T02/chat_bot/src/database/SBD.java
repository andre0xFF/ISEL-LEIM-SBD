package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SBD implements SBD_DAO {

	ArrayList<Connection> free_connections = new ArrayList<Connection>();
	ArrayList<Connection> busy_connections = new ArrayList<Connection>();
	
	int restaurant_id;
	int client_id;
	int employee_id;

	public SBD(int restaurant_id) throws SQLException {	
		for (int i = 0; i < 2; i++) {
			free_connections.add(MySQL.get_connection());
		}
		
		this.restaurant_id = restaurant_id;
		
		String[][] r = this.get_ready_orders();
	}
	
	public Connection get_connection() {
		Connection conn = free_connections.remove(0);
		busy_connections.add(conn);
		return conn;
	}
	
	public void close(Connection connection) {
		busy_connections.remove(connection);
		free_connections.add(connection);
	}
	
	public void close() throws SQLException {
		for (int i = 0; i < free_connections.size(); i++) {
			free_connections.get(i).close();
		}
		for (int i = 0; i < busy_connections.size(); i++) {
			busy_connections.get(i).close();
		}
	}
	
	public String[] get_owner() throws SQLException {
		Connection conn = this.get_connection();
		CallableStatement cstmt = conn.prepareCall(SBD_DAO.GET_OWNER_SP);

		cstmt.setInt(SBD_DAO.GET_OWNER_IN_PARAM[0], this.restaurant_id);
		
		cstmt.execute();
		ResultSet rs = cstmt.getResultSet();
		rs.next();
		
		String[] result = {
			String.valueOf(rs.getInt(SBD_DAO.GET_OWNER_OUT_PARAM[0])),
			rs.getString(SBD_DAO.GET_OWNER_OUT_PARAM[1])
		};
		
		rs.close();
		cstmt.close();
		
		return result;
	}
	
	@Override
	public String[][] get_ready_orders() throws SQLException {
		Connection conn = this.get_connection();
		CallableStatement cstmt = conn.prepareCall(SBD_DAO.GET_READY_ORDERS_SP);
		
		java.sql.Date now = new java.sql.Date(new java.util.Date().getTime());
		cstmt.setDate(SBD_DAO.GET_READY_ORDERS_IN_PARAM[0], now);
		cstmt.setString(SBD_DAO.GET_READY_ORDERS_IN_PARAM[1], "Ready");
		
		cstmt.execute();
		ResultSet rs = cstmt.getResultSet();
		
		rs.last();
		String[][] result = new String[rs.getRow()][SBD_DAO.GET_READY_ORDERS_OUT_PARAM.length];
		
		for (int i = 0; rs.next(); i++) {
			result[i][0] = String.valueOf(rs.getInt(SBD_DAO.GET_READY_ORDERS_OUT_PARAM[0]));
			result[i][1] = "TODO";
		}

		rs.close();
		cstmt.close();
		
		return result;
	}

	@Override
	public boolean register_order(int order_id, int client_id, int restaurant_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean next_state(int order_id, int employee_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add_client(String full_name, String email, String password, String mobile_number, int tax_number,
			Date birth_date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String get_product(int sku) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_product(String product_name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String find_nearest_restaurant(String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_recipe(int product_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_most_sold_products_by_week(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_most_profitable_products_by_week(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_products_by_average_time() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_menu(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_menu_by_ingredient(Date date, String ingredient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_menu_rm_ingredient(Date date, String ingredient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_available_ingredients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_orders(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] get_all_suppliers() {
		// TODO Auto-generated method stub
		return null;
	}
}

interface SBD_DAO {
	
	public final static String GET_OWNER_SP = "{CALL get_owner(?)}";
	public final static String[] GET_OWNER_IN_PARAM = { "in_restaurant_id" };
	public final static String[] GET_OWNER_OUT_PARAM = { "id", "full_name" };
	
	public final static String GET_READY_ORDERS_SP = "{CALL get_ready_orders(?, ?)}";
	public final static String[] GET_READY_ORDERS_IN_PARAM = { "in_date", "in_order_state" };
	public final static String[] GET_READY_ORDERS_OUT_PARAM = { "client_order_id" };
	
	public boolean register_order(int order_id, int client_id, int restaurant_id);
	public boolean next_state(int order_id, int employee_id);
	public boolean add_client(String full_name, String email, String password, String mobile_number, int tax_number, Date birth_date);
	public String get_product(int sku);
	public String get_product(String product_name);
	public String find_nearest_restaurant(String location);
	public String get_recipe(int product_id);
	public String[] get_most_sold_products_by_week(Date date);
	public String[] get_most_profitable_products_by_week(Date date);
	public String[] get_products_by_average_time();
	public String[] get_menu(Date date);
	public String[] get_menu_by_ingredient(Date date, String ingredient);
	public String[] get_menu_rm_ingredient(Date date, String ingredient);
	public String[] get_available_ingredients();
	public String[] get_orders(Date date);
	public String[] get_all_suppliers();
	public String[][] get_ready_orders() throws SQLException;
}
