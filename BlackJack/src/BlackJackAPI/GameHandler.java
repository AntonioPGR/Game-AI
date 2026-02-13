package BlackJackAPI;

import BlackJackAPI.Enums.GameResult;
import BlackJackAPI.Enums.GameStage;
import BlackJackAPI.Enums.PlayerAction;
import BlackJackAPI.Enums.RankEnum;
import BlackJackAPI.Models.*;

import java.util.List;

public class GameHandler {

	private final Player player = new Player();
	private final Player dealer = new Player();

	private final Printer printer = new Printer();
	private final Deck deck = new Deck();

	private GameStage gameStage = GameStage.NOT_STARTED;
	private GameResult gameResult = GameResult.NONE;

	// PUBLIC
	public void startGame(){
		this.resetAttributes();

		// DISTRIBUTE CARDS
		for(int i = 0; i < 2; i++){
			player.addCard(deck.draw());
			dealer.addCard(deck.draw());
		}

		// CHECK FOR BLACKJACK
		int playerHandValue = evaluateHand(player.getCards()).value();
		int dealerHandValue = evaluateHand(dealer.getCards()).value();
		if(playerHandValue == 21){
			if(dealerHandValue == 21) endGame(GameResult.TIE);
			else endGame(GameResult.PLAYER_WIN);
		} else if(
			(dealer.getCards().getFirst().rank().getPower() == 10 || dealer.getCards().getFirst().rank().getPower() == 1) &&
			dealerHandValue == 21
		) endGame(GameResult.DEALER_WIN);

		// OPEN PLAYER TURN
		if(gameStage == GameStage.FINISHED) return;
		gameStage = GameStage.PLAYER_TURN;
	}

	public void takeAction(PlayerAction action){
		if(gameStage != GameStage.PLAYER_TURN) throw new IllegalStateException("Game is not in player's turn");
		switch(action){
			case HIT -> handleHit();
			case STAND -> handleStand();
		}
	}

	public boolean isGameEnded() {
		return gameStage == GameStage.FINISHED;
	}

	public void printTable(){
		printer.clearTerminal();
		printer.printTable(player, dealer, gameStage, gameResult);
	}

	public GameState getGameState(){
		Hand playerHand = evaluateHand(player.getCards());
		GameState state = new GameState(
			playerHand.value(),
			playerHand.soft(),
			dealer.getCards().getFirst().rank().getPower()
		);
		return state;
	}

	public GameResult getGameResult(){
		return gameResult;
	}

	// PRIVATE
	private void resetAttributes(){
		player.reset();
		dealer.reset();
		deck.shuffle();
		gameStage = GameStage.NOT_STARTED;
		gameResult = GameResult.NONE;
	}

	private void endGame(GameResult result){
		this.gameResult = result;
		this.gameStage = GameStage.FINISHED;
	}

	private void handleHit(){
		player.addCard(deck.draw());
		int handValue = evaluateHand(player.getCards()).value();
		if(handValue > 21) endGame(GameResult.DEALER_WIN);
		if(handValue == 21) playDealer();
	}

	private void handleStand(){
		playDealer();
	}

	private void playDealer(){
		// GET DEALER CARDS
		Hand dealerHand = evaluateHand(dealer.getCards());
		int playerHandValue = evaluateHand(player.getCards()).value();
		while (
			dealerHand.value() < 17 ||
			(dealerHand.value() == 17 && dealerHand.soft())
		) {
			dealer.addCard(deck.draw());
			dealerHand = evaluateHand(dealer.getCards());
		}
		// CHECK WINNER
		int dealerHandValue = dealerHand.value();
		if(dealerHandValue > 21) endGame(GameResult.PLAYER_WIN);
		else if(dealerHandValue > playerHandValue) endGame(GameResult.DEALER_WIN);
		else if(dealerHandValue < playerHandValue) endGame(GameResult.PLAYER_WIN);
		else endGame(GameResult.TIE);
	}

	private Hand evaluateHand(List<Card> hand) {
		int sum = 0;
		int aces = 0;
		for (Card card : hand) {
			if (card.rank() == RankEnum.ACE) {
				sum += 1;
				aces++;
			} else sum += card.rank().getPower();
		}
		boolean soft = false;
		while (aces > 0 && sum + 10 <= 21) {
			sum += 10;
			aces--;
			soft = true;
		}
		return new Hand(sum, soft);
	}

}
