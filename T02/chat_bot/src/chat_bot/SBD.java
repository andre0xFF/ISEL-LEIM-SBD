package chat_bot;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.MySQL;

public class SBD implements SBD_DAO {


	private Connection conn = MySQL.get_connection();
	private int restaurant_id;
	private int client_id;
	private int employee_id;

	public SBD(int restaurant_id) throws SQLException {
		this.restaurant_id = restaurant_id;
	}

	public void close() throws SQLException {
		conn.close();
	}

	public String get_owner() throws SQLException, User_not_found {
		String sp = "{CALL get_owner(?)}";
		String[] in = { "in_restaurant_id" };
		String[] out = { "full_name" };

		CallableStatement cstmt = this.conn.prepareCall(sp);
		cstmt.setInt(in[0], this.restaurant_id);
		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		rs.next();
		
		String owner = rs.getString(out[0]);

		rs.close();
		cstmt.close();

		return owner;
	}

	@Override
	public ResultSet get_ready_orders() throws SQLException {
		String sp = "{CALL get_orders_with_state(?, ?)}";
		String[] in = { "in_date", "in_order_state" };
		String[] out = { "client_order_id", "employee_id" };

		CallableStatement cstmt = this.conn.prepareCall(sp);
		java.sql.Date now = new java.sql.Date(new java.util.Date().getTime());
		now = java.sql.Date.valueOf("2017-01-30");
		cstmt.setDate(in[0], now);
		cstmt.setString(in[1], "Ready");

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();

		return rs;
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
	public ResultSet get_ready_orders() throws SQLException;
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
