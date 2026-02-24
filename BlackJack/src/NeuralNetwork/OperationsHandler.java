package NeuralNetwork;

import NeuralNetwork.Enums.ActivationFunctions;
import NeuralNetwork.Enums.MatrixOperations;
import NeuralNetwork.Enums.ScalarOperations;

public class OperationsHandler {

	// SIMPLIFIED MATRIX OPERATIONS
	public static double[][] addMatrix(double[][] a, double[][] b){
		return MatrixOperation(a, b, MatrixOperations.SUM);
	}

	public static double[][] addScalar(double[][] a, double x){
		return ScalarOperation(a, x, ScalarOperations.ADD);
	}

	public static double[][] subtractMatrix(double[][] a, double[][] b){
		return MatrixOperation(a, b, MatrixOperations.SUBTRACT);
	}

	public static double[][] multiplyMatrix(double[][] a, double[][] b){
		return MatrixOperation(a, b, MatrixOperations.MULTIPLY);
	}

	public static double[][] multiplyHadamard(double[][] a, double[][] b){
		return MatrixOperation(a, b, MatrixOperations.HADAMARD);
	}

	public static double[][] multiplyScalar(double[][] a, double x){
		return ScalarOperation(a, x, ScalarOperations.MULTIPLY);
	}

	public static double[][] divideMatrix(double[][] a, double[][] b){
		return MatrixOperation(a, b, MatrixOperations.DIVIDE);
	}

	public static double[][] divideScalar(double[][] a, double x){
		return ScalarOperation(a, x, ScalarOperations.DIVIDE);
	}


	// OTHER MATRIX OPERATIONS
	public static double[][] sqrtMatrix(double[][] a){
		double[][] res = new double[a.length][a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				res[r][c] = Math.sqrt(a[r][c]);
			}
		}
		return res;
	}

	public static double[][] transpose(double[][] a){
		double[][] res = new double[a[0].length][a.length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				res[c][r] = a[r][c];
			}
		}
		return res;
	}


	// ACTIVATION FUNCTIONS
	public static double[][] relu(double[][] a){
		return ActivationFunction(a, ActivationFunctions.RELU);
	}

	public static double[][] dRelu(double[][] a){
		return ActivationFunction(a, ActivationFunctions.DRELU);
	}

	public static double[][] sigmoid(double[][] a){
		return ActivationFunction(a, ActivationFunctions.SIGMOID);
	}

	public static double[][] dSigmoid(double[][] a){
		return ActivationFunction(a, ActivationFunctions.DSIGMOID);
	}

	public static double[] softmax(double[] a){
		double max = a[0];
		for(double v : a) max = Math.max(max, v);

		double[] exponentials = new double[a.length];
		for (int i = 0; i < a.length; i++) exponentials[i] = Math.exp(a[i] - max);

		double sumOfExponentials = 0;
		for(double v : exponentials) sumOfExponentials += v;

		double[] probabilities = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			probabilities[i] = exponentials[i] / sumOfExponentials;
		}
		return probabilities;
	}

	// APPLICATION METHODS
	public static double[][] ScalarOperation(double[][] a, double x, ScalarOperations op){
		double[][] res = new double[a.length][a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				switch (op) {
					case MULTIPLY -> res[r][c] = x * a[r][c];
					case DIVIDE -> res[r][c] = a[r][c] / x;
					case ADD -> res[r][c] = x + a[r][c];
				}
			}
		}
		return res;
	}

	public static double[][] MatrixOperation(double[][] a, double[][] b, MatrixOperations op){
		double[][] res = new double[a.length][op == MatrixOperations.MULTIPLY? b[0].length : a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < (op == MatrixOperations.MULTIPLY? b[0].length : a[0].length); c++){
				switch (op){
					case MULTIPLY:
						res[r][c] = 0;
						for(int k = 0; k < a[0].length; k++){
							res[r][c] += a[r][k] * b[k][c];
						}
						break;
					case HADAMARD:
						res[r][c] = a[r][c] * b[r][c];
						break;
					case SUM:
						res[r][c] = a[r][c] + b[r][c];
						break;
					case SUBTRACT:
						res[r][c] = a[r][c] - b[r][c];
						break;
					case DIVIDE:
						res[r][c] = a[r][c] / b[r][c];
						break;
				}
			}
		}
		return res;
	}

	public static double[][] ActivationFunction(double[][] a, ActivationFunctions func){
		double[][] res = new double[a.length][a[0].length];
		double[][] sigmoidInput = null;
		if(func == ActivationFunctions.DSIGMOID) sigmoidInput = sigmoid(a);
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				switch (func) {
					case RELU -> res[r][c] = Math.max(0, a[r][c]);
					case DRELU -> res[r][c] = a[r][c] > 0 ? 1 : 0;
					case DSIGMOID -> res[r][c] = sigmoidInput[r][c] * (1 - sigmoidInput[r][c]);
					case SIGMOID -> res[r][c] = 1 / (1 + Math.exp(-a[r][c]));
				}
			}
		}
		return res;
	}

	public static double[][] deepCopy(double[][] a){
		double[][] res = new double[a.length][a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				res[r][c] = a[r][c];
			}
		}
		return res;
	}

}
