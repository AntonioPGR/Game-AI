package PokerAPI.Engine;

import PokerAPI.Model.Card;
import PokerAPI.Model.Deck;
import PokerAPI.Model.Player;
import PokerAPI.Model.Table;
import PokerAPI.Config.PokerConfig;

import java.util.List;

public record Dealer(
	Table table,
	Deck deck,
	List<Player> activePlayers
) {

	public void dealFlop() {
		deck.burn();
		for (int i = 0; i < PokerConfig.FLOP_CARDS; i++) table.add(deck.draw());
	}

	public void dealTurn() {
		deck.burn();
		table.add(deck.draw());
	}

	public void dealRiver() {
		deck.burn();
		table.add(deck.draw());
	}

	public void dealHoleCards(int smallBlindIdx) {
		int dealIdx = smallBlindIdx;
		for (int cardRound = 0; cardRound < PokerConfig.HOLE_CARDS; cardRound++) {
			for (int i = 0; i < activePlayers.size(); i++) {
				Card card = deck.draw();
				activePlayers.get(dealIdx).addHoleCard(card);
				dealIdx = (dealIdx + 1) % activePlayers.size();
			}
		}
	}

}
