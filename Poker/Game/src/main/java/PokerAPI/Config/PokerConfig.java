package PokerAPI.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PokerConfig {

	public static final int MIN_PLAYERS = 3;
	public static final int MAX_PLAYERS = 10;

	public static final int START_STACK = 500;

	public static final int HOLE_CARDS = 2;
	public static final int FLOP_CARDS = 3;

	public static final int SMALL_BET = 5;
	public static final int BIG_MULTIPLIER = 2;
	public static final int BIG_BET = SMALL_BET * BIG_MULTIPLIER;
	public static final int MIN_RAISE = SMALL_BET * BIG_MULTIPLIER;

}
