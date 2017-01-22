package chat_bot;

import java.sql.SQLException;

public class main {

	public final static int RESTAURANT = 1;
	public static void main(String[] args) {

//		MySQL.test_stored_procedure();
		SBD db;
		Bot bot;
		
		// TODO insert ready orders for current day

		try {
			db = new SBD(RESTAURANT);
			bot = new Bot(
					new Profile[] {
							new Client(db),
							new Chef(db),
							new Owner(db),
							new Waitress(db)
					}
			);
			bot.run();
			db.close();
			Output.clear_screen();
		} catch (SQLException e) {
			System.out.println("Communications link failure.");
		}
	}

}
