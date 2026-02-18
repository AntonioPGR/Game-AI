package NeuralNetwork;

import java.util.Random;

public class NNLayer {
	// MATRIX MULTIPLICATION input x weights = output
	// [1 x s] x [s x n] = [1 x n]
	
	private double[][] weights; // [(input + 1) x neuronio] -> peso
	private double[][] input; // [1 x (input + 1)] -> entrada
	private double[][] activatedOutput; // [1 x neuronio] -> saida
	private double[][] rawOutput;

	private double[][] dWeights; // weights -= learningRate * dWeights

	private final ActivationFunctions activationFunction;
	private final double learningRate;
	private final double momentum;

	private Random rand = new Random();

	private final int layer;

	public NNLayer(
		int id,
		int inputs,
		int outputs,
		ActivationFunctions activationFunction,
		double learningRate,
		double momentum
	) {
		this.layer = id;
		int size = 1 + inputs;
		this.weights = new double[size][outputs];
		this.input = new double[1][size];
		this.activatedOutput = new double[1][outputs];
		this.dWeights = new double[size][outputs];
		this.activationFunction = activationFunction;
		this.learningRate = learningRate;
		this.momentum = momentum;
		for(int r = 0; r < weights.length; r++){
			for(int c = 0; c < weights[0].length; c++){
				weights[r][c] = (rand.nextDouble() - 0.5d) * 4d;
			}
		}
	}

	// input x weights = output
	public double[][] forward(double[][] newInput){
		input[0][0] = 1;
		for(int i = 0; i < newInput[0].length; i++){
			input[0][i+1] = newInput[0][i];
		}
		rawOutput = MatrixOperations.MatrixMatrixMultiply(input, weights);
		switch (activationFunction){
			case RELU -> activatedOutput = ActivationFunctionsHandler.ReLu(rawOutput);
			case SIGMOID -> activatedOutput = ActivationFunctionsHandler.Sigmoid(rawOutput);
			case NONE -> activatedOutput = rawOutput;
		}

		return activatedOutput;
	}

	public double[][] backward(double[][] errors){

		double[][] nextLayerError = errors;
		double[][] internalOutputs = activatedOutput;

	}

	// PRIVATE
	private double[][] nextLayerError(double[][] errors){
		double[][] result = new double[input.length][input[0].length];

		double[][] tempMatrix1 = ActivationFunctionsHandler.DSigmoid(rawOutput);
		tempMatrix1 = MatrixOperations.MatrixMatrixMultiply(tempMatrix1, errors);

		double[][] actualWeights = performAdjustimentByDecreasing(weights);
		double[][] tempMatrix2 = MatrixOperations.MatrixTranspose(actualWeights);
		tempMatrix1 = MatrixOperations.MatrixMatrixMultiply(tempMatrix1, tempMatrix2);

		return tempMatrix1;
	}

	private double[][] performAdjustimentByDecreasing(double[][] internalOutputs){
		double[][] res = new double[internalOutputs.length-1][internalOutputs[0].length];
		for(int r = 0; r < res.length; r++){
			for(int c = 0; c < res[0].length; c++){
				res[r][c] = internalOutputs[r][c];
			}
		}
		return res;
	}



}
