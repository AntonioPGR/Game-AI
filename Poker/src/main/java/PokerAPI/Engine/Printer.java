package PokerAPI.Engine;

import PokerAPI.Enums.PlayerAction;
import PokerAPI.Enums.PlayerRole;
import PokerAPI.Enums.PlayerState;
import PokerAPI.Model.Card;
import PokerAPI.Model.Player;
import PokerAPI.Model.Pot;
import PokerAPI.Model.Table;

import java.util.ArrayList;
import java.util.List;

public record Printer(
	Table table,
	Pot pot,
	List<Player> allPlayers,
	List<Player> activePlayers,
	List<Player> winners,
	BetManager betManager
) {

	// ANSI COLORS
	private static final String RESET = "\u001B[0m";
	private static final String BOLD = "\u001B[1m";

	private static final String CYAN = "\u001B[36m";
	private static final String GREEN = "\u001B[32m";
	private static final String YELLOW = "\u001B[33m";
	private static final String RED = "\u001B[31m";
	private static final String GRAY = "\u001B[90m";
	private static final String BLUE = "\u001B[34m";

	// STATIC
	public static void printPlayerActions(List<PlayerAction> playerActions) {
		Printer.printSectionHeader("ACTION");
		System.out.print("Available actions: ");
		for (PlayerAction action : playerActions) {
			System.out.print(action.name().toLowerCase() + " | ");
		}
		System.out.println("exit");
		System.out.println("Enter action for current player:");
		System.out.print("> ");
	}

	// PUBLIC
	public void printTableStateInTerminal(int currentPlayerIdx) {
		printSectionHeader(" TABLE INFO");

		System.out.print(" Community cards: ");
		if (table.isEmpty()) System.out.println("(none)");
		else {
			for (Card card : table.getCards())
				System.out.print(card.getShortLabel() + " ");
			System.out.println();
		}

		System.out.println(" Pot value: " + pot.getValue());
		System.out.println(" Current bet: " + betManager.getCurrentBet());
		printLineBreak();

		if (!activePlayers.isEmpty()) {
			printSectionHeader(" IN GAME PLAYERS INFO");
			for (int idx = 0; idx < activePlayers.size(); idx++) {
				printPlayerInfo(activePlayers.get(idx), currentPlayerIdx == idx);
			}
		}

		List<Player> foldPlayers = new ArrayList<>();
		List<Player> outPlayers = new ArrayList<>();

		for (Player player : allPlayers) {
			if (player.getState() == PlayerState.FOLD) foldPlayers.add(player);
			else if (player.getState() == PlayerState.OUT) outPlayers.add(player);
		}

		if (!foldPlayers.isEmpty()) {
			printSectionHeader(" FOLD PLAYERS INFO");
			for (Player player : foldPlayers)
				printPlayerInfo(player, false);
		}

		if (!outPlayers.isEmpty()) {
			printSectionHeader(" OUT PLAYERS INFO");
			for (Player player : outPlayers)
				printPlayerInfo(player, false);
		}
	}

	public void printEndHandTable() {
		printSectionHeader(" TABLE INFO");
		System.out.print(" Community cards: ");
		for (Card card : table.getCards()) System.out.print(card.getShortLabel() + " ");
		System.out.println();
		System.out.println(" Pot value: " + pot.getValue());
		System.out.println(" Current bet: " + betManager.getCurrentBet());
		printLineBreak();
		printSectionHeader(" END STATE PLAYERS");
		for (Player player : allPlayers) printPlayerInfo(player, false);
		printSectionHeader(" WINNER INFO");
		System.out.println("Winner: ");
		for (Player player : winners) System.out.println(player.getId());
	}

	// PRIVATE
	private void printPlayerInfo(Player player, Boolean isCurrentPlayer) {
		System.out.print(" ▶ Player " + player.getId() + " ");
		if (isCurrentPlayer)
			System.out.println(GREEN + "( Current )" + RESET);
		else if (player.getState() == PlayerState.ALLIN)
			System.out.println(YELLOW + "( ALLIN )" + RESET);
		else if (player.getState() == PlayerState.FOLD)
			System.out.println(RED + "( FOLD )" + RESET);
		else if (player.getState() == PlayerState.OUT) {
			System.out.println(GRAY + "( OUT )" + RESET);
			return;
		}
		else
			System.out.println();

		if (player.getRole() != PlayerRole.NONE)
			System.out.println("   ├─ " + BLUE + "ROLE" + RESET + " : " + player.getRole().getLabel());

		System.out.print("   ├─ " + BLUE + "HAND" + RESET + " : ");
		if (player.getHoleCards().isEmpty()) System.out.println("No cards");
		else {
			for (Card card : player.getHoleCards())
				System.out.print(card.getShortLabel() + " ");
			printLineBreak();
		}

		System.out.println("   ├─ " + BLUE + "BET" + RESET + "  : " + player.getCurrentBet());
		System.out.println("   └─ " + BLUE + "STACK" + RESET + ": " + player.getStack());
		printLineBreak();
	}

	// STATIC
	public static void printError(String message) {
		System.out.println(RED + BOLD + message + RESET);
	}

	public static void clearTerminal() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public static void printSectionHeader(String title) {
		printDoubleLine();
		System.out.println(CYAN + BOLD + title + RESET);
		printDoubleLine();
	}

	public static void printSimpleLine() {
		System.out.println("─────────────────────────────────");
	}

	public static void printDoubleLine() {
		System.out.println("═════════════════════════════════");
	}

	public static void printLineBreak() {
		System.out.println();
	}
}
