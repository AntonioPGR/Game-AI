package TrucoAPI;

import TrucoAPI.Models.Card;
import TrucoAPI.Models.Player;
import TrucoAPI.Models.ScoreBoard;

import java.util.List;

public class Printer{

	private static final String RESET  = "\u001B[0m";
	private static final String GREEN  = "\u001B[32m";
	private static final String BLUE   = "\u001B[34m";
	private static final String YELLOW = "\u001B[33m";
	private static final String RED    = "\u001B[31m";
	private static final String BOLD   = "\u001B[1m";

	// PUBLIC
	public void clearTerminal() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public void printTable(List<Player> players, Card upcard, ScoreBoard scoreboard) {
		Player p1 = players.get(0);
		Player p2 = players.get(1);
		Player p3 = players.get(2);
		Player p4 = players.get(3);

		printLine();
		printLn(padLeft("TRUCO PAULISTA", 8));
		printLine();
		printLn(padLeft(RED + scoreboard.getTeamAScore() + RESET + " xx " + BLUE + scoreboard.getTeamBScore() + RESET, 12));
		printLine();
		printBreak();

		for(Player p : players){
			String color = BLUE;
			if(p.getId() % 2 == 0) color = RED;
			print(BOLD + color + "Jogador " + p.getId() + ": " + RESET);
			if (p.getCards().isEmpty()) print(" -- -- --");
			else {
				StringBuilder sb = new StringBuilder();
				for (Card c : p.getCards()) {
					sb.append(c.getShortLabel()).append("  ");
				}
				print(sb.toString());
			}
			printBreak();
		}

		printBreak();
		print(YELLOW + "Vira: " + RESET);
		if(upcard != null ) printLn(upcard.getShortLabel());
		else printLn("--");

		printBreak();
		printLine();
	}

	// PRIVATE
	private void print(String text) {
		System.out.print(text);
	}

	private void printLine(){
		System.out.println("=".repeat(30));
	}

	private void printBreak(){
		System.out.println();
	}

	private void printLn(String text) {
		System.out.println(text);
	}


	private String padRight(String text, int size) {
		return text + " ".repeat(Math.max(0, size));
	}

	private String padLeft(String text, int size) {
		return " ".repeat(Math.max(0, size)) + text;
	}

}