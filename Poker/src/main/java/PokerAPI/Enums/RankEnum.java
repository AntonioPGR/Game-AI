package PokerAPI.Enums;

import lombok.Getter;

@Getter
public enum RankEnum {
	TWO(2, "Two", "2"),
	THREE(3, "Three", "3"),
	FOUR(4, "Four", "4"),
	FIVE(5, "Five", "5"),
	SIX(6, "Six", "6"),
	SEVEN(7, "Seven", "7"),
	EIGHT(8, "Eight", "8"),
	NINE(9, "Nine", "9"),
	TEN(10, "Ten", "10"),
	JACK(11, "Jack", "J"),
	QUEEN(12, "Queen", "Q"),
	KING(13, "King", "K"),
	ACE(14, "Ace", "A");

	private final int power;
	private final String label;
	private final String shortLabel;

	RankEnum(int power, String label, String shortLabel) {
		this.power = power;
		this.label = label;
		this.shortLabel = shortLabel;
	}

	public static RankEnum fromValue(int value) {
		if (value < 2 || value > 14)
			throw new IllegalArgumentException("PokerAPI.Model.Card rank must be between 2 and 14");

		return switch (value) {
			case 2  -> TWO;
			case 3  -> THREE;
			case 4  -> FOUR;
			case 5  -> FIVE;
			case 6  -> SIX;
			case 7  -> SEVEN;
			case 8  -> EIGHT;
			case 9  -> NINE;
			case 10 -> TEN;
			case 11 -> JACK;
			case 12 -> QUEEN;
			case 13 -> KING;
			case 14 -> ACE;
			default -> throw new IllegalStateException("Unexpected value: " + value);
		};
	}

}
