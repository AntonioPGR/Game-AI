package TrucoAPI.Enums;


public enum SuitEnum {
	DIAMONDS(0, "Diamonds", "♦"),
	SPADES(1, "Spades", "♠"),
	HEARTS(2, "Hearts", "♥"),
	CLUBS(3, "Clubs", "♣");

	private final int power;
	private final String label;
	private final String shortLabel;

	SuitEnum(int power, String label, String shortLabel) {
		this.power = power;
		this.label = label;
		this.shortLabel = shortLabel;
	}

	public String getLabel() {
		return label;
	}

	public String getShortLabel() {
		return shortLabel;
	}

}
