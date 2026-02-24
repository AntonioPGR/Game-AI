package NeuralNetwork;

import NeuralNetwork.Enums.ActivationFunctions;
import NeuralNetwork.Enums.MatrixOperations;

import java.util.Random;

public class NetworkLayer {
	// MATRIX MULTIPLICATION input x weights = output
	// [1 x s] x [s x n] = [1 x n]

	private double[][] weights; // [(input + 1) x neuronio] -> peso
	private final double[][] input; // [1 x (input + 1)] -> entrada
	private double[][] activatedOutput; // [1 x neuronio] -> saida
	private double[][] rawOutput;

	private double[][] dWeights = null; // weights -= learningRate * dWeights
	private double[][] mt = null;
	private double[][] vt = null;
	int timeStep = 0;

	private final ActivationFunctions activationFunction;

	private final Random rand = new Random();

	private final int layer;

	public NetworkLayer(
		int id,
		int inputs,
		int outputs,
		ActivationFunctions activationFunction
	) {
		this.layer = id;
		int size = 1 + inputs;
		this.weights = new double[size][outputs];
		this.input = new double[1][size];
		this.activatedOutput = new double[1][outputs];
		this.dWeights = new double[size][outputs];
		this.activationFunction = activationFunction;
		for(int r = 0; r < weights.length; r++){
			for(int c = 0; c < weights[0].length; c++){
				double std = Math.sqrt(2.0 / inputs);
				weights[r][c] = rand.nextGaussian() * std;
			}
		}
	}

	// input x weights = output
	public double[][] forward(double[][] newInput){
		for(int i = 0; i < newInput[0].length; i++){
			input[0][i] = newInput[0][i];
		}
		input[0][input[0].length - 1] = 1; // LAST ONE AS BIAS
		rawOutput = OperationsHandler.MatrixOperation(input, weights, MatrixOperations.MULTIPLY);
		switch (activationFunction){
			case RELU -> activatedOutput = OperationsHandler.relu(rawOutput);
			case SIGMOID -> activatedOutput = OperationsHandler.sigmoid(rawOutput);
			default -> activatedOutput = rawOutput;
		}

		return activatedOutput;
	}

	/*
	a = learning rate
	b1 = beta 1 (momentum)
	b2 = beta 2 (RMSProp)
	gt = dWeights (gradient of weights) = dL/dW
	mt = b1 * mt-1 + (1 - b1) * dWeights (momentum term) -> b1*mt-1 + dWeights - b1*dWeights
	vt = b2 * vt-1 + (1 - b2) * (dWeights^2) (variance term) -> b2*vt-1 + dWeights^2 - b2*dWeights^2
	mct = mt / (1 - b1^t) (bias-corrected momentum term)
	vct = vt / (1 - b2^t) (bias-corrected variance term)
	weights = weights - (mct * a) / (sqrt(vct) + epsilon) (update rule)
	*/
	public double[][] backward(double[][] errors, double a, double b1, double b2){
		timeStep++;
		double[][] oldWeights = OperationsHandler.deepCopy(weights);

		double[][] derivative;
		switch (activationFunction) {
			case SIGMOID -> derivative = OperationsHandler.dSigmoid(rawOutput);
			case RELU -> derivative = OperationsHandler.dRelu(rawOutput);
			default -> derivative = getOnesMatrix();
		}
		double[][] gt = OperationsHandler.multiplyMatrix(
			OperationsHandler.transpose(input),
			OperationsHandler.multiplyHadamard(derivative, errors)
		);

		mt = OperationsHandler.addMatrix(
			OperationsHandler.multiplyScalar(mt == null? new double[gt.length][gt[0].length] : mt, b1),
			OperationsHandler.subtractMatrix(gt, OperationsHandler.multiplyScalar(gt, b1))
		);
		double[][] mct = OperationsHandler.divideScalar(mt, 1 - Math.pow(b1, timeStep));

		double[][] gt2 = OperationsHandler.multiplyHadamard(gt, gt);
		vt = OperationsHandler.addMatrix(
			OperationsHandler.multiplyScalar(vt == null? new double[gt.length][gt[0].length] : vt, b2),
			OperationsHandler.subtractMatrix(gt2, OperationsHandler.multiplyScalar(gt2, b2))
		);
		double[][] vct = OperationsHandler.divideScalar(vt, 1 - Math.pow(b2, timeStep));

		weights = OperationsHandler.subtractMatrix(
			weights,
			OperationsHandler.divideMatrix(
				OperationsHandler.multiplyScalar(mct, a),
				OperationsHandler.addScalar(
					OperationsHandler.sqrtMatrix(vct),
					1e-8
				)
			)
		);

		double[][] delta = OperationsHandler.multiplyHadamard(derivative, errors);
		double[][] weightsNoBias = removeBiasRow(oldWeights);
		double[][] weightsT = OperationsHandler.transpose(weightsNoBias);
		double[][] nextError = OperationsHandler.multiplyMatrix(delta, weightsT);
		return nextError;

	}

	/*
	GRADIENT DESCENT ALGORITHM:
	public double[][] backward(double[][] errors, double learningRate, double momentum){
		double[][] oldWeights = OperationsHandler.deepCopy(weights);

		double[][] derivative;
		switch (activationFunction) {
			case SIGMOID -> derivative = OperationsHandler.ActivationFunction(rawOutput, ActivationFunctions.DSIGMOID);
			case RELU -> derivative = OperationsHandler.ActivationFunction(rawOutput, ActivationFunctions.DRELU);
			default -> {
				derivative = new double[rawOutput.length][rawOutput[0].length];
				for (int r = 0; r < derivative.length; r++) {
					for (int c = 0; c < derivative[0].length; c++) {
						derivative[r][c] = 1.0;
					}
				}
			}
		}
		double[][] delta = OperationsHandler.MatrixOperation(derivative, errors, MatrixOperations.HADAMARD);

		double[][] inputTransposed = OperationsHandler.MatrixTranspose(input);
		double[][] gradient = OperationsHandler.MatrixOperation(inputTransposed, delta, MatrixOperations.MULTIPLY);

		double[][] velocity = OperationsHandler.MatrixOperation(
			OperationsHandler.ScalarOperation(dWeights, momentum, ScalarOperations.MULTIPLY),
			OperationsHandler.ScalarOperation(gradient, learningRate, ScalarOperations.MULTIPLY),
			MatrixOperations.SUM
		);
		dWeights = velocity;
		weights = OperationsHandler.MatrixOperation(
			weights,
			velocity,
			MatrixOperations.SUBTRACT
		);

		double[][] weightsTransposed = OperationsHandler.MatrixTranspose(removeBiasRow(oldWeights));
		double[][] nextLayerError = OperationsHandler.MatrixOperation(delta, weightsTransposed, MatrixOperations.MULTIPLY);
		return nextLayerError;
	}
	*/

	// PRIVATE
	private double[][] getOnesMatrix(){
		double[][] res = new double[rawOutput.length][rawOutput[0].length];
		for (int r = 0; r < res.length; r++) {
			for (int c = 0; c < res[0].length; c++) {
				res[r][c] = 1.0;
			}
		}
		return res;
	}

	private double[][] removeBiasRow(double[][] internalOutputs){
		double[][] res = new double[internalOutputs.length-1][internalOutputs[0].length];
		for(int r = 0; r < res.length; r++){
			for(int c = 0; c < res[0].length; c++){
				res[r][c] = internalOutputs[r][c];
			}
		}
		return res;
	}


}
