package PokerAPI.Engine;

import PokerAPI.Model.Card;
import PokerAPI.Model.Deck;
import PokerAPI.Model.Player;
import PokerAPI.Model.Table;
import PokerAPI.config.PokerConfig;

import java.util.List;

public class Dealer {

	private final Table table;
	private final Deck deck;
	private final List<Player> currentPlayers;

	public Dealer(Table table, Deck deck, List<Player> currentPlayers){
		this.table = table;
		this.deck = deck;
		this.currentPlayers = currentPlayers;
	}

	public void dealFlop(){
		deck.burn();
		for(int i = 0; i < PokerConfig.FLOP_CARDS; i++) table.add(deck.draw());
	}

	public void dealTurn(){
		deck.burn();
		table.add(deck.draw());
	}

	public void dealRiver(){
		deck.burn();
		table.add(deck.draw());
	}

	public void dealRoles(int smallBlindIdx){
		for (Player player : currentPlayers) player.clearRole();
		smallBlindIdx = (smallBlindIdx) % currentPlayers.size();
		// setCurrentPlayerIdx(smallBlindIdx);
		currentPlayers.get(smallBlindIdx).setAsSmallBlind();
		currentPlayers.get((smallBlindIdx + 1) % currentPlayers.size()).setAsBigBlind();
	}

	public void dealHoleCards(int smallBlindIdx){
		int dealIdx = smallBlindIdx;
		for (int cardRound = 0; cardRound < PokerConfig.HOLE_CARDS; cardRound++) {
			for (int i = 0; i < currentPlayers.size(); i++) {
				Card card = deck.draw();
				currentPlayers.get(dealIdx).addHoleCard(card);
				dealIdx = (dealIdx + 1) % currentPlayers.size();
			}
		}
	}

}
