import Enums.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokerAPI {

	private static final int MIN_PLAYERS = 3;
	private static final int MAX_PLAYERS = 10;
	private static final int START_STACK = 500;
	private static final int MIN_BET = 5;
	private static final int BIG_BLIND_MULTIPLIER = 2;
	private static final int HOLE_CARDS = 2;
	private static final int FLOP_CARDS = 3;

	private final List<Card> deck = new ArrayList<Card>();
	private final List<Card> table = new ArrayList<Card>();
	private final List<Player> allPlayers = new ArrayList<Player>();
	private List<Player> currentPlayers = new ArrayList<Player>();


	private GameStage gameStage = GameStage.NOTSTARTED;
	private int currentPlayerIdx = 0;
	private int smallBlindIdx = 0;
	private int currentBet = 0;
	private int pot = 0;
	private int lastAggressorIdx = -1;
	private int minRaise = MIN_BET * BIG_BLIND_MULTIPLIER;

	public PokerAPI(int playerCount) {
		if(playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS)
			throw new IllegalArgumentException("A poker game should not have less than " + MIN_PLAYERS + " players and we also don't accept more than " + MAX_PLAYERS + " players!");
		for(int i = 0; i < playerCount; i++) allPlayers.add(new Player(START_STACK));
		gameStage = GameStage.NOTSTARTED;
	}

	// GAME RELATED
	public void startGame() {
		if (gameStage != GameStage.NOTSTARTED)
			throw new IllegalStateException("Game already started");
		this.startHand();
	}

	void startHand() {
		gameStage = GameStage.PREFLOP;
		currentPlayers = new ArrayList<>(allPlayers);
		currentPlayers.removeIf(p -> p.getState() == PlayerState.OUT);
		pot = 0;
		currentBet = 0;
		lastAggressorIdx = -1;
		minRaise = MIN_BET * BIG_BLIND_MULTIPLIER;
		table.clear();
		deck.clear();
		currentPlayers.forEach(p -> {
			p.clearBet();
			p.clearHand();
			p.setRole(PlayerRole.NONE);
		});
		smallBlindIdx = (smallBlindIdx + 1) % currentPlayers.size();
		createDeck();
		shuffleDeck();
		dealRoles();
		dealHoleCards();
		gameStage = GameStage.PREFLOP_BETS;
		takeMandatoryBets();
	}

	void advanceStreet() {
		currentBet = 0;
		minRaise = MIN_BET * BIG_BLIND_MULTIPLIER;
		lastAggressorIdx = -1;
		for (Player p : currentPlayers) p.clearBet();
		gameStage = gameStage.next();
		switch (gameStage) {
			case FLOP_BETS -> dealFlop();
			case TURN_BETS -> dealTurn();
			case RIVER_BETS -> dealRiver();
			case SHOWDOWN -> {/* showdown */}
		}
		currentPlayerIdx = smallBlindIdx;
	}

	void advanceTurn() {
		do {
			currentPlayerIdx = (currentPlayerIdx + 1) % currentPlayers.size();
		} while (currentPlayers.get(currentPlayerIdx).getState() != PlayerState.INGAME);

		if (lastAggressorIdx != -1 &&
			currentPlayerIdx == lastAggressorIdx &&
			allBetsMatched()) {
			advanceStreet();
		}
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
		pot += amount;
	}

	// ROLES RELATED
	private void dealRoles(){
		for (Player player : currentPlayers) player.setRole(PlayerRole.NONE);
		smallBlindIdx = (smallBlindIdx) % currentPlayers.size();
		currentPlayerIdx = smallBlindIdx;
		currentPlayers.get(smallBlindIdx).setRole(PlayerRole.SMALLBLIND);
		currentPlayers.get((smallBlindIdx + 1) % currentPlayers.size()).setRole(PlayerRole.BIGBLIND);
	}

	// ACTIONS RELATED
	private void takeMandatoryBets(){
		currentPlayers.get(currentPlayerIdx).bet(MIN_BET); // small blind
		addToPot(MIN_BET);
		advanceTurn();
		int bigBet = MIN_BET * BIG_BLIND_MULTIPLIER;
		currentPlayers.get(currentPlayerIdx).bet(bigBet); // big blind
		addToPot(MIN_BET * BIG_BLIND_MULTIPLIER);
		currentBet = bigBet;
		lastAggressorIdx = currentPlayerIdx;
		advanceTurn();
	}

	public void takeAction(PlayerAction action){
		if(!gameStage.isBetStage()) throw new IllegalStateException("Not bet stage!");
		Player player = currentPlayers.get(currentPlayerIdx);
		switch (action) {
			case RAISE:
				throw new IllegalArgumentException("RAISE action requires amount");
			case CHECK:
				if(currentBet != 0) throw new IllegalArgumentException("CHECK action requires current bet to be 0");
				advanceTurn();
				break;
			case CALL:
				int toCall = currentBet - player.getCurrentBet();
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
					currentBet = player.getCurrentBet();
					lastAggressorIdx = currentPlayerIdx;
				}
				player.setState(PlayerState.ALLIN);
				advanceTurn();
				break;
		}
	}
	public void takeAction(PlayerAction action, int amount){
		if(!gameStage.isBetStage()) throw new IllegalStateException("Not bet stage!");
		Player player = currentPlayers.get(currentPlayerIdx);
		if(action != PlayerAction.RAISE) throw new IllegalArgumentException("Only RAISE action requires amount");
		if(amount < minRaise) throw new IllegalArgumentException("Amount must be at least " + minRaise);
		int toCall = currentBet - player.getCurrentBet();
		int total = toCall + amount;
		player.bet(total);
		addToPot(total);
		currentBet += amount;
		minRaise = amount;
		lastAggressorIdx = currentPlayerIdx;
		advanceTurn();
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
			System.out.println();
		}
		System.out.println("=================================");
	}

}