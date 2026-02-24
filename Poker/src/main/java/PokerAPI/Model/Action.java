package PokerAPI.Model;

import PokerAPI.Enums.PlayerAction;

public record Action(PlayerAction type, Integer amount) {

	public Action {
		if (type == PlayerAction.RAISE && amount == null) throw new IllegalArgumentException("RAISE requires an amount");
		if (type != PlayerAction.RAISE && amount != null) throw new IllegalArgumentException(type + " cannot have an amount");
		if (amount != null && amount <= 0) throw new IllegalArgumentException("Amount must be positive");
	}

	public static Action fold() {
		return new Action(PlayerAction.FOLD, null);
	}

	public static Action check() {
		return new Action(PlayerAction.CHECK, null);
	}

	public static Action call() {
		return new Action(PlayerAction.CALL, null);
	}

	public static Action allIn() {
		return new Action(PlayerAction.ALL_IN, null);
	}

	public static Action raise(int amount) {
		return new Action(PlayerAction.RAISE, amount);
	}
}

