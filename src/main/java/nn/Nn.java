package nn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Nn {

	public static void main(String[] args) throws Exception {
		run();

	}
	
	public static void run() throws Exception {
		
		HashMap<double[],double[]> realData = data();
		
		int inputSize = 4;
		int hiddenSize = 6;
		int outputSize =  3;
		double learningRate = 0.01;
		double loss;
		
		// actual rs -- t
		double[] actual = new double[outputSize];
		
		//input layer
		double[] input = new double[inputSize];
		//hidden layer -- y
		double[] hidden = new double[hiddenSize];
		//output layer -- z -> o
		double[] output = new double[outputSize];
		
		//weights between input and hidden
		double[][] ih = new double[inputSize][hiddenSize];
		
		//weights between hidden and output
		double[][] ho = new double[hiddenSize][outputSize];
		
		
		//random weight
		randomInitialze(ih);
		randomInitialze(ho);
		
		int ite = 0;
		
		while(ite < 1000) {
			loss = 0.0;
			for(double[] dArr : realData.keySet()) {
				input = dArr;
				actual = realData.get(dArr);
				
				// forward
				forward(input,ih,hidden);
				forward(hidden,ho,output);
				output = softmax(output);
				loss += crossEntropy(output, actual);
//				System.out.println("loss : " + loss);
				
				
				// backward
				for(int i = 0 ; i < ih.length ; ++i) {
					for(int j = 0 ; j < ih[0].length ; ++j) {
						ih[i][j] -= learningRate * input[i] * idk(output,actual,ho,j);
					}
				}
				
				for(int i = 0 ; i < ho.length ; ++i) {
					for(int j = 0 ; j < ho[0].length ; ++j) {
						ho[i][j] -= learningRate * hidden[i] * (output[j] - actual[j]);
					}
				}
				
			}
			System.out.println("iteration : " + ite + "\t->\tloss:" + loss);
			ite++;
		}
		
	}
	
	public static void printArr(double[] arr) {
		StringBuilder sb = new StringBuilder();
		for(double d : arr) {
			sb.append(d).append("   ");
		}
		System.out.println(sb.toString());
	}
	
	public static double idk(double[] predicted , double[] actual , double[][] ho , int noInY) {
		double rs = 0.0;
		for(int i = 0 ; i < actual.length ; ++i) {
			rs += ho[noInY][i] * (predicted[i] - actual[i]);
		}
		return rs;
	}
		
	public static void forward(double[] ip, double[][] ws, double[] op) {
		for(int i = 0 ; i < op.length ; ++i) {
			op[i] = 0.0;
			for(int j = 0 ; j < ip.length ; ++j) {
				op[i] += ip[j] * ws[j][i];
			}
		}
	}
	
	public static double[] softmax(double[] input) {
		double[] rs = new double[input.length];
		double sum = 0.0;
		for(double d : input) {
			sum += Math.exp(d);
		}
		for(int i = 0 ; i < input.length ; ++i) {
			rs[i] = Math.exp(input[i])/sum;
		}
		return rs;
	}
	
	public static double crossEntropy(double[] predicted , double[] actual) {
		double rs = 0.0;
		for(int i = 0 ; i < predicted.length ; ++i) {
			rs -= actual[i] * Math.log10(predicted[i]);
		}
		return rs;
	}
	
	public static void randomInitialze(double[][] matrix) {
		for(int i = 0 ; i < matrix.length ; ++i) {
			for(int j = 0 ; j < matrix[0].length ; ++j) {
				matrix[i][j] = Math.random();
			}
		}
	}
	
	public static HashMap<double[],double[]> data() throws Exception { 
	 	HashMap<double[],double[]> data = new HashMap<double[], double[]>(); 
		BufferedReader br = new BufferedReader(new FileReader("iris.txt"));
		String line = br.readLine();
		while(line != null) {
			String[] elements = line.split(",");
			double[] ip = new double[4];
			for(int i = 0 ; i < 4 ; ++i) {
				ip[i] = Double.parseDouble(elements[i]);
			}
			if(elements[4].equals("Iris-virginica")) {
				data.put(ip, new double[] {1.0,0.0,0.0});
			}else if (elements[4].equals("Iris-setosa")) {
				data.put(ip, new double[] {0.0,1.0,0.0});
			}else {
				data.put(ip, new double[] {0.0,0.0,1.0});
			}
			line = br.readLine();
		}
		br.close();
		return data;
	}

}
