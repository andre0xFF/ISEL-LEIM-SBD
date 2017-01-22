package chat_bot;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.MySQL;

public class SBD implements SBD_DAO {


	Connection conn = MySQL.get_connection();
	int restaurant_id;
	int client_id;
	int employee_id;

	public SBD(int restaurant_id) throws SQLException {
		this.restaurant_id = restaurant_id;
	}

	public void close() throws SQLException {
		conn.close();
	}

	public String get_owner() throws SQLException, User_not_found {
		String owner = "";
		
		CallableStatement cstmt = this.conn.prepareCall(SBD_DAO.GET_OWNER_SP);
		cstmt.setInt(SBD_DAO.GET_OWNER_IN_PARAM[0], this.restaurant_id);
		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		rs.next();
		
		rs.close();
		cstmt.close();

		return owner;
	}

	@Override
	public String get_ready_orders() throws SQLException, No_orders_found {
		String orders = "";
		
		CallableStatement cstmt = this.conn.prepareCall(SBD_DAO.GET_READY_ORDERS_SP);
		java.sql.Date now = new java.sql.Date(new java.util.Date().getTime());
		cstmt.setDate(SBD_DAO.GET_READY_ORDERS_IN_PARAM[0], now);
		cstmt.setString(SBD_DAO.GET_READY_ORDERS_IN_PARAM[1], "Ready");

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		rs.next();
		
		rs.close();
		cstmt.close();

		return orders;
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
	public String get_most_sold_products_by_week(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_most_profitable_products_by_week(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_products_by_average_time() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_menu(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_menu_by_ingredient(Date date, String ingredient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_menu_rm_ingredient(Date date, String ingredient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_available_ingredients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_orders(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_all_suppliers() {
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
	public final static String[] GET_READY_ORDERS_OUT_PARAM = { "client_order_id", "employee_id" };

	public boolean register_order(int order_id, int client_id, int restaurant_id);
	public boolean next_state(int order_id, int employee_id);
	public boolean add_client(String full_name, String email, String password, String mobile_number, int tax_number, Date birth_date);
	public String get_product(int sku);
	public String get_product(String product_name);
	public String find_nearest_restaurant(String location);
	public String get_recipe(int product_id);
	public String get_most_sold_products_by_week(Date date);
	public String get_most_profitable_products_by_week(Date date);
	public String get_products_by_average_time();
	public String get_menu(Date date);
	public String get_menu_by_ingredient(Date date, String ingredient);
	public String get_menu_rm_ingredient(Date date, String ingredient);
	public String get_available_ingredients();
	public String get_orders(Date date);
	public String get_all_suppliers();
	public String get_ready_orders() throws SQLException, No_orders_found;
}

@SuppressWarnings("serial")
class User_not_found extends Exception {

	public User_not_found(String string) {
		super(string);
	}
}

@SuppressWarnings("serial")
class No_orders_found extends Exception {

	public No_orders_found(String string) {
		super(string);
	}
}
