package NeuralNetwork;

public class NeuralNetwork {

	private final int layersSize = 128;
	private final NNLayer[] layers;

	public NeuralNetwork(int inputs, int hiddenLayers, int outputs) {
		layers = new NNLayer[hiddenLayers + 1];
		for(int i = 0; i <= hiddenLayers; i++){
			int in = (i == 0) ? inputs : layersSize;
			int out = (i == hiddenLayers) ? outputs : layersSize;
			ActivationFunctions func = (i == hiddenLayers) ? ActivationFunctions.NONE : ActivationFunctions.RELU;
			layers[i] = new NNLayer(i, in, out, func, 0.1d, 0.1d);
		}
	}

	public double[][] forward(double[][] input){
		double[][] output = input;
		for(NNLayer layer : layers){
			output = layer.forward(output);
		}
		return output;
	}

	public void backward(double[][] input, double[][] expectedOutput){
		double[][] output = forward(input);
		double[][] errors = MatrixOperations.MatrixMatrixSubtract(output, expectedOutput);
		for(int i = layers.length - 1; i >= 0; i--){
			errors = layers[i].backward(errors);
		}
	}


}
