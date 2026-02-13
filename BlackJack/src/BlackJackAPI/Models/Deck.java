package BlackJackAPI.Models;

import BlackJackAPI.Enums.RankEnum;
import BlackJackAPI.Enums.SuitEnum;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Deck {

	private final List<Card> fullDeck = new ArrayList<>();
	private final Deque<Card> currentDeck = new ArrayDeque<>();

	public Deck() {
		for (SuitEnum suit : SuitEnum.values()) {
			for (RankEnum rank : RankEnum.values()) {
				fullDeck.add(new Card(rank, suit));
			}
		}
		shuffle();
	}

	public void shuffle() {
		Collections.shuffle(fullDeck);
		currentDeck.clear();
		currentDeck.addAll(fullDeck);
	}

	public Card draw() {
		if (currentDeck.isEmpty()) throw new IllegalStateException("Deck is empty");
		return currentDeck.removeFirst();
	}

}
