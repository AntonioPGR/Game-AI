package Agents;

import Agents.Enums.GameAction;
import BlackJackAPI.Models.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloAgent {

	// ACTIONS
	public final int HIT = 0;
	public final int STAND = 1;

	// OBSERVATION SPACE
	private double[][][][] Q = new double[22][2][11][2];
	List<GameAction> episode = new ArrayList<>();
	private int[][][][] N = new int[22][2][11][2];

	// EXPLORATION PARAMETERS
	public double epsilon = 1.0;      // Exploration rate
	public final double decayRate;
	public final double minEpsilon = 0.1;
	public final double explorationPercentage = 0.7;

	// RANDOM GENERATOR
	private final Random randomGenerator = new Random();

	public MonteCarloAgent(int totalSteps) {
		decayRate = (epsilon - minEpsilon) / ((double) totalSteps * explorationPercentage);
	}

	// PUBLIC
	public int chooseAction(GameState state) {
		int soft = getSoft(state.isSoft());
		double hitQ = Q[state.playerTotal()][soft][state.dealerCard()][HIT];
		double standQ = Q[state.playerTotal()][soft][state.dealerCard()][STAND];
		if (randomGenerator.nextDouble() < epsilon || (hitQ == standQ))
			return randomGenerator.nextBoolean() ? HIT : STAND;
		return hitQ > standQ? HIT : STAND;
	}

	public void updateTerminal(double reward) {
		for (GameAction action : episode) {
			int takenAction = action.action();
			int playerTotal = action.state().playerTotal();
			int dealerCard = action.state().dealerCard();
			int soft = getSoft(action.state().isSoft());

			N[playerTotal][soft][dealerCard][takenAction]++;
			int visits = N[playerTotal][soft][dealerCard][takenAction];
			Q[playerTotal][soft][dealerCard][takenAction] += (reward - Q[playerTotal][soft][dealerCard][takenAction]) / visits;
		}
		epsilon = Math.max(minEpsilon, epsilon - decayRate);
		clearEpisode();
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public void addAction(GameState state, int action) {
		episode.add(new GameAction(new GameState(state.playerTotal(), state.isSoft(), state.dealerCard()), action));
	}

	// PRIVATE METHODS
	private int getSoft(boolean isSoft) {
		return isSoft ? 1 : 0;
	}

	private void clearEpisode() {
		episode.clear();
	}

}
