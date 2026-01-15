import Enums.RankEnum;
import Enums.SuitEnum;

public class Card {

	public final RankEnum rank;
	public final SuitEnum suit;

	Card(RankEnum rank, SuitEnum suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public int getPower(){
		return rank.getPower() * 10 + suit.getPower();
	}

	public String getLabel(){
		return rank.getLabel() + " of " + suit.getLabel();
	}

	public String getShortLabel(){
		return rank.getShortLabel() + suit.getShortLabel();
	}

}
