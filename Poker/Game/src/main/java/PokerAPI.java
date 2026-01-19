import Enums.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PokerAPI {

	private static final int MIN_PLAYERS = 3;
	private static final int MAX_PLAYERS = 10;
	private static final int START_STACK = 500;
	private static final int MIN_BET = 5;
	private static final int BIG_BLIND_MULTIPLIER = 2;
	private static final int HOLE_CARDS = 2;
	private static final int FLOP_CARDS = 3;
	private final int BIG_BET = MIN_BET * BIG_BLIND_MULTIPLIER;

	private final List<Card> deck = new ArrayList<Card>();
	private final List<Card> table = new ArrayList<Card>();
	private final List<Player> allPlayers = new ArrayList<Player>();
	private List<Player> currentPlayers = new ArrayList<Player>();

	private GameStage gameStage = GameStage.NOTSTARTED;
	private int currentPlayerIdx = -1;
	private int smallBlindIdx = -1;
	private int currentBet = 0;
	private int pot = 0;
	private int lastAggressorIdx = -1;
	private int minRaise = MIN_BET * BIG_BLIND_MULTIPLIER;

	public PokerAPI(int playerCount) {
		if(playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS)
			throw new IllegalArgumentException("A poker game should not have less than " + MIN_PLAYERS + " players and we also don't accept more than " + MAX_PLAYERS + " players!");
		for(int i = 0; i < playerCount; i++) allPlayers.add(new Player(START_STACK));
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
		currentPlayers.forEach(p -> {
			p.clearBet();
			p.clearHand();
			p.setRole(PlayerRole.NONE);
		});
		setPot(0);
		setCurrentBet(0);
		setLastAggressorIdx(-1);
		setMinRaise(MIN_BET * BIG_BLIND_MULTIPLIER);
		table.clear();
		deck.clear();
		increaseSmallBlindIdx();
		createDeck();
		shuffleDeck();
		dealRoles();
		dealHoleCards();
		gameStage = GameStage.PREFLOP_BETS;
		takeMandatoryBets();
	}

	private void takeMandatoryBets(){
		getCurrentPlayer().bet(MIN_BET);
		addToPot(MIN_BET);
		advanceTurn();
		getCurrentPlayer().bet(BIG_BET);
		addToPot(BIG_BET);
		setCurrentBet(BIG_BET);
		advanceTurn();
	}

	void advanceTurn() {
		if (lastAggressorIdx == -1 && gameStage == GameStage.PREFLOP_BETS && getCurrentPlayer().getRole() == PlayerRole.BIGBLIND && allBetsMatched()) {
			advanceStreet();
			return;
		}
		do setCurrentPlayerIdx((currentPlayerIdx + 1) % currentPlayers.size());
		while (getCurrentPlayer().getState() != PlayerState.INGAME && currentPlayerIdx != lastAggressorIdx);
		if (
			(lastAggressorIdx != -1 && currentPlayerIdx == lastAggressorIdx && allBetsMatched()) ||
			(lastAggressorIdx == -1 && currentPlayerIdx == 0 && gameStage != GameStage.PREFLOP_BETS && allBetsMatched()
		)) {
			advanceStreet();
			return;
		}
	}

	void advanceStreet() {
		setCurrentBet(0);
		setMinRaise(MIN_BET);
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

	public void takeAction(PlayerAction action){
		if(!gameStage.isBetStage()) throw new IllegalStateException("Not bet stage!");
		Player player = getCurrentPlayer();
		switch (action) {
			case RAISE:
				throw new IllegalArgumentException("RAISE action requires amount");
			case CHECK:
				if(currentBet != player.getCurrentBet()) throw new IllegalArgumentException("Cannot check — bet not matched");
				advanceTurn();
				break;
			case CALL:
				int toCall = currentBet - player.getCurrentBet();
				if(toCall == player.getStack()) throw new IllegalArgumentException("Cannot call - Calling with all stack value requires all in!");
				player.bet(toCall);
				addToPot(toCall);
				advanceTurn();
				break;
			case FOLD:
				player.fold();
				advanceTurn();
				break;
			case ALL_IN:
				int amount = player.getStack();
				player.bet(amount);
				addToPot(amount);
				if (player.getCurrentBet() > currentBet) {
					setCurrentBet(player.getCurrentBet());
					setLastAggressorIdx(currentPlayerIdx);
				}
				player.setState(PlayerState.ALLIN);
				advanceTurn();
				break;
		}
	}
	public void takeAction(PlayerAction action, int amount){
		if(!gameStage.isBetStage()) throw new IllegalStateException("Not bet stage!");
		if(action != PlayerAction.RAISE) throw new IllegalArgumentException("Only RAISE action requires amount");
		if(amount < minRaise) throw new IllegalArgumentException("Amount must be at least " + minRaise);
		Player player = getCurrentPlayer();
		int toCall = currentBet - player.getCurrentBet();
		int total = toCall + amount;
		player.bet(total);
		addToPot(total);
		setCurrentBet(currentBet + amount);
		setMinRaise(amount);
		setLastAggressorIdx(currentPlayerIdx);
		advanceTurn();
	}


	void resolvePot(){

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

	void addToPot(int amount){
		setPot(pot + amount);
	}

	// ROLES RELATED
	private void dealRoles(){
		for (Player player : currentPlayers) player.setRole(PlayerRole.NONE);
		smallBlindIdx = (smallBlindIdx) % currentPlayers.size();
		setCurrentPlayerIdx(smallBlindIdx);
		currentPlayers.get(smallBlindIdx).setRole(PlayerRole.SMALLBLIND);
		currentPlayers.get((smallBlindIdx + 1) % currentPlayers.size()).setRole(PlayerRole.BIGBLIND);
	}

	// ACTIONS RELATED
	void advanceGameStage(){
		setGameStage(gameStage.next());
	}

	void increaseSmallBlindIdx(){
		setSmallBlindIdx((smallBlindIdx + 1) % currentPlayers.size());
	}

	// DECK RELATED
	private void createDeck(){
		for(SuitEnum suit :  SuitEnum.values()){
			for(RankEnum rank : RankEnum.values()){
				deck.add(new Card(rank,suit));
			}
		}
	}

	private void shuffleDeck(){
		Collections.shuffle(deck);
	}

	private void dealHoleCards(){
		int dealIdx = this.smallBlindIdx;
		for (int cardRound = 0; cardRound < HOLE_CARDS; cardRound++) {
			for (int i = 0; i < currentPlayers.size(); i++) {
				Card card = deck.removeFirst();
				currentPlayers.get(dealIdx).addCardToHand(card);
				dealIdx = (dealIdx + 1) % currentPlayers.size();
			}
		}
	}

	private void burnCard(){
		deck.removeFirst();
	}

	private void dealFlop(){
		this.burnCard();
		for(int i = 0; i < FLOP_CARDS; i++) table.add(deck.removeFirst());
	}

	private void dealTurn(){
		this.burnCard();
		table.add(deck.removeFirst());
	}

	private void dealRiver(){
		this.burnCard();
		table.add(deck.removeFirst());
	}

	private Player getCurrentPlayer(){
		return currentPlayers.get(currentPlayerIdx);
	}

	// DISPLAY RELATED
	public void printTableState() {
		System.out.println("=================================");
		System.out.println("TABLE");
		System.out.print("Community cards: ");
		if (table.isEmpty()) System.out.println("(none)");
		else {
			for (Card card : table) System.out.print(card.getShortLabel() + " ");
			System.out.println();
		}
		System.out.println("Pot value: " + pot);
		System.out.println("Current bet: " + currentBet);
		System.out.println("---------------------------------");
		System.out.println("PLAYERS");
		for (int i = 0; i < currentPlayers.size(); i++) {
			Player player = currentPlayers.get(i);
			System.out.print("Player " + (i + 1));
			if (player.getRole() != PlayerRole.NONE) System.out.print(" [" + player.getRole().getLabel() + "]");
			System.out.print(" -> Hand: ");
			if (player.getHand().isEmpty()) System.out.println("(no cards)");
			else {
				for (Card card : player.getHand()) System.out.print(card.getShortLabel() + " ");
			}
			System.out.print("- Bet: " + player.getCurrentBet() + " - Stack: " + player.getStack());
			if (currentPlayerIdx == i) System.out.print(" ( Current Player )");
			if(player.getState() == PlayerState.ALLIN) System.out.print(" ( ALLIN )");
			if(player.getState() == PlayerState.FOLD) System.out.print(" ( FOLD )");
			if(player.getState() == PlayerState.OUT) System.out.print(" ( OUT )");
			System.out.println();
		}
		System.out.println("=================================");
	}

}