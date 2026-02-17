package NeuralNetwork;

public class NeuralNetwork {

	private final int layersSize = 128;
	private final NNLayer[] layers;

	public NeuralNetwork(int inputs, int hiddenLayers, int outputs) {
		layers = new NNLayer[hiddenLayers + 1];
		layers[0] = new NNLayer(inputs, layersSize, 0);
		for(int i = 1; i < layersSize; i++){
			layers[i] = new NNLayer(layersSize, layersSize, i);
		}
		layers[hiddenLayers] = new NNLayer(layersSize, outputs, hiddenLayers);
	}

}
