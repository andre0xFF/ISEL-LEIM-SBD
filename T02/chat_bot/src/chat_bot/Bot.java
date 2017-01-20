package chat_bot;

import java.util.Random;
import java.util.Scanner;

public class Bot {

	Profile user;
	Profile[] profiles = { new Client(), new Chef(), new Owner(), new Waitress() };

	public void Bot() {}

	public void run() {

		String in;
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
			Output.write(String.format("%s %s\n", this.user.get_greeting(), Output.get_welcome_profile()));

			// run the profile
			this.user.run();

			// the user has logged out by now, clear the screen and loop
			Output.clear_screen();
		}
	}
}

interface Profile {

	public void run();
	public String get_profile_name();
	public String get_greeting();
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

interface IO {

	public static String random_option(String[] options) {
		return options[new Random().nextInt(options.length)];
	}

	public static int find_option_idx(String[] options, String input) {

		for (int i = 0; i < options.length; i++) {
			if (IO.find_option(options[i], input)) {
				return i;
			}
		}

		return -1;
	}

	public static boolean find_option(String[] options, String input) {

		return (IO.find_option_idx(options, input) > -1);
	}

	public static boolean find_option(String option, String input) {

		option = option.toLowerCase();
		input = input.toLowerCase();

		return (input.contains(option) || option.contains(input));
	}
}

class Input implements IO {

	public final static String[] logout = {
		"Bye",
		"Logout",
		"Exit",
		"Adious",
		"Cya",
		"Adeus"
	};

	public static String read() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input;

		System.out.printf("\n> ");
		input = scanner.nextLine();
		System.out.printf("\n");
		return input;
	}

	public static int get_user_option(String[] options) {
		String in = Input.read();

		// check if the user wants to logout
		if (IO.find_option(Input.logout, in)) {
			return -2;
		}

		// check what option the user has chosen
		int idx = -1;

		try {
			// is it a number?
			int aux = Integer.parseInt(in.substring(0, 1));

			if (aux >= 0 && aux <= options.length) {
				idx = aux - 1;
			}

		} catch (NumberFormatException nfe) {
			// maybe its a profile description
			idx = IO.find_option_idx(options, in);
		}

		if (idx == -1) {
			Output.write(IO.random_option(Output.unknown));
			return get_user_option(options);
		}

		return idx;
	}
}

class Output implements IO {

	public final static String[] welcome = {
		"Welcome! I'm the restaurant assistant. Who are you?",
		"I am known by many names, but you may call me... Tim.\nSo, who are you?",
		"Great, the digital pimp at work.",
		"I am the one!",
		"Whoa! Who are you?",
		"If real is what you can feel, smell, taste and see, then 'real' is simply electrical signals interpreted by your brain (Morpheus)\nWhat's your input?",
		"My name...... is Neo! And if you are not Agent Smith, then who are you?"
	};

	public final static String[] welcome_profile = {
		"What can I do for you?",
		"How many I help you?",
		"At votre service! (french)"
	};

	public final static String[] unknown = {
		"I'm sorry I didn't get that. Can you repeat?",
		"Pardon?",
		"Comando desconhecido.",
		"Did you misspell something?",
		"I'm not sure what you mean.. Write a command or the command option."
	};

	public static String get_welcome() { return IO.random_option(Output.welcome); }
	public static String get_welcome_profile() { return IO.random_option(Output.welcome_profile); }

	public static void write(String s) {
		System.out.printf("%s\n", s);
	}

	public static void write(int i) {
		Output.write(String.valueOf(i));
	}

	public static void clear_screen() {

		System.out.print("\033[H\033[2J");
	}
}

interface Client_model {

	public String get_name();
	public int get_id();
}

class Client implements Profile, Menu, Client_model {

	final String profile_name = "Client";
	final String greeting = "Welcome!";
	final String[] menu_options = {
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
	public String[] get_menu_options() { return this.menu_options; }
	@Override
	public String get_greeting() { return this.greeting; }
	@Override
	public String get_profile_name() { return this.profile_name; }
	@Override
	public String get_name() { return this.name; }
	@Override
	public int get_id() { return this.id; }

	public Client() {}

	@Override
	public void run() {
		String in;
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.menu_options));

			idx = Input.get_user_option(menu_options);

			if (idx == -2) { return; }

			Output.write(idx);
		}
	}
}

class Chef implements Profile, Menu {

	final String profile_name = "Chef";
	final String greeting = "Bonjour chef!";
	final String[] menu_options = {
		"View current orders",
		"Manage orders"
	};

	@Override
	public String[] get_menu_options() { return this.menu_options; }
	@Override
	public String get_greeting() { return this.greeting; }
	@Override
	public String get_profile_name() { return this.profile_name; }


	public Chef() {}

	@Override
	public void run() {
		String in;
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.menu_options));

			idx = Input.get_user_option(menu_options);

			if (idx == -2) { return; }

		}
	}
}

class Owner implements Profile, Menu {

	final String profile_name = "Owner";
	final String greeting = "Hi boss!";
	final String[] menu_options = {
		"View ingredients and their stock",
		"View suppliers and make orders",
		"Show three most sold items of the week",
		"Show three most profitable item of the week",
		"Show items by average preparation time",
		"Show clients sorted by amount spent",
		"Check item details"
	};

	@Override
	public String[] get_menu_options() { return this.menu_options; }
	@Override
	public String get_greeting() { return this.greeting; }
	@Override
	public String get_profile_name() { return this.profile_name; }

	public Owner() {}

	@Override
	public void run() {
		String in;
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.menu_options));

			idx = Input.get_user_option(menu_options);

			if (idx == -2) { return; }

		}
	}
}

class Waitress implements Profile, Menu {

	final String profile_name = "Waitress";
	final String greeting = "Hi there!";
	final String[] menu_options = {
		"View orders that are ready"
	};

	@Override
	public String[] get_menu_options() { return this.menu_options; }

	public Waitress() {}

	@Override
	public String get_greeting() { return this.greeting; }
	@Override
	public String get_profile_name() { return this.profile_name; }

	@Override
	public void run() {
		String in;
		int idx = -1;

		while (true) {
			Output.write(Menu.print(this.menu_options));

			idx = Input.get_user_option(menu_options);

			if (idx == -2) { return; }

		}
	}

}
