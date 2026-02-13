package Agents;

import BlackJackAPI.Models.GameState;

import java.util.Random;

public class SarsaAgent {

	// ACTIONS
	public final int HIT = 0;
	public final int STAND = 1;

	// OBSERVATION SPACE
	private double[][][][] Q = new double[22][2][11][2];

	// LEARNING PARAMETERS
	public final double alpha = 0.01;
	public final double gamma = 1;

	// EXPLORATION PARAMETERS
	public double epsilon = 1.0;
	public final double decayRate;
	public final double minEpsilon = 0.1;
	public final double explorationPercentage = 0.7;

	// RANDOM GENERATOR
	private final Random randomGenerator = new Random();

	public SarsaAgent(int totalSteps) {
		decayRate = (epsilon - minEpsilon) / ((double) totalSteps * explorationPercentage);
	}

	// PUBLIC METHODS
	public int chooseAction(GameState state) {
		int soft = getSoft(state.isSoft());
		double hitQ = Q[state.playerTotal()][soft][state.dealerCard()][HIT];
		double standQ = Q[state.playerTotal()][soft][state.dealerCard()][STAND];
		if (randomGenerator.nextDouble() < epsilon || (hitQ == standQ))
			return randomGenerator.nextBoolean() ? HIT : STAND;
		return hitQ > standQ? HIT : STAND;
	}

	public void updateStep(GameState old_state, int old_action, GameState next_state, int next_action, double reward) {
		int old_soft = getSoft(old_state.isSoft());
		double oldQ = Q[old_state.playerTotal()][old_soft][old_state.dealerCard()][old_action];

		int next_soft = getSoft(next_state.isSoft());
		double nextQ = Q[next_state.playerTotal()][next_soft][next_state.dealerCard()][next_action];

		Q[old_state.playerTotal()][old_soft][old_state.dealerCard()][old_action] = oldQ + alpha * (reward + (gamma * nextQ) - oldQ);
	}

	public void updateTerminal(GameState state, int action, double reward) {
		int soft = getSoft(state.isSoft());
		double oldQ = Q[state.playerTotal()][soft][state.dealerCard()][action];
		Q[state.playerTotal()][soft][state.dealerCard()][action] = oldQ + alpha * (reward - oldQ);
		epsilon = Math.max(minEpsilon, epsilon - decayRate);
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	// PRIVATE METHODS
	private int getSoft(boolean isSoft) {
		return isSoft ? 1 : 0;
	}

}
