package PokerAPI.Enums;

public enum PlayerRole {
	BIG_BLIND("Big Blind"),
	SMALL_BLIND("Small Blind"),
	NONE("None");

	private final String label;

	PlayerRole(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
