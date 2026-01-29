package PokerAPI.Enums;

public enum GameStage {
	NOTSTARTED,
	PREFLOP,
	FLOP,
	TURN,
	RIVER,
	SHOWDOWN,
	ENDED;

	public boolean isBetStage(){
		return switch (this) {
			case NOTSTARTED, ENDED, SHOWDOWN -> false;
			default -> true;
		};
	}

	public GameStage next() {
		return switch (this) {
			case NOTSTARTED, ENDED -> PREFLOP;
			case PREFLOP      -> FLOP;
			case FLOP         -> TURN;
			case TURN         -> RIVER;
			case RIVER        -> SHOWDOWN;
			case SHOWDOWN     -> ENDED;
		};
	}
}
