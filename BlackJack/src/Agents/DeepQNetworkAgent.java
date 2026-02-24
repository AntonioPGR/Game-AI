package Agents;

import BlackJackAPI.Models.GameState;
import NeuralNetwork.NeuralNetwork;

import java.util.Random;

public class DeepQNetworkAgent {

	public static final int HIT = 0;
	public static final int STAND = 1;

	private final int inputSize = 3; // Player total, isSoft, dealerCard
	private final int outputSize = 2; // Hit or Stand
	private final int hiddenLayersAmount = 2;
	private final int hiddenLayersSize = 64;

	private final double gamma = 0.95; // Discount factor
	private double epsilon = 1.0;
	public final double decayRate;
	public final double explorationPercentage = 0.5;
	public final double minEpsilon = 0.05;

	private final double learningRate = 1e-3d;
	private final double momentum = 0.9d;
	private final double variance = 0.999d;

	private final NeuralNetwork qNetwork = new NeuralNetwork(inputSize, hiddenLayersAmount, outputSize, hiddenLayersSize);

	private final Random randomGenerator = new Random();

	public DeepQNetworkAgent(int totalSteps) {
		decayRate = (epsilon - minEpsilon) / ((double) totalSteps * explorationPercentage);

		qNetwork.setLearningRate(learningRate);
		qNetwork.setMomentum(momentum);
		qNetwork.setVariance(variance);
	}

	public int chooseAction(GameState state){
		if(randomGenerator.nextDouble() < epsilon) {
			return randomGenerator.nextBoolean()? HIT : STAND;
		}
		double[][] input = getStateInputs(state);
		double[][] qValues = qNetwork.forward(input);
		return qValues[0][HIT] > qValues[0][STAND] ? HIT : STAND;
	}

	public void update(GameState prevState, int action, double reward, GameState nextState, boolean done) {
		double[][] stateInput = getStateInputs(prevState);
		double[][] qValues = qNetwork.forward(stateInput);

		double target;
		if (done) {
			target = reward;
		} else {
			double[][] nextInput = getStateInputs(nextState);
			double[][] nextQ = qNetwork.forward(nextInput);
			double maxNextQ = Math.max(nextQ[0][0], nextQ[0][1]);
			target = reward + gamma * maxNextQ;
		}

		double[][] expectedOutput = {{ qValues[0][0], qValues[0][1] }};
		expectedOutput[0][action] = target;

		qNetwork.backward(stateInput, expectedOutput);
		epsilon = Math.max(minEpsilon, epsilon - decayRate);

	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	// PRIVATE METHODS
	private double[][] getStateInputs(GameState state){
		double scaledPlayerSum = state.playerTotal() / 21.0;
		double scaledDealerCard = state.dealerCard() / 10.0;
		double scaledIsSoft = state.isSoft() ? 1.0 : 0.0;
		return new double[][]{{scaledPlayerSum, scaledDealerCard, scaledIsSoft}};
	}

}
