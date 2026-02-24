package TrucoAPI.Models;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private final int id;
	private final List<Card> cards = new ArrayList<>();

	public Player(int id) {
		this.id = id;
	}

	// STATIC

	// PUBLIC
	public int getId() {
		return id;
	}

	public void reset(){
		cards.clear();
	}

	public void addCard(Card card){
		cards.add(card);
	}

	public List<Card> getCards(){
		return new ArrayList<>(cards);
	}

	public Card removeCard(int idx){
		return cards.remove(idx);
	}

}
