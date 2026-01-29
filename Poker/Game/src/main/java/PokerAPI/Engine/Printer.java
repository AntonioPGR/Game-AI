package PokerAPI.Engine;

import PokerAPI.Enums.PlayerRole;
import PokerAPI.Enums.PlayerState;
import PokerAPI.Model.Card;
import PokerAPI.Model.Player;
import PokerAPI.Model.Pot;
import PokerAPI.Model.Table;
import lombok.NoArgsConstructor;

import java.util.List;

public class Printer {

	private final Table table;
	private final Pot pot;
	private final List<Player> players;

	public Printer(Table table, Pot pot, List<Player> players){
		this.table = table;
		this.pot = pot;
		this.players = players;
	}

	public void printTableStateInTerminal(int playerIdx, int bet){
		System.out.println("=================================");
		System.out.println("TABLE");
		System.out.print("Community cards: ");
		if (table.isEmpty()) System.out.println("(none)");
		else {
			for (Card card : table.getCards()) System.out.print(card.getShortLabel() + " ");
			System.out.println();
		}
		System.out.println("Pot value: " + pot);
		System.out.println("Current bet: " + bet);
		System.out.println("---------------------------------");
		System.out.println("PLAYERS");
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			System.out.print("PokerAPI.Model.Player " + (i + 1));
			if (player.getRole() != PlayerRole.NONE) System.out.print(" [" + player.getRole().getLabel() + "]");
			System.out.print(" -> PokerAPI.Engine.Hand: ");
			if (player.getHoleCards().isEmpty()) System.out.println("(no cards)");
			else {
				for (Card card : player.getHoleCards()) System.out.print(card.getShortLabel() + " ");
			}
			System.out.print("- Bet: " + player.getCurrentBet() + " - Stack: " + player.getStack());
			if (playerIdx == i) System.out.print(" ( Current PokerAPI.Model.Player )");
			if(player.getState() == PlayerState.ALLIN) System.out.print(" ( ALLIN )");
			if(player.getState() == PlayerState.FOLD) System.out.print(" ( FOLD )");
			if(player.getState() == PlayerState.OUT) System.out.print(" ( OUT )");
			System.out.println();
		}
		System.out.println("=================================");
	}

	public void clearTerminal(){
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

}
