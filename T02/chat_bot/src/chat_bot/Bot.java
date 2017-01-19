package chat_bot;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Bot {

	Profile user;
	Profile[] profiles = { new Client(), new Chef(), new Owner(), new Waitress() };

	public void Bot() {}

	public void run() {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input;

		do {
			System.out.printf("%s\n", IO.fetch_one(Outputs.welcome));
			String[] profiles_names = new String[profiles.length];
			// print options
			// build profiles names for further use
			for (int i = 0; i < profiles.length; i++) {
				profiles_names[i] = profiles[i].get_descriptions(0);
				System.out.printf("%d. %s\n", i + 1, profiles_names[i]);
			}
			// read user input
			System.out.printf("\n> ");
			input = scanner.nextLine();
			// check if the user wants to logout
			if (IO.find_option(Inputs.logout, input) > -1) {
				return;
			}
			// check what option the user has chosen
			int idx = -1;

			try {
				// is it a number?
				idx = IO.find_option(profiles_names, Integer.parseInt(input));

			} catch(NumberFormatException nfe) {
				// maybe its some sort of profile description
				for (int i = 0; i < profiles.length; i++) {
					String[] descriptions = profiles[i].get_descriptions();

					if (IO.find_option(descriptions, input) > -1) {
						idx = i;
						break;
					}
				}
			}
			// was the option found?
			if (idx < 0) {
				continue;
			}
			// get the chosen profile
			this.user = profiles[idx];
			System.out.printf("%s %s\n", this.user.get_greeting(), IO.fetch_one(Outputs.welcome_profile));
			// run the profile
			this.user.run();
			// the user has logged out by now, clear the screen and loop
			this.clear_screen();

		} while (IO.find_option(Inputs.logout, input) == -1);
	}

	private void clear_screen() {

		System.out.print("\033[H\033[2J");
	}
}

interface IO {

	public static String fetch_one(String[] options) {
		return options[ThreadLocalRandom.current().nextInt(0, options.length)];
	}

	public static int find_option(String[] inputs, String input) {

		for (int i = 0; i < inputs.length; i++) {
			if (input.contains(inputs[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int find_option(String[] inputs, int option) {

		for (int i = 0; i < inputs.length; i++) {
			if (option == i + 1) {
				return i;
			}
		}

		return -1;
	}
}

class Inputs implements IO {

	public final static String[] logout = {
		"Bye",
		"Logout",
		"bye",
		"logout",
		"Exit",
		"exit"
	};
}

class Outputs implements IO {

	public final static String[] welcome = {
		"Welcome! I'm the restaurant assistant. Who are you?\n\n",
		"I am known by many names, but you may call me... Tim.\nSo, who are you?\n",
		"Great, the digital pimp at work.\n",
		"I am the one!\n",
		"Whoa! Who are you?\n",
		"If real is what you can feel, smell, taste and see, then 'real' is simply electrical signals interpreted by your brain (Morpheus)\n",
		"My name...... Is Neo! And if you are not Agent Smith, then who are you?\n"
	};

	public final static String[] welcome_profile = {
		"What can I do for you?\n",
		"How many I help you?\n",
		"At votre service! (french)\n"
	};
}

interface Profile {

	public void run();
	public String[] get_descriptions();
	public String get_greeting();
	public String get_descriptions(int i);
	public String[] get_options();
}

class Client implements Profile {

	final String[] descriptions = { "Client", "client" };
	final String greeting = "Welcome!";

	public Client() {}

	@Override
	public String[] get_descriptions() { return this.descriptions; }
	@Override
	public String get_descriptions(int i) { return this.descriptions[i]; }
	@Override
	public String get_greeting() { return this.greeting; }

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);


	}

	@Override
	public String[] get_options() {
		// TODO Auto-generated method stub
		return null;
	}
}

class Chef implements Profile {

	final String[] descriptions = { "Chef", "chef" };
	final String greeting = "Bonjour Chef!";

	public Chef() {}

	@Override
	public String[] get_descriptions() { return this.descriptions; }
	@Override
	public String get_descriptions(int i) { return this.descriptions[i]; }
	@Override
	public String get_greeting() { return this.greeting; }

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);

	}

	@Override
	public String[] get_options() {
		// TODO Auto-generated method stub
		return null;
	}
}

class Owner implements Profile {

	final String[] descriptions = { "Owner", "owner" };
	final String greeting = "Hi boss!";

	public Owner() {}

	@Override
	public String[] get_descriptions() { return this.descriptions; }
	@Override
	public String get_descriptions(int i) { return this.descriptions[i]; }
	@Override
	public String get_greeting() { return this.greeting; }

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);

	}

	@Override
	public String[] get_options() {
		// TODO Auto-generated method stub
		return null;
	}
}

class Waitress implements Profile {

	final String[] descriptions = { "Waitress", "waitress" };
	final String greeting = "Hi there!";

	public Waitress() {}

	@Override
	public String[] get_descriptions() { return this.descriptions; }
	@Override
	public String get_descriptions(int i) { return this.descriptions[i]; }
	@Override
	public String get_greeting() { return this.greeting; }

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);

	}

	@Override
	public String[] get_options() {
		// TODO Auto-generated method stub
		return null;
	}


}
