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
	public String get_orders(Date date, String state) throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_orders_by_state(?, ?, ?)}";
		String[] in = { "in_date", "in_order_state", "in_restaurant_id" };
		String[] out = { "client_order_id", "time" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setDate(in[0], date);
		cstmt.setString(in[1], state);
		cstmt.setInt(in[2], this.restaurant_id);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no orders\n");
		}
		
		do {
			results += String.format("%-4d %-12s\n", rs.getInt(out[0]), rs.getTime(out[1]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();

		return results;
	}

	@Override
	public boolean register_order(String order_id, String client_id, String restaurant_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean next_state(String order_id, String employee_id) throws SQLException, SQL_invalid_results {
		String sp = "{CALL order_next_state(?, ?)}";
		String[] in = { "in_client_order_id", "in_employee_id" };
		String[] out = { "client_order_id" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setInt(in[0], Integer.parseInt(order_id));
		cstmt.setInt(in[1], Integer.parseInt(employee_id));

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		
		if (!rs.first()) {
			throw new SQL_invalid_results("Could not move order to next state\n");
		}
		
		rs.close();
		cstmt.close();

		return true;
	}

	@Override
	public boolean add_client(String full_name, String email, String password, String mobile_number, int tax_number,
			Date birth_date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String get_product(String product_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String find_nearest_restaurant(String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_recipe(String product_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_most_sold_products_by_week(Date date) throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_most_sold_products_by_week(?, ?)}";
		String[] in = { "in_date", "in_restaurant_id" };
		String[] out = { "Product", "Quantity of products" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setDate(in[0], date);
		cstmt.setInt(in[1], this.restaurant_id);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no clients orders\n");
		}
		
		do {
			results += String.format("%-15s %-4d\n", rs.getString(out[0]), rs.getInt(out[1]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();

		return results;
	}

	@Override
	public String get_most_profitable_products_by_month(Date date) throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_most_profitable_products_by_month(?, ?)}";
		String[] in = { "in_date", "in_restaurant_id" };
		String[] out = { "Product", "Revenue" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setDate(in[0], date);
		cstmt.setInt(in[1], this.restaurant_id);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no clients orders\n");
		}
		
		do {
			results += String.format("%-15s € %-4d\n", rs.getString(out[0]), rs.getInt(out[1]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();

		return results;
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
	public String get_available_ingredients() throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_ingredients_stock(?)}";
		String[] in = { "in_restaurant_id" };
		String[] out = { "name", "quantity" };
		
		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setInt(in[0], this.restaurant_id);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no orders\n");
		}
		
		do {
			results += String.format("%-15s %-4d\n", rs.getString(out[0]), rs.getInt(out[1]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();
		
		return results;
	}

	@Override
	public String get_all_suppliers() {
		// TODO Auto-generated method stub
		return null;
	}

	public int get_employee_id(String email) throws SQLException, User_not_found {
		String sp = "{CALL get_employee(?)}";
		String[] in = { "in_email" };
		String[] out = { "employee.id" };
		
		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setString(in[0], email);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		int results;
		
		if (rs.next()) {
			results = rs.getInt(out[0]);
		}
		else {
			throw new User_not_found("Invalid email.\n");
		}
		
		rs.close();
		cstmt.close();
		
		return results;
	}

	@Override
	public String get_order_details(String order_id) throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_order_details(?)}";
		String[] in = { "in_client_order_id" };
		String[] out = { "Order #", "Product state", "date", "time", "Employee" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setInt(in[0], Integer.parseInt(order_id));

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no orders\n");
		}
		
		do {
			results += String.format("%-4d %-15s %-12s %-12s %-15s\n", rs.getInt(out[0]), rs.getString(out[1]), rs.getDate(out[2]), rs.getTime(out[3]), rs.getString(out[4]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();

		return results;
	}

	@Override
	public String get_clients_reveneu(Date date) throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_clients_revenue_by_trimester(?, ?)}";
		String[] in = { "in_date", "in_restaurant_id" };
		String[] out = { "Client name", "Total amount spent" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setDate(in[0], date);
		cstmt.setInt(in[1], this.restaurant_id);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no clients orders\n");
		}
		
		do {
			results += String.format("%-15s € %-4d\n", rs.getString(out[0]), rs.getInt(out[1]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();

		return results;
	}

	@Override
	public String get_products_by_avg_time() throws SQLException, SQL_invalid_results {
		String sp = "{CALL get_products_avg_time(?}";
		String[] in = { "in_restaurant_id" };
		String[] out = { "name", "Average time" };

		CallableStatement cstmt = this.conn.prepareCall(sp);

		cstmt.setInt(in[0], this.restaurant_id);

		cstmt.execute();

		ResultSet rs = cstmt.getResultSet();
		
		String results = "";
		
		if (!rs.first()) {
			throw new SQL_invalid_results("There are no clients orders\n");
		}
		
		do {
			results += String.format("%-8s € %-8s\n", rs.getString(out[0]), rs.getString(out[1]));
		} while(rs.next());
		
		rs.close();
		cstmt.close();

		return results;
	}

}

interface SBD_DAO {

	public boolean register_order(String order_id, String client_id, String restaurant_id);
	public boolean next_state(String order_id, String employee_id) throws SQLException, SQL_invalid_results;
	public boolean add_client(String full_name, String email, String password, String mobile_number, int tax_number, Date birth_date);
	public String get_product(String product_id);
	public String find_nearest_restaurant(String location);
	public String get_recipe(String product_id);
	public String get_most_profitable_products_by_month(Date date) throws SQLException, SQL_invalid_results;
	public String get_most_sold_products_by_week(Date date) throws SQLException, SQL_invalid_results;
	public String get_clients_reveneu(Date date) throws SQLException, SQL_invalid_results;
	public String get_products_by_avg_time() throws SQLException, SQL_invalid_results;
	public String get_menu(Date date);
	public String get_menu_by_ingredient(Date date, String ingredient);
	public String get_menu_rm_ingredient(Date date, String ingredient);
	public String get_available_ingredients() throws SQLException, SQL_invalid_results;
	public String get_orders(Date date, String state) throws SQLException, SQL_invalid_results;
	public String get_all_suppliers();
	public String get_order_details(String order_id) throws SQLException, SQL_invalid_results;
//	public String get_ready_orders() throws SQLException, SQL_invalid_results;
}

@SuppressWarnings("serial")
class User_not_found extends Exception {

	public User_not_found(String string) {
		super(string);
	}
}

@SuppressWarnings("serial")
class SQL_invalid_results extends Exception {

	public SQL_invalid_results(String string) {
		super(string);
	}
}
