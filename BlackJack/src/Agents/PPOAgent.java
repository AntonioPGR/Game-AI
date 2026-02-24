package Agents;

import BlackJackAPI.Models.GameState;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.OperationsHandler;

import java.util.Random;

public class PPOAgent {

	public static final int HIT = 0;
	public static final int STAND = 1;

	NeuralNetwork actorNetwork = new NeuralNetwork(3, 2, 2, 64); // Player total, isSoft, dealerCard
	NeuralNetwork criticNetwork = new NeuralNetwork(3, 2, 1, 64); // Player total, isSoft, dealerCard

	int updateSteps = 512;

	public int bufferIndex = 0;
	GameState[] stateBuffer = new GameState[updateSteps];
	int[] actionBuffer = new int[updateSteps];
	double[] rewardBuffer = new double[updateSteps];
	double[] valueBuffer = new double[updateSteps];
	double[] log_probBuffer = new double[updateSteps];

	private final Random rand = new Random();

	private final double gamma = 0.99;
	private final double clipEpsilon = 0.2;
	private final int ppoEpochs = 5;

	private final double learningRate = 1e-3d;
	private final double momentum = 0.9d;
	private final double variance = 0.999d;

	public PPOAgent(){
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

		int action = rand.nextDouble() < probs[0] ? 0 : 1;

		return action;
	}

	public void update(boolean lastDone, GameState lastState){

		int batchSize = bufferIndex;
		if(batchSize == 0) return;

		// 1️⃣ Bootstrap value only when we have a following state
		double lastValue = 0;
		if(!lastDone && lastState != null){
			lastValue = criticNetwork.forward(getStateInputs(lastState))[0][0];
		}

		double[] advantages = new double[batchSize];
		double[] returns = new double[batchSize];

		// 2️⃣ Compute returns + advantages (simple TD version)
		double runningReturn = lastValue;
		for(int t = batchSize - 1; t >= 0; t--){
			runningReturn = rewardBuffer[t] + gamma * runningReturn;
			returns[t] = runningReturn;
			advantages[t] = returns[t] - valueBuffer[t];
		}

		// 3️⃣ Normalize advantages (VERY IMPORTANT)
		double mean = 0;
		for(double a : advantages) mean += a;
		mean /= batchSize;

		double std = 0;
		for(double a : advantages) std += Math.pow(a - mean, 2);
		std = Math.sqrt(std / batchSize + 1e-8);

		for(int i = 0; i < batchSize; i++){
			advantages[i] = (advantages[i] - mean) / std;
		}

		// 4️⃣ PPO Training epochs
		for(int epoch = 0; epoch < ppoEpochs; epoch++){

			for(int i = 0; i < batchSize; i++){

				double[][] input = getStateInputs(stateBuffer[i]);

				// ---- ACTOR ----
				double[] logits = actorNetwork.forward(input)[0];
				double[] probs = OperationsHandler.softmax(logits);

				double newLogProb = Math.log(probs[actionBuffer[i]]);
				double ratio = Math.exp(newLogProb - log_probBuffer[i]);

				double unclipped = ratio * advantages[i];
				double clipped = clamp(ratio, 1 - clipEpsilon, 1 + clipEpsilon) * advantages[i];

				double actorLossGrad = -Math.min(unclipped, clipped);

				double weight;

				if(unclipped < clipped)
					weight = -advantages[i] * ratio;
				else
					weight = -advantages[i] * clamp(ratio, 1 - clipEpsilon, 1 + clipEpsilon);

				double[][] actorGrad = new double[1][2];

				for(int a = 0; a < 2; a++){
					double indicator = (a == actionBuffer[i]) ? 1.0 : 0.0;
					actorGrad[0][a] = weight * (indicator - probs[a]);
				}

				actorNetwork.backwardFromGradient(actorGrad);

				// ---- CRITIC ----
				double value = criticNetwork.forward(input)[0][0];
				double criticGrad = value - returns[i];

				double[][] criticGradMatrix = {{criticGrad}};
				criticNetwork.backwardFromGradient(criticGradMatrix);
			}
		}

		bufferIndex = 0;
	}

	private double clamp(double v, double min, double max){
		return Math.max(min, Math.min(max, v));
	}

	public void storeTransition(GameState state, int action, double reward){

		double[][] input = getStateInputs(state);

		stateBuffer[bufferIndex] = state;
		actionBuffer[bufferIndex] = action;
		rewardBuffer[bufferIndex] = reward;

		double[] logits = actorNetwork.forward(input)[0];
		double[] probs = OperationsHandler.softmax(logits);
		log_probBuffer[bufferIndex] = Math.log(probs[action]);

		valueBuffer[bufferIndex] = criticNetwork.forward(input)[0][0];

		bufferIndex++;
	}

	public boolean isBufferFull(){
		return bufferIndex == updateSteps;
	}

	// PRIVATE METHODS
	private double[][] getStateInputs(GameState state){
		double scaledPlayerSum = state.playerTotal() / 21.0;
		double scaledDealerCard = state.dealerCard() / 10.0;
		double scaledIsSoft = state.isSoft() ? 1.0 : 0.0;
		return new double[][]{{scaledPlayerSum, scaledDealerCard, scaledIsSoft}};
	}

}
