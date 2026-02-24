package PokerAPI.Exceptions;

import PokerAPI.Config.PokerConfig;

public class InvalidPlayersNumberException extends Exception{

	public InvalidPlayersNumberException(){
		super(
			"INVALID PLAYERS NUMBER - Our poker game should have at least %d and we also don't accept more than %d allPlayers!"
				.formatted(PokerConfig.MIN_PLAYERS, PokerConfig.MAX_PLAYERS)
		);
	}

}
