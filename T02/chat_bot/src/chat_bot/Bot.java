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

	public void run() throws SQLException;
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
		"Manage orders"
	};

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
	public void run() {
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }

		}
	}
}

class Owner implements Profile, Menu {

	private SBD db;
	public final String PROFILE_NAME = "Owner";
	public final String GREETING = "Hello";
	public final String[] MENU_OPTIONS = {
		"View ingredients and their stock",
		"View suppliers and make orders",
		"Show three most sold items of the week",
		"Show three most profitable item of the week",
		"Show items by average preparation time",
		"Show clients sorted by amount spent",
		"Check item details"
	};

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
	public void run() {
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }

		}
	}
}

class Waitress implements Profile, Menu {

	private SBD db;
	private int[] orders = new int[0];
	public final String PROFILE_NAME = "Waitress";
	public final String GREETING = "Hi there!";
	public final String[] MENU_OPTIONS = {
		"View orders that are ready",
		"Deliver an order"
	};

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
	public void run() throws SQLException {
		this.load_orders();
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.MENU_OPTIONS));

			idx = Input.get_user_option(MENU_OPTIONS);

			if (idx == -2) { return; }

			if (idx == 0) { this.write_ready_orders(); }
			if (idx == 1) { this.deliver_order(); }
		}
	}
	
	private void load_orders() throws SQLException {
			
		ResultSet rs = db.get_ready_orders();
		rs.last();
		this.orders = new int[rs.getRow() - 1];
		rs.beforeFirst();
		
		for (int i = 0; i < this.orders.length && rs.next(); i++) {
			this.orders[i] = rs.getInt("client_order_id");
		}
		
		rs.close();
	}

	private void write_ready_orders() throws SQLException {
		
		this.load_orders();
		
		if (this.orders.length > 0) {
			Output.write(String.format("%-12s", "Orders ready"));
			for (int i = 0; i < this.orders.length; i++) {
				Output.write(String.format("%-12s", this.orders[i]));
			}	
		}
		else {
			Output.write("There are no orders ready.");
		}
		
		Output.write("\n");
	}
	
	private void deliver_order() {
		
		Output.write("Which order?");
		int order = Input.get_user_option(this.orders);
		
		System.out.println(this.orders[order]);
	}

}
