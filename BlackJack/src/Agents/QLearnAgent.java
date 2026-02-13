package Agents;

import BlackJackAPI.Models.GameState;

import java.io.*;
import java.util.Random;

public class QLearnAgent {

	// ACTIONS
	public final int HIT = 0;
	public final int STAND = 1;

	// OBSERVATION SPACE
	private double[][][][] Q = new double[22][2][11][2];

	// LEARNING PARAMETERS
	private final double alpha = 0.01; // Learning rate
	private final double gamma = 1;  // Discount factor

	// EXPLORATION PARAMETERS
	private double epsilon = 1.0;      // Exploration rate
	private final double decayRate;
	private final double minEpsilon = 0.1;
	private final double explorationPercentage = 0.7;

	// RANDOM GENERATOR
	private final Random randomGenerator = new Random();

	public QLearnAgent(int totalSteps) {
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

	public void updateTerminal(GameState state, int action, double reward) {
		int soft = getSoft(state.isSoft());
		double oldQ = Q[state.playerTotal()][soft][state.dealerCard()][action];
		Q[state.playerTotal()][soft][state.dealerCard()][action] = oldQ + alpha * (reward - oldQ);
		epsilon = Math.max(minEpsilon, epsilon - decayRate);
	}

	public void updateStep(GameState prevState, int action, GameState newState) {
		int soft = prevState.isSoft() ? 1 : 0;
		int nextSoft = newState.isSoft() ? 1 : 0;
		double oldQ = Q[prevState.playerTotal()][soft][prevState.dealerCard()][action];
		double bestNext = Math.max(
			Q[newState.playerTotal()][nextSoft][newState.dealerCard()][HIT],
			Q[newState.playerTotal()][nextSoft][newState.dealerCard()][STAND]
		);
		Q[prevState.playerTotal()][soft][prevState.dealerCard()][action] = oldQ + alpha * (gamma * bestNext - oldQ);
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	// PRIVATE METHODS
	private int getSoft(boolean isSoft) {
		return isSoft ? 1 : 0;
	}

}
