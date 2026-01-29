package PokerAPI.Exceptions;

import PokerAPI.config.PokerConfig;

public class InvalidPlayersNumberException extends Exception{

	public InvalidPlayersNumberException(){
		super(
			"INVALID PLAYERS NUMBER - Our poker game should have at least %d and we also don't accept more than %d players!"
				.formatted(PokerConfig.MIN_PLAYERS, PokerConfig.MAX_PLAYERS)
		);
	}

}
