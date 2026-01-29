package PokerAPI;

import PokerAPI.Engine.Dealer;
import PokerAPI.Engine.Printer;
import PokerAPI.Enums.*;
import PokerAPI.Exceptions.InvalidPlayersNumberException;
import PokerAPI.Model.*;
import PokerAPI.Engine.HandResolver;
import PokerAPI.config.PokerConfig;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;


public class PokerAPI {

	private final Deck deck = new Deck();
	private final Table table = new Table();
	private final Pot pot = new Pot();

	private final List<Player> allPlayers = new ArrayList<>();
	private List<Player> currentPlayers = new ArrayList<>();

	private final Printer display = new Printer(table, pot, allPlayers);
	private final Dealer dealer = new Dealer(table, deck, currentPlayers);

	@Setter
	private GameStage gameStage = GameStage.NOTSTARTED;
	@Setter
	private int currentPlayerIdx = -1;
	@Setter
	private int smallBlindIdx = -1;
	@Setter
	private int currentBet = 0;
	@Setter
	private int lastAggressorIdx = -1;
	@Setter
	private int minRaise = PokerConfig.MIN_RAISE;

	public PokerAPI(int playerCount) throws InvalidPlayersNumberException {
		if(playerCount < PokerConfig.MIN_PLAYERS || playerCount > PokerConfig.MAX_PLAYERS) throw new InvalidPlayersNumberException();
		for(int i = 0; i < playerCount; i++)
			allPlayers.add(new Player(PokerConfig.START_STACK));
	}

	// GAME RELATED
	public void startGame() {
		if (gameStage != GameStage.NOTSTARTED)
			throw new IllegalStateException("Game already started");
		setSmallBlindIdx(-1);
		setCurrentPlayerIdx(-1);
		this.startHand();
	}

	void startHand() {
		setGameStage(GameStage.PREFLOP);
		currentPlayers = new ArrayList<>(allPlayers);
		currentPlayers.removeIf(p -> p.getState() == PlayerState.OUT);
		currentPlayers.forEach(Player::resetForNewHand);
		pot.clear();
		setCurrentBet(0);
		setLastAggressorIdx(-1);
		setMinRaise(PokerConfig.MIN_RAISE);
		table.clear();
		deck.shuffle();
		increaseSmallBlindIdx();
		dealer.dealRoles(smallBlindIdx);
		dealer.dealHoleCards(smallBlindIdx);
		takeMandatoryBets();
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
			case FLOP -> dealer.dealFlop();
			case TURN -> dealer.dealTurn();
			case RIVER -> dealer.dealRiver();
			case SHOWDOWN -> resolvePot();
		}
		advanceGameStage();
	}

	public void takeAction(PlayerAction action){
		if(gameStage.isBetStage()) throw new IllegalStateException("Not bet stage!");
		Player player = getCurrentPlayer();
		switch (action) {
			case RAISE:
				throw new IllegalArgumentException("RAISE action requires amount");
			case CHECK:
				if(currentBet != player.getCurrentBet()) throw new IllegalArgumentException("Cannot check — bet not matched");
				advanceTurn();
				break;
			case CALL:
				if(currentBet == player.getCurrentBet() || currentBet == 0) throw new IllegalArgumentException("Cannot call - Calling with same bet is checking!");
				int toCall = currentBet - player.getCurrentBet();
				if(toCall == player.getStack()) throw new IllegalArgumentException("Cannot call - Calling with all stack value requires all in!");
				player.bet(toCall);
				pot.add(toCall);
				advanceTurn();
				break;
			case FOLD:
				player.fold();
				advanceTurn();
				break;
			case ALL_IN:
				int amount = player.getStack();
				player.goAllIn();
				pot.add(amount);
				if (player.getCurrentBet() > currentBet) {
					setCurrentBet(player.getCurrentBet());
					setLastAggressorIdx(currentPlayerIdx);
				}
				advanceTurn();
				break;
		}
	}
	public void takeAction(PlayerAction action, int amount){
		if(gameStage.isBetStage()) throw new IllegalStateException("Not bet stage!");
		if(action != PlayerAction.RAISE) throw new IllegalArgumentException("Only RAISE action requires amount");
		if(amount < minRaise) throw new IllegalArgumentException("Amount must be at least " + minRaise);
		Player player = getCurrentPlayer();
		int toCall = currentBet - player.getCurrentBet();
		int total = toCall + amount;
		player.bet(total);
		pot.add(total);
		setCurrentBet(currentBet + amount);
		setMinRaise(amount);
		setLastAggressorIdx(currentPlayerIdx);
		advanceTurn();
	}


	void resolvePot(){
		int WinnerIdx = getWinnerIdx(); // Consider half pot
		currentPlayers.get(WinnerIdx).earn(pot.getValue());
		this.startHand();
	}

	int getWinnerIdx(){
		if(currentPlayers.size() == 1) return 0;

		int bestIdx = -1;
		int bestValue = -1;
		for(int idx = 0; idx < currentPlayers.size(); idx++){
			Player p = currentPlayers.get(idx);
			List<Card> sevenCards = new ArrayList<>();
			sevenCards.addAll(p.getHoleCards());
			sevenCards.addAll(table.getCards());
			int handValue = new HandResolver(sevenCards).getValue();
			if(bestIdx == -1 || handValue > bestValue){
				bestIdx = idx;
				bestValue = handValue;
			}
		}

		return bestIdx;
	}

	boolean allBetsMatched(){
		boolean all_bets_equal = true;
		for(Player player : currentPlayers){
			if(player.getState() == PlayerState.INGAME && player.getCurrentBet() != currentBet){
				all_bets_equal = false;
				break;
			}
		}
		return all_bets_equal;
	}

	// ACTIONS RELATED
	void advanceGameStage(){
		setGameStage(gameStage.next());
	}

	void increaseSmallBlindIdx(){
		setSmallBlindIdx((smallBlindIdx + 1) % currentPlayers.size());
	}


	private Player getCurrentPlayer(){
		return currentPlayers.get(currentPlayerIdx);
	}

	public void displayTable(){
		display.clearTerminal();
		display.printTableStateInTerminal(currentPlayerIdx, currentBet);
	}

}