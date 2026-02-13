package PokerAPI;

import PokerAPI.Engine.*;
import PokerAPI.Enums.*;
import PokerAPI.Exceptions.InvalidPlayersNumberException;
import PokerAPI.Model.*;
import PokerAPI.config.PokerConfig;
import java.util.ArrayList;
import java.util.List;


public class PokerAPI {

	private final Deck deck = new Deck();
	private final Table table = new Table();
	private final Pot pot = new Pot();

	private final List<Player> allPlayers = new ArrayList<>();
//	private final List<Integer> activePlayerIds = new ArrayList<>();

	private final Dealer dealer;
	private final BetManager betManager;
	private final GameManager gameManager;
	private final Printer printer;

	public PokerAPI(int playerCount) throws InvalidPlayersNumberException {
		if(playerCount < PokerConfig.MIN_PLAYERS || playerCount > PokerConfig.MAX_PLAYERS)
			throw new InvalidPlayersNumberException();

		for(int i = 0; i < playerCount; i++)
			allPlayers.add(new Player(i, PokerConfig.START_STACK));

		this.dealer = new Dealer(table, deck, activePlayerIds);
		this.betManager = new BetManager(allPlayers, pot);
		this.gameManager = new GameManager(allPlayers);
		this.printer = new Printer(table, pot, allPlayers, betManager, gameManager);
	}

	// POKER API WANTED METHODS
	public void startGame(){
		gameManager.startGame();
	}

	public void startHand(){
		gameManager.startHand();
	}

	private Player getCurrentPlayer() {
		return gameManager.getCurrentPlayer();
	}

	private List<PlayerAction> getPlayerActions(){
		return gameManager.getAvailableActions();
	}

	private void takeAction(Action action){
		gameManager.takeAction(action);
	}

	public void printTable(){
		printer.clearTerminal();
		printer.printTableStateInTerminal();
	}

}