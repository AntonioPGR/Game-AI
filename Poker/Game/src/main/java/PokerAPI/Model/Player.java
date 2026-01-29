package PokerAPI.Model;

import PokerAPI.Enums.PlayerRole;
import PokerAPI.Enums.PlayerState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

	@Getter
	private int stack;

	private final List<Card> holeCards = new ArrayList<>();

	@Getter
	private int currentBet = 0;
	@Getter
	private PlayerState state = PlayerState.OUT;
	@Getter
	private PlayerRole role = PlayerRole.NONE;

	public Player(int stack) {
		this.stack = stack;
	}

	// ACTIONS
	public void resetForNewHand(){
		this.clearBet();
		this.clearRole();
		this.clearHoleCards();
		if(state != PlayerState.OUT) this.enterGame();
	}

	public void bet(int bet) {
		if(bet > this.stack) throw new IllegalArgumentException("You can't bet more than " + this.stack + " chips");
		this.currentBet += bet;
		this.stack -= bet;
		if(stack == 0) this.state = PlayerState.ALLIN;
	}

	public void goAllIn(){
		if(stack == 0) throw new IllegalArgumentException("Player can't go all in with 0 in stack");
		this.currentBet += this.stack;
		this.stack = 0;
		this.state = PlayerState.ALLIN;
	}

	public void clearBet(){
		this.currentBet = 0;
	}

	public void earn(int amount){
		this.stack += amount;
	}

	// STATE
	public void fold(){
		this.state = PlayerState.FOLD;
	}

	public void leaveGame(){ this.state = PlayerState.OUT; }

	public void enterGame(){ this.state = PlayerState.INGAME; }

	// ROLE
	public void setAsSmallBlind(){this.role = PlayerRole.SMALL_BLIND;}

	public void setAsBigBlind(){this.role = PlayerRole.BIG_BLIND;}

	public void clearRole(){this.role = PlayerRole.NONE;}

	// HOLE CARDS
	public void clearHoleCards() {
		holeCards.clear();
	}

	public void addHoleCard(Card card) {
		holeCards.add(card);
	}

	// GETTERS
	public List<Card> getHoleCards() {
		return Collections.unmodifiableList(holeCards);
	}

}
