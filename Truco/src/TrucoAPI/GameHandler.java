package TrucoAPI;

import TrucoAPI.Enums.GameState;
import TrucoAPI.Enums.ScoreType;
import TrucoAPI.Models.*;

import java.util.ArrayList;
import java.util.List;

public class GameHandler {

	private final List<Player> players;
	private final Deck deck;
	private final Printer printer = new Printer();

	private final ScoreBoard gameScoreBoard;
	private final ScoreBoard handScoreBoard;

	private GameState gameState;
	private int dealerIdx;
	private int currentPlayerIdx;
	private int handTurn = 0;
	private Card upcard;
	private List<Card> turnCards = new ArrayList<>();

	public GameHandler(){
		players = new ArrayList<Player>();
		for(int i = 1; i <= 4; i++) players.add(new Player(i));
		deck = new Deck();

		gameScoreBoard = new ScoreBoard(ScoreType.POINTS_LIMIT, 15);
		handScoreBoard = new ScoreBoard(ScoreType.BEST_OF, 3);

		gameState = GameState.NOT_STARTED;
		dealerIdx = 0;
		upcard = null;
	}

	// PUBLIC
	public void startGame(){
		if(gameState != GameState.NOT_STARTED && gameState != GameState.FINISHED) throw new IllegalStateException("Game already started");
		for(Player p : players) p.reset();
		handScoreBoard.reset();
		gameScoreBoard.reset();
		gameState = GameState.WAITING_HAND;
		dealerIdx = 0;
	}

	public void startHand(){
		if(gameState != GameState.WAITING_HAND) throw new IllegalStateException("Game is not waiting hand to start");
		gameState = GameState.IN_HAND;
		handScoreBoard.reset();
		handTurn = 0;
		deck.shuffle();
		turnCards.clear();
		for(int i = 1; i <= players.size() * 3; i++){
			players.get((dealerIdx + i) % players.size()).addCard(deck.draw());
		}
		currentPlayerIdx = (dealerIdx + 1) % players.size();
		upcard = deck.draw();
	}

	public void takeAction(Action action){
		if(gameState != GameState.IN_HAND) throw new IllegalStateException("Game is not in hand");
		switch(action.getAction()){
			case FOLD -> handleFold();
			case PLAY_CARD -> handlePlayCard(action);
			case CALL_TRUCO -> handleCallTruco();
		}
	}

	public void printTable(){
		printer.clearTerminal();
		printer.printTable(players, upcard, gameScoreBoard);
	}

	// PRIVATE
	private void handleFold(){
		if(currentPlayerIdx % 2 == 0) gameScoreBoard.scoreTeamA(1);
		else gameScoreBoard.scoreTeamB(1);
		if(gameScoreBoard.finished()) gameState = GameState.FINISHED;
		else gameState = GameState.WAITING_HAND;
	}

	private void handlePlayCard(Action action){

	}

	private void handleCallTruco(){}

}
