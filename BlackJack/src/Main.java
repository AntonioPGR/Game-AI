import Agents.ActorCritcAgent;
import Agents.PPOAgent;
import BlackJackAPI.Enums.PlayerAction;
import BlackJackAPI.GameHandler;
import BlackJackAPI.Models.GameState;
import BlackJackAPI.Printer;

public class Main {

	public static int TRAIN_STEPS = 1_000_000;
	public static int TEST_STEPS = 100_000;
	public static GameHandler gameHandler = new GameHandler();
	public static PPOAgent agent = new PPOAgent();

	public static void main(String[] args){
		trainAgent();
		evaluateAgent();
	}

	static void trainAgent(){
		Printer.printLn(">>>> TRAINING");

		for(int i = 0; i < TRAIN_STEPS; i++){

			if(i % (TRAIN_STEPS / 10) == 0)
				Printer.printLn((i * 100) / TRAIN_STEPS + "%");

			gameHandler.startGame();
			GameState state = gameHandler.getGameState();

			while (!gameHandler.isGameEnded()) {

				int action = agent.chooseAction(state);

				gameHandler.takeAction(
					action == 0 ? PlayerAction.HIT : PlayerAction.STAND
				);

				GameState nextState = gameHandler.getGameState();
				boolean done = gameHandler.isGameEnded();

				double reward = 0.0;
				if(done){
					switch (gameHandler.getGameResult()) {
						case PLAYER_WIN -> reward = 1.0;
						case DEALER_WIN -> reward = -1.0;
						default -> reward = 0.0;
					}
				}

				agent.storeTransition(state, action, reward);

				state = nextState;

				if(agent.isBufferFull()){
					agent.update(done, nextState);
				}
			}

			if(agent.bufferIndex > 0){
				agent.update(true, null);
			}
		}
	}

	static void evaluateAgent(){
		Printer.printLn(">>>> EVALUATION");
		int wins = 0, losses = 0, ties = 0;
//		agent.setEpsilon(0.0);
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
//		Printer.printLn("decayRate = " + agent.decayRate);
//		Printer.printLn("minEpsilon = " + agent.minEpsilon);
//		Printer.printLn("explorationPercentage = " + agent.explorationPercentage);

	}

}


