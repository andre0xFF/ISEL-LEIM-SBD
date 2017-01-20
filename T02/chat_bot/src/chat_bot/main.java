package chat_bot;

import java.sql.SQLException;

import database.SBD;

public class main {

	public final static int RESTAURANT = 9;
	public static void main(String[] args) {

//		MySQL.test_stored_procedure();
		SBD db;
		Bot bot;

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
		} catch (SQLException e) {
			System.out.println("Error connecting to database!");
		}
	}

}
