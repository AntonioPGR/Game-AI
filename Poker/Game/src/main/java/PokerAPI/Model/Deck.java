package PokerAPI.Model;

import PokerAPI.Enums.RankEnum;
import PokerAPI.Enums.SuitEnum;

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
		reset();
	}

	public void shuffle() {
		Collections.shuffle(fullDeck);
		reset();
	}

	public Card draw() {
		if (currentDeck.isEmpty()) throw new IllegalStateException("Deck is empty");
		return currentDeck.removeFirst();
	}

	public void burn(){
		this.draw();
	}

	private void reset() {
		currentDeck.clear();
		currentDeck.addAll(fullDeck);
	}
}
