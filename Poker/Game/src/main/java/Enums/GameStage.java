package Enums;

public enum GameStage {
	NOTSTARTED,
	PREFLOP,
	PREFLOP_BETS,
	FLOP,
	FLOP_BETS,
	TURN,
	TURN_BETS,
	RIVER,
	RIVER_BETS,
	SHOWDOWN,
	ENDED;

	public boolean isBetStage(){
		return switch (this){
			case PREFLOP_BETS, FLOP_BETS, TURN_BETS, RIVER_BETS -> true;
			default -> false;
		};
	}

	public GameStage next() {
		return switch (this) {
			case NOTSTARTED, ENDED -> PREFLOP;
			case PREFLOP      -> PREFLOP_BETS;
			case PREFLOP_BETS -> FLOP;
			case FLOP         -> FLOP_BETS;
			case FLOP_BETS    -> TURN;
			case TURN         -> TURN_BETS;
			case TURN_BETS    -> RIVER;
			case RIVER        -> RIVER_BETS;
			case RIVER_BETS   -> SHOWDOWN;
			case SHOWDOWN     -> ENDED;
		};
	}
}
