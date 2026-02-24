package NeuralNetwork;

import NeuralNetwork.Enums.ActivationFunctions;
import NeuralNetwork.Enums.MatrixOperations;

public class NeuralNetwork {

	private final int layersSize;

	private double learningRate = 1e-3d;
	private double momentum = 0.9d;
	private double variance = 0.999d;

	private final NetworkLayer[] layers;

	public NeuralNetwork(int inputs, int hiddenLayers, int outputs, int layersSize) {
		this.layersSize = layersSize;
		layers = new NetworkLayer[hiddenLayers + 1];
		for(int i = 0; i <= hiddenLayers; i++){
			int in = (i == 0) ? inputs : layersSize;
			int out = (i == hiddenLayers) ? outputs : layersSize;
			ActivationFunctions func = (i == hiddenLayers) ? ActivationFunctions.NONE : ActivationFunctions.RELU;
			layers[i] = new NetworkLayer(i, in, out, func);
		}
	}

	public double[][] forward(double[][] input){
		double[][] output = input;
		for(NetworkLayer layer : layers){
			output = layer.forward(output);
		}
		return output;
	}

	public void backward(double[][] input, double[][] expectedOutput){
		double[][] output = forward(input);
		double[][] errors = OperationsHandler.MatrixOperation(output, expectedOutput, MatrixOperations.SUBTRACT);
		for(int i = layers.length - 1; i >= 0; i--){
			errors = layers[i].backward(errors, learningRate, momentum, variance);
		}
	}

	public void backwardFromGradient(double[][] gradOutput){
		double[][] errors = gradOutput;
		for(int i = layers.length - 1; i >= 0; i--){
			errors = layers[i].backward(errors, learningRate, momentum, variance);
		}
	}

	// SETTERS
	public void setLearningRate(double learningRate){
		this.learningRate = learningRate;
	}
	public void setMomentum(double momentum){
		this.momentum = momentum;
	}
	public void setVariance(double variance){
		this.variance = variance;
	}

	// GETTERS
	public double getLearningRate(){
		return learningRate;
	}



}
