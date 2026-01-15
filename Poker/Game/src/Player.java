import Enums.PlayerRole;
import Enums.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private int stack;
	private List<Card> hand = new ArrayList<Card>();
	private PlayerRole role;
	private int currentBet = 0;
	private PlayerState state;

	Player(int stack) {
		this.stack = stack;
		this.role = PlayerRole.NONE;
		this.state = PlayerState.INGAME;
	}


	// ACTIONS
	void clearHand() {
		hand.clear();
	}

	void addCardToHand(Card card) {
		hand.add(card);
	}

	void bet(int bet) {
		if(bet > this.stack) throw  new IllegalArgumentException("You can't bet more than "+this.stack+" cards");
		this.currentBet += bet;
		this.stack -= bet;
	}

	void clearBet(){
		this.currentBet = 0;
	}

	void earn(int amount){
		this.stack += amount;
	}

	void fold(){
		this.state = PlayerState.FOLD;
	}


	// GETTERS
	int getStack() {
		return stack;
	}
	int getCurrentBet(){
		return currentBet;
	}
	List<Card> getHand() {
		return hand;
	}
	PlayerRole getRole() {
		return role;
	}
	PlayerState getState() {
		return state;
	}

	// SETTERS
	void setRole(PlayerRole role) {
		this.role = role;
	}
	void setState(PlayerState state) {
		this.state = state;
	}

}
