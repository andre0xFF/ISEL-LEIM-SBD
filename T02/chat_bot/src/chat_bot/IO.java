package chat_bot;

import java.util.Random;
import java.util.Scanner;

interface IO {

	/**
	 * 
	 * @param elements
	 * @return Random element from the array param
	 */
	public static String random_element(String[] elements) {
		return elements[new Random().nextInt(elements.length)];
	}

	public static int get_option_idx(String[] options, String input) {

		for (int i = 0; i < options.length; i++) {
			if (IO.string_within_string(options[i], input)) {
				return i;
			}
		}

		return -1;
	}

	public static boolean is_an_option(String[] options, String input) {

		return (IO.get_option_idx(options, input) > -1);
	}

	/**
	 * Compares if one string is contained within another or vice-versa
	 * @param option
	 * @param input
	 * @return true or false
	 */
	public static boolean string_within_string(String option, String input) {

		option = option.toLowerCase();
		input = input.toLowerCase();

		return (input.contains(option) || option.contains(input));
	}
}

/**
 * Refers to anything that comes In.
 */
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
		String input = Input.read();

		// check if the user wants to logout
		if (IO.is_an_option(Input.logout, input)) {
			return -2;
		}

		// check what option the user has chosen
		int idx = -1;

		try {
			// is it a number?
			int aux = Integer.parseInt(input);

			if (aux >= 0 && aux <= options.length) {
				idx = aux - 1;
				
			} else if (IO.is_an_option(options, input)){
				idx = IO.get_option_idx(options, input);
			}
			

		} catch (NumberFormatException nfe) {
			// maybe its a profile description
			idx = IO.get_option_idx(options, input);
		}

		if (idx == -1) {
			Output.write(IO.random_element(Output.unknown_input));
			return get_user_option(options);
		}

		return idx;
	}
	
	public static int get_user_option(int[] options) {
		String[] parsed_options = new String[options.length];
		
		for (int i = 0; i < options.length; i++) {
			parsed_options[i] = Integer.toString(options[i]);
		}
		
		return Input.get_user_option(parsed_options);
	}
}

/**
 * Refers to anything that goes Out.
 */
class Output implements IO {

	public final static String[] welcome = {
		"Welcome! I'm the restaurant assistant. Who are you?",
		"I am known by many names, but you may call me... Tim. - Graham Chapman\n\nSo, who are you?",
		"Great, the digital pimp hard at work. - Switch, in The Matrix",
		"I am the one!",
		"Whoa! Who are you?",
		"If real is what you can feel, smell, taste and see, then 'real' is simply electrical signals interpreted by your brain. - Morpheus in The Matrix\n\nWhat's your input?",
		"My name...... is Neo! And if you are not Agent Smith, then who are you?",
		"You say freak, I say unique. - Christian Baloga",
		"So how do you plan on saving the world?” — Cooper\n\nWe’re not meant to save the world, we’re meant to leave it.” — Brand in Interstellar\n\n"
	};

	public final static String[] welcome_profile = {
		"What can I do for you?",
		"How many I help you?",
		"At votre service! (french)"
	};

	public final static String[] unknown_input = {
		"I'm sorry I didn't get that. Can you repeat?",
		"Pardon?",
		"Comando desconhecido.",
		"Did you misspell something?",
		"I'm not sure what you mean.. Write a command or the command option."
	};

	public final static String[] stranger = {
		"There is something fishy..",
		"Do I know you?",
		"Wait.. who?",
		"I don't recognize your bytecode."
	};

	public static String get_welcome() { return IO.random_element(Output.welcome); }
	public static String get_welcome_profile() { return IO.random_element(Output.welcome_profile); }

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
