package NeuralNetwork;

public class ActivationFunctionsHandler {

	public static double[][] ReLu(double[][] input){
		for(int i = 0; i < input.length; i++){
			for(int j = 0; j < input[0].length; j++) {
				input[i][j] = Math.max(0, input[i][j]);
			}
		}
		return input;
	}

	public static double[][] DReLu(double[][] input){
		for(int i = 0; i < input.length; i++){
			for(int j = 0; j < input[0].length; j++) {
				input[i][j] = (input[i][j] > 0) ? 1 : 0;
			}
		}
		return input;
	}

	public static double[][] Sigmoid(double[][] input){
		for(int i = 0; i < input.length; i++){
			for(int j = 0; j < input[0].length; j++) {
				input[i][j] = 1 / (1 + Math.exp(-input[i][j]));
			}
		}
		return input;
	}

	public static double[][] DSigmoid(double[][] input){
		double[][] sigmoidInput = Sigmoid(input);
		for(int i = 0; i < input.length; i++){
			for(int j = 0; j < input[0].length; j++) {
				input[i][j] = sigmoidInput[i][j] * (1 - sigmoidInput[i][j]);
			}
		}
		return input;
	}

}
