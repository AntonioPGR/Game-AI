import PokerAPI.Engine.Printer;
import PokerAPI.Enums.GameStage;
import PokerAPI.Enums.PlayerAction;
import PokerAPI.Model.Action;
import PokerAPI.PokerManager;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		PokerManager game = new PokerManager(5);
		game.startGame();

		Scanner scanner = new Scanner(System.in);
		String error_message = "";
		while (true) {
			if(game.isHandEnd()) {
				game.printEndHandTable();
			} else if(game.isGameEnd()){
				break;
				// PRINT END GAME TABLE
			} else {
				game.printTable();

				if (!error_message.isEmpty()) {
					Printer.printError(error_message);
					System.out.println();
					error_message = "";
				}

				Printer.printPlayerActions(game.getPlayerActions());
				String input = scanner.nextLine().trim().toLowerCase();

				if (input.equals("exit")) {
					System.out.println("Exiting game.");
					break;
				}

				// SPLIT FOR RAISE
				String[] parts = input.split("\\s+");
				String actionToken = parts[0];

				// PARSE ENUM
				PlayerAction chosenAction;
				try {
					chosenAction = PlayerAction.valueOf(actionToken.toUpperCase());
				} catch (IllegalArgumentException e) {
					error_message = "INVALID INPUT!";
					continue;
				}

				// CHECK INPUT AVAILABLE
				if (!game.getPlayerActions().contains(chosenAction)) {
					error_message = "ACTION NOT AVAILABLE!";
					continue;
				}

				// EXECUTE
				try {
					switch (chosenAction) {
						case CHECK -> game.takeAction(Action.check());
						case CALL -> game.takeAction(Action.call());
						case FOLD -> game.takeAction(Action.fold());
						case ALL_IN -> game.takeAction(Action.allIn());
						case RAISE -> {
							if (parts.length != 2) {
								error_message = "RAISE USAGE: raise <amount>";
								continue;
							}
							int amount;
							try {
								amount = Integer.parseInt(parts[1]);
							} catch (NumberFormatException e) {
								error_message = "RAISE AMOUNT MUST BE A NUMBER";
								continue;
							}
							game.takeAction(Action.raise(amount));
						}
					}
				} catch (Exception e) {
					error_message = e.getMessage();
				}
			}
		}
		scanner.close();
	}
}
