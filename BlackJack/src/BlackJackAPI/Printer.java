package BlackJackAPI;

import BlackJackAPI.Enums.GameResult;
import BlackJackAPI.Enums.GameStage;
import BlackJackAPI.Models.Card;
import BlackJackAPI.Models.Player;


public class Printer{

	private static final String RESET  = "\u001B[0m";
	private static final String GREEN  = "\u001B[32m";
	private static final String BLUE   = "\u001B[34m";
//	private static final String YELLOW = "\u001B[33m";
	private static final String RED    = "\u001B[31m";
	private static final String BOLD   = "\u001B[1m";

	private static final int SIZE = 30;

	// PUBLIC
	public void clearTerminal() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public void printTable(Player player, Player dealer, GameStage gameStage, GameResult gameResult) {
		printLine();
		printLn(padCenter("BLACKJACK"));
		printLine();
		printBreak();

		// DEALER
		print(BOLD + RED + "Dealer's Hand: " + RESET);
		if(gameStage != GameStage.NOT_STARTED){
			for(int idx = 0; idx < dealer.getCards().size(); idx++) {
				if (gameStage == GameStage.FINISHED) {
					String cardLabel = dealer.getCards().get(idx).getLabel();
					if(cardLabel.length() == 2) cardLabel = " " + cardLabel;
					print(cardLabel + " ");
				} else {
					if (idx == 0) {
						String cardLabel = dealer.getCards().get(idx).getLabel();
						if(cardLabel.length() == 2) cardLabel = " " + cardLabel;
						print(cardLabel + " ");
					} else print("[?] ");
				}
			}
		} else {
			printLn("[?] [?] ");
		}
		printBreak();

		// PLAYER
		print(BOLD + BLUE + "Player's Hand: " + RESET);
		if(gameStage != GameStage.NOT_STARTED){
			for(Card card : player.getCards()) {
				String cardLabel = card.getLabel();
				if(cardLabel.length() == 2) cardLabel = " " + cardLabel;
				print(cardLabel + " ");
			}
			printBreak();
		} else {
			printLn("[?] [?] ");
		}
		printBreak();

		printLine();
		if(gameStage == GameStage.FINISHED){
			switch(gameResult){
				case PLAYER_WIN -> printLn(BLUE + BOLD + padCenter( "You win!" ) + RESET);
				case DEALER_WIN -> printLn(RED + BOLD + padCenter("Dealer wins!") + RESET);
				case TIE -> printLn(GREEN + BOLD + padCenter("It's a tie!") + RESET);
			}
			printLine();
		}
	}

	// STATIC
	public static void printLine(){
		System.out.println("=".repeat(SIZE));
	}

	public static void print(String text) {
		System.out.print(text);
	}

	public static void printLn(String text) {
		System.out.println(text);
	}

	public static void printBreak(){
		System.out.println();
	}

	// PRIVATE
	private String padRight(String text, int size) {
		return text + " ".repeat(Math.max(0, size));
	}

	private String padLeft(String text, int size) {
		return " ".repeat(Math.max(0, size)) + text;
	}

	private String padCenter(String text) {
		int leftSize = (SIZE - text.length()) / 2;
		return padLeft(text, leftSize);
	}

}