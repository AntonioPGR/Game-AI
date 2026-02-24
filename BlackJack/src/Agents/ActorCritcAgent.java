package Agents;

import BlackJackAPI.Models.GameState;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.OperationsHandler;

import java.util.Random;

public class ActorCritcAgent {

	public static final int HIT = 0;
	public static final int STAND = 1;

	private final double gamma = 0.95;

	private final double learningRate = 1e-3d;
	private final double momentum = 0.9d;
	private final double variance = 0.999d;

	private final Random rand = new Random();

	NeuralNetwork actorNetwork = new NeuralNetwork(3, 2, 2, 64); // Player total, isSoft, dealerCard
	NeuralNetwork criticNetwork = new NeuralNetwork(3, 2, 1, 64); // Player total, isSoft, dealerCard

	public ActorCritcAgent(){
		actorNetwork.setLearningRate(learningRate);
		actorNetwork.setMomentum(momentum);
		actorNetwork.setVariance(variance);

		criticNetwork.setLearningRate(learningRate);
		criticNetwork.setMomentum(momentum);
		criticNetwork.setVariance(variance);
	}

	public int chooseAction(GameState state){
		double[][] input = getStateInputs(state);
		double[] output = actorNetwork.forward(input)[0];
		double[] probs = OperationsHandler.softmax(output);
		return rand.nextDouble() < probs[HIT]? HIT : STAND;
	}

	public void update(GameState prevState, GameState nextState, int action, double reward, boolean done){
		double[][] prevInput = getStateInputs(prevState);
		double[][] nextInput = getStateInputs(nextState);

		double value = criticNetwork.forward(prevInput)[0][0];
		double nextValue = done? 0 : criticNetwork.forward(nextInput)[0][0];

		double target = reward + gamma * nextValue;
		criticNetwork.backward(prevInput, new double[][]{{target}});

		double tdError = reward + gamma * nextValue - value;

		double[] logits = actorNetwork.forward(prevInput)[0];
		double[] probs = OperationsHandler.softmax(logits);

		double[] oneHot = new double[2];
		oneHot[action] = 1.0;

		double[][] gradOutput = new double[1][2];

		for(int i = 0; i < 2; i++){
			gradOutput[0][i] = (oneHot[i] - probs[i]) * tdError;
		}

		actorNetwork.forward(prevInput);
		actorNetwork.backwardFromGradient(gradOutput);

	}

	// PRIVATE METHODS
	private double[][] getStateInputs(GameState state){
		double scaledPlayerSum = state.playerTotal() / 21.0;
		double scaledDealerCard = state.dealerCard() / 10.0;
		double scaledIsSoft = state.isSoft() ? 1.0 : 0.0;
		return new double[][]{{scaledPlayerSum, scaledDealerCard, scaledIsSoft}};
	}

}
