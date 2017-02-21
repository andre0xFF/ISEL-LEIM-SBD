package chat_bot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class Bot {

	Profile user;
	Profile[] profiles;

	public Bot(Profile[] profiles) {
		this.profiles = profiles;
	}

	public void run() throws SQLException {
		int idx = -1;

		while (true) {
			Output.write(String.format("%s\n", Output.get_welcome()));

			String[] profiles_names = new String[profiles.length];
			for (int i = 0; i < profiles.length; i++) {
				profiles_names[i] = profiles[i].get_profile_name();
			}

			Output.write(Menu.print(profiles_names));
			idx = Input.get_user_option(profiles_names);

			if (idx == -2) { return; }

			// get the chosen profile
			this.user = profiles[idx];

			try {
				Output.write(String.format("%s, %s\n", this.user.get_greeting(), Output.get_welcome_profile()));
				this.user.run();
				Thread.sleep(3500);

			} catch (User_not_found e) {
				Output.write(e.getMessage());

			} catch (InterruptedException e) {}

			// the user logged out by now, clear the screen and loop
			Output.clear_screen();
		}
	}
}

interface Profile {

	public void run() throws SQLException, User_not_found;
	public String get_profile_name();
	public String get_greeting() throws User_not_found, SQLException;
}

interface Menu {

	public String[] get_menu_options();

	public static String print(String[] options) {
		String output = "";
		for (int i = 0; i < options.length; i++) {
			output += String.valueOf(i + 1) + ". " + options[i] + "\n";
		}

		return output;
	}
}



interface Client_model {

	public String get_name();
	public int get_id();
}

class Client implements Profile, Menu, Client_model {

	private SBD db;
	public final String PROFILE_NAME = "Client";
	public final String GREETING = "Welcome!";
	public final String[] MENU_OPTIONS = {
		"View today's menu",
		" - Filter by ingredient",
		" - Remove ingredient",
		"Make an order",
		"View recipe",
		"Find nearest restaurant"
	};

	String name = "";
	int id;

	@Override
	public String[] get_menu_options() { return this.MENU_OPTIONS; }
	@Override
	public String get_greeting() { return this.GREETING; }
	@Override
	public String get_profile_name() { return this.PROFILE_NAME; }
	@Override
	public String get_name() { return this.name; }
	@Override
	public int get_id() { return this.id; }

	public Client(SBD database) {
		this.db = database;
	}

	@Override
	public void run() {
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }

			Output.write(idx);
		}
	}
}

class Chef implements Profile, Menu {

	private SBD db;
	public final String PROFILE_NAME = "Chef";
	public final String GREETING = "Bonjour chef!";
	public final String[] MENU_OPTIONS = {
		"View current orders",
		"Advance an order"
	};
	private int id;

	@Override
	public String[] get_menu_options() { return this.MENU_OPTIONS; }
	@Override
	public String get_greeting() { return this.GREETING; }
	@Override
	public String get_profile_name() { return this.PROFILE_NAME; }


	public Chef(SBD database) {
		this.db = database;
	}

	@Override
	public void run() throws SQLException, User_not_found {
		this.ask_who();
		
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }
			
			if (idx == 0) { this.write_orders(); }
			if (idx == 1) { this.advance_order(); }

		}
	}
	
	private void ask_who() throws SQLException, User_not_found {
		Output.write("What's your email?");
		String email = Input.read();
		this.id = db.get_employee_id(email);
	}
	
	private void write_orders() throws SQLException {
		String orders;
		
		try {
			java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
			Output.write("Orders accepted:");
			orders = db.get_orders(date, "Accepted");
			Output.write(orders);
			Output.write("Orders in preparation:");
			orders = db.get_orders(date, "In preperation");
			Output.write(orders);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	
	private void advance_order() throws SQLException {
		Output.write("Which order?");
		
		try {
			String order = Input.read();
			
			db.next_state(order, String.valueOf(this.id));
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
}

class Owner implements Profile, Menu {

	private SBD db;
	public final String PROFILE_NAME = "Owner";
	public final String GREETING = "Hello";
	public final String[] MENU_OPTIONS = {
		"View ingredients and their stock",
//		"View suppliers and make orders",
		"Show three most sold items of the week",
		"Show three most profitable item of the month",
//		"Show items by average preparation time",
		"Show clients sorted by amount spent",
		"Check order details"
	};
	private int id;

	@Override
	public String[] get_menu_options() { return this.MENU_OPTIONS; }
	@Override
	public String get_greeting() throws SQLException {
		String owner = "";

		try { owner = db.get_owner(); }
		catch (User_not_found e) { Output.write(e.getMessage()); }

		return this.GREETING + " " + owner;
	}
	@Override
	public String get_profile_name() { return this.PROFILE_NAME; }

	public Owner(SBD database) {
		this.db = database;
	}

	@Override
	public void run() throws SQLException, User_not_found {
		
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }
			if (idx == 0) { this.write_ingredients(); }
			if (idx == 1) { this.write_most_sold(); }
			if (idx == 2) { this.write_most_profitable(); }
			if (idx == 3) { this.write_clients_amount_spent(); }
			if (idx == 4) { this.check_order(); }
		}
	}
	
	private void check_order() throws SQLException {
		Output.write("Which order?");
		
		try {
			String order = Input.read();
			String details = db.get_order_details(order);
			Output.write(details);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	private void write_clients_amount_spent() throws SQLException {		
		try {
			java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
			Output.write("Clients by this trimester:\n");
			String details = db.get_clients_reveneu(date);
			Output.write(details);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}	
	}
	private void write_avg_time() throws SQLException {
		try {
			Output.write("Products average time:\n");
			String details = db.get_products_by_avg_time();
			Output.write(details);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	private void write_most_profitable() throws SQLException {
		try {
			java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
			Output.write("Most profitable products of this month:\n");
			String details = db.get_most_profitable_products_by_month(date);
			Output.write(details);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	private void write_most_sold() throws SQLException {
		try {
			java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
			Output.write("Most profitable products of this month:\n");
			String details = db.get_most_sold_products_by_week(date);
			Output.write(details);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	private void write_ingredients() throws SQLException, User_not_found {
		try {
			Output.write("Ingredients stock:\n");
			String details = db.get_available_ingredients();
			Output.write(details);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	private void ask_who() throws SQLException, User_not_found {
		Output.write("What's your email?");
		String email = Input.read();
		this.id = db.get_employee_id(email);
	}

}

class Waitress implements Profile, Menu {

	private SBD db;
	public final String PROFILE_NAME = "Waitress";
	public final String GREETING = "Hi there!";
	public final String[] MENU_OPTIONS = {
		"View orders that are ready",
		"Deliver an order"
	};
	private int id;

	@Override
	public String[] get_menu_options() { return this.MENU_OPTIONS; }
	@Override
	public String get_greeting() { return this.GREETING; }
	@Override
	public String get_profile_name() { return this.PROFILE_NAME; }

	public Waitress(SBD database) {
		this.db = database;
	}

	@Override
	public void run() throws SQLException, User_not_found {
		this.ask_who();
		
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }

			if (idx == 0) { this.write_ready_orders(); }
			if (idx == 1) { this.deliver_order(); }
		}
	}
	
	private void ask_who() throws SQLException, User_not_found {
		Output.write("What's your email?");
		String email = Input.read();
		this.id = db.get_employee_id(email);
	}

	private void write_ready_orders() throws SQLException {
		
		String orders;
		
		try {
			java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
			orders = db.get_orders(date, "Ready");
			Output.write(orders);
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}
	
	private void deliver_order() throws SQLException {
		
		Output.write("Which order?");
		
		try {
			String order = Input.read();
			db.next_state(order, String.valueOf(this.id));
		} catch (SQL_invalid_results e) {
			Output.write(e.getMessage());
		}
	}

}
