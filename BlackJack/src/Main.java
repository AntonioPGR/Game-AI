import Agents.MonteCarloAgent;
import Agents.SarsaAgent;
import BlackJackAPI.Enums.PlayerAction;
import BlackJackAPI.GameHandler;
import BlackJackAPI.Models.GameState;
import BlackJackAPI.Printer;

public class Main {

	public static int TRAIN_STEPS = 100_000_000;
	public static int TEST_STEPS = 100_000;
	public static GameHandler gameHandler = new GameHandler();
	public static MonteCarloAgent agent = new MonteCarloAgent(TRAIN_STEPS);

	public static void main(String[] args){
		trainAgent();
		evaluateAgent();
	}

	static void trainAgent(){
		Printer.printLn(">>>> TRAINING");
		int nextMilestone = 0;

		for (int i = 1; i <= TRAIN_STEPS; i++) {
			// PROGRESS LOG
			int progress = (i * 100) / TRAIN_STEPS;
			if (progress >= nextMilestone) {
				Printer.printLn(nextMilestone + "%");
				nextMilestone += 10;
			}

			// RUN GAME
			GameState state = null;
			int action = 0;

			gameHandler.startGame();
			while (!gameHandler.isGameEnded()) {
				state = gameHandler.getGameState();
				action = agent.chooseAction(state);
				agent.addAction(state, action);
				gameHandler.takeAction(action == agent.HIT ? PlayerAction.HIT : PlayerAction.STAND);
			}

			// EVALUATE REWARD
			int reward = 0;
			switch (gameHandler.getGameResult()){
				case PLAYER_WIN -> reward = 1;
				case DEALER_WIN -> reward = -1;
			}
			agent.updateTerminal(reward);
		}
	}

	static void evaluateAgent(){
		Printer.printLn(">>>> EVALUATION");
		int wins = 0, losses = 0, ties = 0;
		agent.setEpsilon(0.0);
		for(int i = 0; i < TEST_STEPS; i++){
			gameHandler.startGame();
			while (!gameHandler.isGameEnded()) {
				int action = agent.chooseAction(gameHandler.getGameState());
				gameHandler.takeAction(action == agent.HIT ? PlayerAction.HIT : PlayerAction.STAND);
			}
			switch (gameHandler.getGameResult()) {
				case PLAYER_WIN -> wins++;
				case DEALER_WIN -> losses++;
				default -> ties++;
			}
		}

		Printer.printLn(wins + "/" + losses + "/" + ties);
		Printer.printBreak();
		Printer.printLn("TRAIN_STEPS = " + TRAIN_STEPS);
		Printer.printBreak();
		Printer.printLn("Epsilon = " + 1);
		Printer.printLn("decayRate = " + agent.decayRate);
		Printer.printLn("minEpsilon = " + agent.minEpsilon);
		Printer.printLn("explorationPercentage = " + agent.explorationPercentage);

	}

}


