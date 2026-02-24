package PokerAPI;

import PokerAPI.Engine.*;
import PokerAPI.Enums.*;
import PokerAPI.Model.*;
import PokerAPI.Config.PokerConfig;
import lombok.AccessLevel;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Setter(AccessLevel.PRIVATE)
public class PokerManager {

	private final Deck deck = new Deck();
	private final Table table = new Table();
	private final Pot pot = new Pot();

	private final Dealer dealer;
	private final BetManager betManager;
	private final Printer printer;

	private final int playerCount;
	private final List<Player> allPlayers = new ArrayList<>();
	private final List<Player> activePlayers = new ArrayList<>();
	private List<Player> winners = new ArrayList<>();
	private int currentPlayerIdx = 0;

	private GameStage gameStage = GameStage.NOTSTARTED;

	public PokerManager(int playerCount) {
		if(playerCount < PokerConfig.MIN_PLAYERS || playerCount > PokerConfig.MAX_PLAYERS)
			throw new IllegalArgumentException("The player count must be between %d and %d".formatted(PokerConfig.MIN_PLAYERS, PokerConfig.MAX_PLAYERS));
		this.playerCount = playerCount;

		this.betManager = new BetManager(allPlayers, activePlayers, pot, table);
		this.dealer = new Dealer(table, deck, activePlayers);
		this.printer = new Printer(table, pot, allPlayers, activePlayers, winners, betManager);
	}

	// PUBLIC METHODS
	public void startGame(){
		if (gameStage != GameStage.NOTSTARTED)
			throw new IllegalStateException("GAME STAGE ERROR - You already has a game running. End it to start other!");
		this.restoreGameAttributes();
		this.startHand();
	}

	void startHand() {
		if (gameStage != GameStage.NOTSTARTED && gameStage != GameStage.END_HAND)
			throw new IllegalStateException("GAME STAGE ERROR - You already has a game running. End it to start other!");
		this.restoreHandAttributes();
		betManager.postSmallBlind();
		setCurrentPlayerIdx(betManager.getSmallBlindIdx());
		betManager.dealRoles();
		dealer.dealHoleCards(betManager.getSmallBlindIdx());
		betManager.takeSmallBlind(currentPlayerIdx);
		advanceTurn();
		betManager.takeBigBlind(currentPlayerIdx);
		advanceTurn();
	}

	public Player getCurrentPlayer() {
		Player p = activePlayers.get(currentPlayerIdx);
		if(p == null) throw new IllegalStateException("Player not found!");
		return p;
	}

	public List<PlayerAction> getPlayerActions(){
		List<PlayerAction> playerActions = new ArrayList<>();
		Player currentPlayer = getCurrentPlayer();
		// NO ACTIONS CASES
		if(currentPlayer.getState() != PlayerState.INGAME)
			return playerActions;
		// FOLD
		if(
			!(betManager.getCurrentBet() == 0)
		) playerActions.add(PlayerAction.FOLD);
		// CHECK
		if(
			betManager.getCurrentBet() == 0 ||
			currentPlayer.getCurrentBet() == betManager.getCurrentBet()
		)
			playerActions.add(PlayerAction.CHECK);
		// CALL
		if(
			betManager.getCurrentBet() < currentPlayer.getStack() &&
			betManager.getCurrentBet() != currentPlayer.getCurrentBet() &&
			betManager.getCurrentBet() > 0
		) playerActions.add(PlayerAction.CALL);
		// RAISE
		if(currentPlayer.getStack() >= betManager.getMinRaise())
			playerActions.add(PlayerAction.RAISE);
		// ALL IN
		playerActions.add(PlayerAction.ALL_IN);
		return playerActions;
	}

	public void takeAction(Action action){
		if(!gameStage.isBetStage()) throw new IllegalStateException("You cannot bet in %s stage!".formatted(gameStage.toString()));
		switch (action.type()) {
			case RAISE:
				betManager.raiseBet(action.amount(), currentPlayerIdx);
				advanceTurn();
				break;
			case CHECK:
				if(betManager.getCurrentBet() != getCurrentPlayer().getCurrentBet()) throw new IllegalArgumentException("Cannot check — bet not matched");
				advanceTurn();
				break;
			case CALL:
				betManager.callBet(currentPlayerIdx);
				advanceTurn();
				break;
			case FOLD:
				getCurrentPlayer().fold();
				activePlayers.remove(currentPlayerIdx);
				retreatPlayer();
				advanceTurn();
				break;
			case ALL_IN:
				betManager.allInBet(currentPlayerIdx);
				advanceTurn();
				break;
		}
	}

	public void printTable(){
		Printer.clearTerminal();
		printer.printTableStateInTerminal(currentPlayerIdx);
	}

	public void printEndHandTable(){
		Printer.clearTerminal();
		printer.printEndHandTable();
	}

	public boolean isHandEnd(){
		return gameStage == GameStage.END_HAND;
	}

	public boolean isGameEnd(){
		boolean isEnd = false;
		boolean isFirst = true;
		for(Player p : allPlayers){
			if(p.getStack() <= PokerConfig.BIG_BET){
				if(isFirst) isFirst = false;
				else {
					isEnd = true;
					break;
				}
			}
		}
		return isEnd;
	}


	// PRIVATE METHODS
	private void restoreGameAttributes(){
		allPlayers.clear();
		activePlayers.clear();
		for(int i= 0; i < playerCount; i++) {
			allPlayers.add(new Player(i+1, PokerConfig.START_STACK));
			allPlayers.get(i).enterGame();
			activePlayers.add(allPlayers.get(i));
		}
		setGameStage(GameStage.NOTSTARTED);
		betManager.restoreHandAttributes();
		setCurrentPlayerIdx(0);
		table.clear();
		pot.clear();
	}

	private void restoreHandAttributes(){
		setGameStage(GameStage.PREFLOP);
		betManager.restoreHandAttributes();
		activePlayers.clear();
		allPlayers.forEach(player -> {
			if(player.getState() != PlayerState.OUT){
				player.resetForNewHand();
				activePlayers.add(player);
			}
		});
		pot.clear();
		table.clear();
		deck.shuffle();
		setWinners(new ArrayList<>());
	}

	private void advanceTurn() {
		if (
			betManager.getLastAggressorIdx() == -1 &&
			gameStage == GameStage.PREFLOP &&
			getCurrentPlayer().getRole() == PlayerRole.BIG_BLIND &&
			betManager.allBetsMatched()
		) {
			advanceStreet();
			return;
		}
		do advancePlayer();
		while (getCurrentPlayer().getState() != PlayerState.INGAME && currentPlayerIdx != betManager.getLastAggressorIdx());
		if (
			(betManager.getLastAggressorIdx() != -1 && currentPlayerIdx == betManager.getLastAggressorIdx() && betManager.allBetsMatched()) ||
			(betManager.getLastAggressorIdx() == -1 && currentPlayerIdx == 0 && gameStage != GameStage.PREFLOP && betManager.allBetsMatched())
		) {
			advanceStreet();
//			return;
		}
	}

	private void advanceStreet() {
		betManager.restoreStreetAttributes();
		for (Player p : activePlayers) p.clearBet();
		setCurrentPlayerIdx(betManager.getSmallBlindIdx());
		while(getCurrentPlayer().getState() != PlayerState.INGAME) advancePlayer();
		setGameStage(gameStage.next());
		switch (gameStage) {
			case FLOP -> dealer.dealFlop();
			case TURN -> dealer.dealTurn();
			case RIVER -> dealer.dealRiver();
			case SHOWDOWN -> findWinner();
		}
	}

	private void findWinner() {
		List<Integer> highestIds = new ArrayList<>();
		HandResolver highestHand = null;
		for(Player player : activePlayers){
			List<Card> playerCards = new ArrayList<>();
			playerCards.addAll(player.getHoleCards());
			playerCards.addAll(table.getCards());
			HandResolver playerHand = new HandResolver(playerCards);
			if(highestHand == null || playerHand.getValue() > highestHand.getValue()){
				highestIds.clear();
				highestIds.add(player.getId());
				highestHand = playerHand;
			} else if(highestHand.getValue() == playerHand.getValue()){
				boolean bothEqual = true;
				boolean keepHighest = true;
				if(highestHand.kickers.size() != playerHand.kickers.size()) throw new IllegalStateException("Kickers are not equal");
				for(int i = 0; i < highestHand.kickers.size(); i++){
					if(highestHand.kickers.get(i).getPower() > playerHand.kickers.get(i).getPower()){
						bothEqual = false;
						break;
					} else if(highestHand.kickers.get(i).getPower() < playerHand.kickers.get(i).getPower()){
						bothEqual = false;
						keepHighest = false;
						break;
					}
				}
				if(bothEqual){
					highestIds.add(player.getId());
				} else if(!keepHighest){
					highestIds.clear();
					highestIds.add(player.getId());
					highestHand = playerHand;
				}
			}
		}
		List<Player> winners = new ArrayList<>();
		int valuePerWinner = betManager.resolvePot(highestIds.size());
		for(Player player : activePlayers){
			if(highestIds.contains(player.getId())) {
				winners.add(player);
				player.earn(valuePerWinner);
			}
		}
		setWinners(winners);
		setGameStage(GameStage.END_HAND);
	}

	private void retreatPlayer(){
		currentPlayerIdx--;
		if (currentPlayerIdx < 0) currentPlayerIdx = activePlayers.size() - 1;
	}

	private void advancePlayer(){
		setCurrentPlayerIdx((currentPlayerIdx + 1) % activePlayers.size());
	}

}