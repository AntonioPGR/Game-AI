package PokerAPI.Model;

import PokerAPI.Enums.RankEnum;
import PokerAPI.Enums.SuitEnum;
import lombok.Data;
import lombok.Getter;

@Getter
public class Card {

	private final RankEnum rank;
	private final SuitEnum suit;

	Card(RankEnum rank, SuitEnum suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public String getLabel(){
		return rank.getLabel() + " of " + suit.getLabel();
	}

	public String getShortLabel(){
		return rank.getShortLabel() + suit.getShortLabel();
	}

}
