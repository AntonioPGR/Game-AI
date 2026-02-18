package NeuralNetwork;

public class MatrixOperations {

	// a [m x n]
	// b [n x p]
	// result [m x p]
	public static double[][] MatrixMatrixMultiply(double[][] a, double[][] b){
		if(a[0].length != b.length) throw new IllegalArgumentException("Invalid matrix dimensions for multiplication");
		double[][] result = new double[a.length][b[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < b[0].length; c++){
				result[r][c] = 0;
				for(int k = 0; k < a[0].length; k++){
					result[r][c] += a[r][k] * b[k][c];
				}
			}
		}
		return result;
	}

	public static double[][] MatrixScalarMultiply(double[][] a, double x){
		double[][] res = new double[a.length][a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				res[r][c] = x * a[r][c];
			}
		}
		return res;
	}

	public static double[][] MatrixMatrixSubtract(double[][] a, double[][] b){
		double[][] result = new double[a.length][a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				result[r][c] = a[r][c] - b[r][c];
			}
		}
		return result;
	}

	public static double[][] MatrixMatrixAdd(double[][] a, double[][] b){
		double[][] result = new double[a.length][a[0].length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				result[r][c] = a[r][c] + b[r][c];
			}
		}
		return result;
	}

	public static double[][] MatrixTranspose(double[][] a){
		double[][] result = new double[a[0].length][a.length];
		for(int r = 0; r < a.length; r++){
			for(int c = 0; c < a[0].length; c++){
				result[c][r] = a[r][c];
			}
		}
		return result;
	}


}
