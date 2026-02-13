package BlackJackAPI.Models;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private final List<Card> cards = new ArrayList<>();

	public Player() {}

	// PUBLIC
	public void reset(){
		cards.clear();
	}

	public void addCard(Card card){
		cards.add(card);
	}

	public List<Card> getCards(){
		return new ArrayList<>(cards);
	}

}
