package TrucoAPI.Models;

import TrucoAPI.Enums.RankEnum;
import TrucoAPI.Enums.SuitEnum;

public record Card(RankEnum rank, SuitEnum suit) {

	public String getLabel() {
		return rank.getLabel() + " of " + suit.getLabel();
	}

	public String getShortLabel() {
		return rank.getShortLabel() + suit.getShortLabel();
	}

}
