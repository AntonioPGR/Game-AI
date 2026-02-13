package BlackJackAPI.Models;

import BlackJackAPI.Enums.RankEnum;
import BlackJackAPI.Enums.SuitEnum;

public record Card(RankEnum rank, SuitEnum suit) {

	public String getLabel() {
		return rank.getLabel() + suit.getLabel();
	}

}
