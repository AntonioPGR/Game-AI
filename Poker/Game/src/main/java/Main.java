import PokerAPI.Enums.PlayerAction;
import PokerAPI.PokerAPI;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		PokerAPI game = new PokerAPI(5);
		game.startGame();
		game.printTableState();

		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println();
			System.out.println("Enter action for current player:");
			System.out.println("check | call | raise <amount> | fold | allin | exit");
			System.out.print("> ");

			String input = scanner.nextLine().trim().toLowerCase();

			if (input.equals("exit")) {
				System.out.println("Exiting game.");
				break;
			}

			try {
				if (input.equals("check")) {
					game.takeAction(PlayerAction.CHECK);
				}
				else if (input.equals("call")) {
					game.takeAction(PlayerAction.CALL);
				}
				else if (input.startsWith("raise")) {
					String[] parts = input.split("\\s+");
					if (parts.length != 2) {
						System.out.println("Usage: raise <amount>");
						continue;
					}
					int amount = Integer.parseInt(parts[1]);
					game.takeAction(PlayerAction.RAISE, amount);
				}
				else if (input.equals("fold")) {
					game.takeAction(PlayerAction.FOLD);
				}
				else if (input.equals("allin")) {
					game.takeAction(PlayerAction.ALL_IN);
				}
				else {
					System.out.println("Unknown command.");
					continue;
				}

				game.printTableState();

			} catch (Exception e) {
				System.out.println("❌ Error: " + e.getMessage());
			}
		}

		scanner.close();
	}
}
