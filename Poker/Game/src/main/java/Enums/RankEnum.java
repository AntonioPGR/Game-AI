package Enums;

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

	public int getPower() {
		return power;
	}

	public String getLabel() {
		return label;
	}

	public String getShortLabel() {
		return shortLabel;
	}
}
