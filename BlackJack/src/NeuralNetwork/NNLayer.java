package NeuralNetwork;

import java.util.Random;

public class NNLayer {
	// MATRIX MULTIPLICATION input x weights = output
	// [1 x s] x [s x n] = [1 x n]
	
	private double[][] weights; // [(input + 1) x neuronio] -> peso
	private double[][] input; // [1 x (input + 1)] -> entrada
	private double[][] output; // [1 x neuronio] -> saida
	private double[][] dWeights; // weights -= learningRate * dWeights

	private Random rand = new Random();

	private final int layer;

	public NNLayer(int inputs, int outputs, int id){
		this.layer = id;

		int size = 1 + inputs;
		this.weights = new double[size][outputs];
		this.input = new double[1][size];
		this.output = new double[1][outputs];
		this.dWeights = new double[size][outputs];

		for(int r = 0; r < weights.length; r++){
			for(int c = 0; c < weights[0].length; c++){
				weights[r][c] = (rand.nextDouble() - 0.5d) * 4d;
			}
		}
	}

}
