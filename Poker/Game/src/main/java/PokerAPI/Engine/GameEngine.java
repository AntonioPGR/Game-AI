package PokerAPI.Engine;

import PokerAPI.Enums.GameStage;
import PokerAPI.Enums.PlayerRole;
import PokerAPI.Enums.PlayerState;
import PokerAPI.Model.Player;
import PokerAPI.config.PokerConfig;

public class GameEngine {

	void advanceTurn() { // Consider all finding except one
		if (lastAggressorIdx == -1 && gameStage == GameStage.PREFLOP && getCurrentPlayer().getRole() == PlayerRole.BIG_BLIND && allBetsMatched()) {
			advanceStreet();
			return;
		}
		do setCurrentPlayerIdx((currentPlayerIdx + 1) % currentPlayers.size());
		while (getCurrentPlayer().getState() != PlayerState.INGAME && currentPlayerIdx != lastAggressorIdx);
		if (
				(lastAggressorIdx != -1 && currentPlayerIdx == lastAggressorIdx && allBetsMatched()) ||
						(lastAggressorIdx == -1 && currentPlayerIdx == 0 && gameStage != GameStage.PREFLOP && allBetsMatched()
						)) {
			advanceStreet();
		}
	}

	void advanceStreet() {
		setCurrentBet(0);
		setMinRaise(PokerConfig.SMALL_BET);
		setLastAggressorIdx(-1);
		setCurrentPlayerIdx(smallBlindIdx);
		while(getCurrentPlayer().getState() != PlayerState.INGAME) setCurrentPlayerIdx((currentPlayerIdx + 1) % currentPlayers.size());
		for (Player p : currentPlayers) p.clearBet();
		advanceGameStage();
		switch (gameStage) {
			case FLOP -> dealFlop();
			case TURN -> dealTurn();
			case RIVER -> dealRiver();
			case SHOWDOWN -> resolvePot();
		}
		advanceGameStage();
	}

	private void takeMandatoryBets(){
		getCurrentPlayer().bet(PokerConfig.SMALL_BET);
		pot.add(PokerConfig.SMALL_BET);
		advanceTurn();
		getCurrentPlayer().bet(PokerConfig.BIG_BET);
		pot.add(PokerConfig.BIG_BET);
		setCurrentBet(PokerConfig.BIG_BET);
		advanceTurn();
	}

}
