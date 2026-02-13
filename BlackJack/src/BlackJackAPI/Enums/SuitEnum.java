package BlackJackAPI.Enums;


public enum SuitEnum {
	DIAMONDS(0, "♦"),
	SPADES(1, "♠"),
	HEARTS(2, "♥"),
	CLUBS(3, "♣");

	private final int power;
	private final String label;

	SuitEnum(int power, String shortLabel) {
		this.power = power;
		this.label = shortLabel;
	}

	public String getLabel() {
		return label;
	}

}
