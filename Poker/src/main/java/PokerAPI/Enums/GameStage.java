package PokerAPI.Enums;

public enum GameStage {
	NOTSTARTED,
	PREFLOP,
	FLOP,
	TURN,
	RIVER,
	SHOWDOWN,
	END_HAND,
	END_GAME;

	public boolean isBetStage(){
		return switch (this) {
			case NOTSTARTED, END_HAND, SHOWDOWN -> false;
			default -> true;
		};
	}

	public GameStage next() {
		return switch (this) {
			case NOTSTARTED, END_HAND, END_GAME -> PREFLOP;
			case PREFLOP      -> FLOP;
			case FLOP         -> TURN;
			case TURN         -> RIVER;
			case RIVER        -> SHOWDOWN;
			case SHOWDOWN     -> END_HAND;
		};
	}
}
