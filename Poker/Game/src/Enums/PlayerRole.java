package Enums;

public enum PlayerRole {
	BIGBLIND("Big Blind"),
	SMALLBLIND("Small Blind"),
	NONE("None");

	private final String label;

	PlayerRole(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
